package com.multi.matchon.team.service;

import com.multi.matchon.chat.domain.ChatRoom;
import com.multi.matchon.chat.repository.ChatRoomRepository;
import com.multi.matchon.chat.service.ChatService;
import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.exception.custom.CustomException;
import com.multi.matchon.common.service.NotificationService;
import com.multi.matchon.team.domain.Review;
import com.multi.matchon.team.dto.res.ResJoinRequestDetailDto;
import com.multi.matchon.team.dto.res.ResJoinRequestDto;
import com.multi.matchon.team.repository.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.multi.matchon.common.domain.*;

import com.multi.matchon.common.dto.res.PageResponseDto;
import com.multi.matchon.common.repository.AttachmentRepository;
import com.multi.matchon.common.repository.PositionsRepository;
import com.multi.matchon.common.repository.SportsTypeRepository;

import com.multi.matchon.common.util.AwsS3Utils;
import com.multi.matchon.matchup.domain.MatchupBoard;
import com.multi.matchon.matchup.dto.res.ResMatchupBoardListDto;
import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.repository.MemberRepository;
import com.multi.matchon.team.domain.*;
import com.multi.matchon.team.dto.req.ReqReviewDto;
import com.multi.matchon.team.dto.req.ReqTeamDto;
import com.multi.matchon.team.dto.req.ReqTeamJoinDto;
import com.multi.matchon.team.dto.res.ResReviewDto;
import com.multi.matchon.team.dto.res.ResTeamDto;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.connection.RedisListCommands;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeamService {
    private final ClientHttpRequestFactorySettings clientHttpRequestFactorySettings;
    //@Value("${spring.cloud.aws.s3.base-url}")
    private String S3_URL;
    private String FILE_DIR = "attachments/";
    private String FILE_URL;

    @Value("${spring.cloud.aws.s3.base-url}")
    private String S3BaseUrl;

    @PostConstruct
    public void init() {
        this.S3_URL = S3BaseUrl; // ✅ Proper value assignment
        this.FILE_URL = S3_URL;
    }


    private final TeamNameRepository teamRepository;

    private final RecruitingPositionRepository recruitingPositionRepository;

    private final PositionsRepository positionsRepository;
    private final AttachmentRepository attachmentRepository;

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;
    private final ResponseRepository responseRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final NotificationService notificationService;

    @PersistenceContext
    private EntityManager em;

    private final AwsS3Utils awsS3Utils;


    public List<Team> findAll() {
        List<Team> teamBoards = teamRepository.findAllNotDeleted();


        return teamBoards;
    }


    public void teamRegister(ReqTeamDto reqTeamDto, CustomUser user) {


        Member member = memberRepository.findByMemberEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다: " + user.getUsername()));


        if (member.getTeam() != null) {
            throw new IllegalArgumentException("이미 소속된 팀이 있습니다.");
        }

        boolean exists = teamRepository.existsByTeamNameAndIsDeletedFalse(reqTeamDto.getTeamName());
        if (exists) {
            throw new IllegalArgumentException("이미 존재하는 팀 이름입니다.");
        }

        if (reqTeamDto.getRecruitmentStatus() == null) {
            throw new IllegalArgumentException("모집 여부를 선택해야 합니다.");
        }

        if (reqTeamDto.getTeamRegion() == null) {
            throw new IllegalArgumentException("팀 지역은 필수입니다.");
        }

        if (reqTeamDto.getRecruitingPositions() == null || reqTeamDto.getRecruitingPositions().isEmpty()) {
            throw new IllegalArgumentException("한 개 이상의 포지션을 선택해야 합니다.");
        }



        // Check if the user already has an active team

        List<PositionName> positions = reqTeamDto.getRecruitingPositions().stream()
                .map(pos -> {
                    System.out.println("⛳ Parsing position string: [" + pos + "]");

                    try {
                        return PositionName.valueOf(pos.trim());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("포지션 정보 오류: " + pos);
                    }
                })
                .collect(Collectors.toList());


        Team newTeam = Team.builder()
                .teamName(reqTeamDto.getTeamName())
                .teamRegion(RegionType.valueOf(reqTeamDto.getTeamRegion()))
                .teamRatingAverage(reqTeamDto.getTeamRatingAverage())
                .recruitmentStatus(reqTeamDto.getRecruitmentStatus()).teamIntroduction(reqTeamDto.getTeamIntroduction())
                .teamAttachmentEnabled(true)
                .createdPerson(member.getMemberEmail())
                .build();
        Team savedTeam = teamRepository.save(newTeam);


        // ⬇️ Only create chat room if team has none (safety against duplication) ⬇️
        if (savedTeam.getChatRoom() == null) {
            String identifierChatRoomName = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            ChatRoom teamChatRoom = ChatRoom.builder()
                    .isGroupChat(true)
                    .chatRoomName("Team Chat - " + savedTeam.getTeamName() + " - " + identifierChatRoomName)
                    .build();

            chatRoomRepository.save(teamChatRoom);

            savedTeam.setChatRoom(teamChatRoom);
            teamRepository.save(savedTeam); // update team with linked room

            chatService.addParticipantToRoom(teamChatRoom, member); // add leader
        }else {
            // ✅ Fallback: just add participant to existing chat room
            chatService.addParticipantToRoom(savedTeam.getChatRoom(), member);
        }


        // Add creator as team member (leader)

        TeamMember teamMember = TeamMember.builder()
                .team(savedTeam)
                .member(member)
                .introduction("팀 리더입니다.")
                .teamLeaderStatus(true)
                .build();

        teamMemberRepository.save(teamMember);

        em.createQuery("UPDATE Member m SET m.team = :team WHERE m.memberEmail = :email")
                .setParameter("team", savedTeam)
                .setParameter("email", user.getUsername())
                .executeUpdate();

        for (PositionName positionName : positions) {
            Positions position = positionsRepository.findByPositionName(positionName)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid position name: " + positionName));

            RecruitingPosition rp = RecruitingPosition.builder()
                    .team(savedTeam)
                    .positions(position)
                    .build();

            recruitingPositionRepository.save(rp);
        }

        insertFile(reqTeamDto.getTeamImageFile(), savedTeam);

    }

    private void insertFile(MultipartFile multipartFile, Team team) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            log.warn("⚠️ No image file uploaded for team '{}'", team.getTeamName());
            return;
        }

        String fileName = UUID.randomUUID().toString().replace("-", "");
        awsS3Utils.saveFile(FILE_DIR, fileName, multipartFile);

        Attachment attachment = Attachment.builder()
                .boardType(BoardType.TEAM)
                .boardNumber(team.getId())
                .fileOrder(0)
                .originalName(multipartFile.getOriginalFilename())
                .savedName(fileName+multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf(".")))
                .savePath(FILE_DIR)
                .build();

        attachmentRepository.save(attachment);
    }

    public void updateFile(MultipartFile multipartFile, Team findTeamBoard) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            log.warn("⚠️ No file provided for team update.");
            return;
        }

        String fileName = UUID.randomUUID().toString().replace("-", "");
        String extension = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        String fullSavedName = fileName + extension;

        List<Attachment> findAttachments = attachmentRepository.findAllByBoardTypeAndBoardNumber(BoardType.TEAM, findTeamBoard.getId());

        if (findAttachments.isEmpty()) {
            // 📌 No file yet: insert new attachment
            Attachment newAttachment = Attachment.builder()
                    .boardType(BoardType.TEAM)
                    .boardNumber(findTeamBoard.getId())
                    .fileOrder(0)
                    .originalName(multipartFile.getOriginalFilename())
                    .savedName(fullSavedName)
                    .savePath(FILE_DIR)
                    .build();

            awsS3Utils.saveFile(FILE_DIR, fileName, multipartFile);
            attachmentRepository.save(newAttachment);
            return;
        }

        // ♻️ Existing file: update it
        Attachment attachment = findAttachments.get(0);
        String oldFileName = attachment.getSavedName();

        attachment.update(
                multipartFile.getOriginalFilename(),
                fullSavedName,
                FILE_DIR
        );

        attachmentRepository.save(attachment);

        awsS3Utils.deleteFile(FILE_DIR, oldFileName);
        awsS3Utils.saveFile(FILE_DIR, fileName, multipartFile);
    }

    public PageResponseDto<ResTeamDto> findAllWithPaging(
            PageRequest pageRequest,
            String recruitingPosition,
            String region,
            Double teamRatingAverage,
            Boolean recruitmentStatus) {

        log.info("📌 teamRatingAverage = {}", teamRatingAverage);
        log.info("📌 recruitmentStatus = {}", recruitmentStatus);

        // ✅ Convert enums safely
        PositionName positionName = null;
        if (recruitingPosition != null && !recruitingPosition.isBlank()) {
            positionName = PositionName.valueOf(recruitingPosition.trim());
        }

        RegionType regionType = null;
        if (region != null && !region.isBlank()) {
            regionType = RegionType.valueOf(region.trim());
        }

        // ✅ 🔀 Use correct query based on presence of rating filter
        Page<Team> teamPage;
        if (teamRatingAverage == null) {
            log.info("📤 Calling findWithoutRatingFilter() — no 별점 filter");
            teamPage = teamRepository.findWithoutRatingFilter(positionName, regionType, recruitmentStatus, pageRequest);
        } else {
            log.info("📤 Calling findWithRatingFilter() — rating filter = {}", teamRatingAverage);
            teamPage = teamRepository.findWithRatingFilter(positionName, regionType, teamRatingAverage, recruitmentStatus, pageRequest);
        }

        // ✅ Transform to DTOs with image handling
        Page<ResTeamDto> dtoPage = teamPage.map(team -> {
            Optional<Attachment> attachment = attachmentRepository.findLatestAttachment(BoardType.TEAM, team.getId());
            String imageUrl = attachment
                    .map(att -> awsS3Utils.createPresignedGetUrl(att.getSavePath(), att.getSavedName()))
                    .orElse("/img/default-team.png");

            double avgRating = calculateAverageRating(team.getId());
            return ResTeamDto.from(team, imageUrl, avgRating);
        });

        return PageResponseDto.<ResTeamDto>builder()
                .items(dtoPage.getContent())
                .pageInfo(PageResponseDto.PageInfoDto.builder()
                        .page(dtoPage.getNumber())
                        .size(dtoPage.getNumberOfElements())
                        .totalElements(dtoPage.getTotalElements())
                        .totalPages(dtoPage.getTotalPages())
                        .isFirst(dtoPage.isFirst())
                        .isLast(dtoPage.isLast())
                        .build())
                .build();
    }


    public ResTeamDto findTeamById(Long teamId) {
        Team team = teamRepository.findByIdNotDeleted(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다: " + teamId));

        Optional<Attachment> attachment = attachmentRepository.findLatestAttachment(BoardType.TEAM, team.getId());
        String imageUrl = attachment
                .map(att -> awsS3Utils.createPresignedGetUrl(att.getSavePath(), att.getSavedName()))
                .orElse("/img/default-team.png");

        double avgRating = calculateAverageRating(team.getId());
        return ResTeamDto.from(team, imageUrl, avgRating);
    }

    @Transactional
    public void processTeamJoinRequest(Long teamId, ReqTeamJoinDto joinRequest, CustomUser user) {
        Team team = teamRepository.findByIdNotDeleted(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다: " + teamId));

        if (!team.getRecruitmentStatus()) {
            throw new IllegalArgumentException("현재 팀원 모집이 진행중이지 않습니다.");
        }

        Member member = memberRepository.findByMemberEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다: " + user.getUsername()));

        if (member.getTeam() != null) {
            throw new IllegalArgumentException("이미 다른 팀에 소속되어 있습니다.");
        }

        Positions userPosition = member.getPositions(); // from Member entity
        if (userPosition == null) {
            throw new IllegalArgumentException("회원님의 포지션 정보가 설정되지 않았습니다.");
        }

        boolean isValidPosition = team.getRecruitingPositions().stream()
                .anyMatch(rp -> rp.getPositions().getId().equals(userPosition.getId())); // safer comparison by ID

        if (!isValidPosition) {
            throw new IllegalArgumentException("회원님의 포지션은 해당 팀에서 모집 중인 포지션이 아닙니다.");
        }


        // Create team member with pending status
        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .member(member)
                .introduction(joinRequest.getIntroduction())
                .teamLeaderStatus(false)
                .build();

        teamMemberRepository.save(teamMember);
    }

    @Transactional
    public void saveReview(Long teamId, CustomUser user, ReqReviewDto dto) {
        log.info("Attempting to save review for team {} by user {}", teamId, user.getUsername());
        
        // Find the member who is writing the review
        Member member = memberRepository.findByMemberEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
        log.info("Found member with ID: {}", member.getId());

        // Find the team
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다: " + teamId));
        // Create and save the review
        Review review = Review.builder()
                .member(member)
                .team(team)
                .reviewRating(dto.getRating())
                .content(dto.getContent())
                .isDeleted(false)
                .build();

        // Save the review
        Review savedReview = reviewRepository.save(review);
        log.info("Successfully saved review with ID: {}", savedReview.getId());

        double newAvgRating = calculateAverageRating(teamId); // You probably have this
        team.updateRating(newAvgRating); // ← add this method to Team entity
    }


    @Transactional(readOnly = true)
    public List<ResReviewDto> getReviewsForTeam(Long teamId) {
        return reviewRepository.findReviewsByTeamId(teamId)
                .stream()
                .map(review -> {
                    Optional<Response> response = responseRepository.findByReviewAndIsDeletedFalse(review);
                    return ResReviewDto.from(review, response.orElse(null), attachmentRepository, awsS3Utils);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateReview(Long reviewId, CustomUser user, ReqReviewDto dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // Check if the user is the owner of the review
        if (!review.getMember().getMemberEmail().equals(user.getUsername())) {
            throw new IllegalArgumentException("리뷰 작성자만 수정할 수 있습니다.");
        }

        // Update review
        review.updateReview(dto.getRating(), dto.getContent());
    }

    @Transactional
    public void deleteReview(Long reviewId, CustomUser user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // Check if the user is the owner of the review
        if (!review.getMember().getMemberEmail().equals(user.getUsername())) {
            throw new IllegalArgumentException("리뷰 작성자만 삭제할 수 있습니다.");
        }

        // Soft delete the review
        review.softDelete();
    }

    @Transactional(readOnly = true)
    public List<ResReviewDto> getMyReviewsForTeam(Long teamId, String userEmail) {
        return reviewRepository.findReviewsByTeamId(teamId).stream()
                .filter(r -> r.getMember().getMemberEmail().equals(userEmail))
                .map(review -> {
                    Optional<Response> response = responseRepository.findByReviewAndIsDeletedFalse(review);
                    return ResReviewDto.from(review, response.orElse(null), attachmentRepository, awsS3Utils);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void sendJoinRequest(Long teamId, CustomUser user, ReqTeamJoinDto joinDto) {
        Team team = teamRepository.findByIdNotDeleted(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        Member member = memberRepository.findByMemberEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        if (member.getTeam() != null) {
            throw new IllegalArgumentException("이미 소속된 팀이 있습니다.");
        }

        // ✅ Word count check
        String intro = joinDto.getIntroduction();
        if (intro != null && intro.trim().split("\\s+").length > 200) {
            throw new IllegalArgumentException("자기소개는 최대 200단어까지 입력할 수 있습니다.");
        }

        // ✅ Change starts here
        // ✅ Strict check: any past request blocks a new one
        boolean alreadyRequested = teamJoinRequestRepository.existsByMemberAndTeam(member, team);
        if (alreadyRequested) {
            throw new IllegalArgumentException("이미 요청한 팀입니다.");
        }

        TeamJoinRequest joinRequest = TeamJoinRequest.builder()
                .member(member)
                .team(team)
                .joinRequestStatus(Status.PENDING)
                .isDeleted(false)
                .introduction(intro)
                .build();

        teamJoinRequestRepository.save(joinRequest);

        // find the team leader
        Member leader = teamMemberRepository.findByTeamAndTeamLeaderStatusTrue(team)
                .orElseThrow(() -> new IllegalStateException("팀 리더를 찾을 수 없습니다."))
                .getMember(); // 👈 FIX HERE

        String message = "[팀 가입 신청] " + member.getMemberName() + "님이 팀에 가입 신청했습니다.";
        String url = "/team/team/" + team.getId() + "/join-request/" + joinRequest.getId();

        notificationService.sendNotification(leader, message, url);
    }
    @Transactional(readOnly = true)
    public List<ResJoinRequestDto> getJoinRequestsForTeam(Long teamId, CustomUser user) {
        Team team = teamRepository.findByIdNotDeleted(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        // Optional: check if user is team leader
        Member currentUser = memberRepository.findByMemberEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        boolean isLeader = teamMemberRepository.existsByTeamAndMemberAndTeamLeaderStatusTrue(team, currentUser);
        if (!isLeader) {
            throw new IllegalArgumentException("팀 리더만 신청 목록을 조회할 수 있습니다.");
        }

        List<TeamJoinRequest> requests = teamJoinRequestRepository.findByTeamIdAndJoinRequestStatus(teamId, Status.PENDING);

        return requests.stream()
                .map(ResJoinRequestDto::from)
                .collect(Collectors.toList());
    }
    @Transactional
    public void approveJoinRequest(Long requestId) {
        TeamJoinRequest request = teamJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        if (request.getJoinRequestStatus() != Status.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        Member member = request.getMember(); // 가입 신청한 사용자

        // 🚫 이미 팀에 속해 있는 경우
        if (member.getTeam() != null) {

            // ✅ 팀 리더 찾기
            TeamMember leader = teamMemberRepository.findByTeamAndTeamLeaderStatusTrue(request.getTeam())
                    .orElseThrow(() -> new IllegalStateException("팀 리더를 찾을 수 없습니다."));

            // ❗ 알림을 보낼 수도 있음 (선택)
            notificationService.sendNotification(
                    leader.getMember(), // 팀장에게 알림 전송
                    "[거절 자동 처리] " + member.getMemberName() + "님은 이미 다른 팀에 속해 있어 요청이 거절되었습니다.",
                    "/team/team/" + request.getTeam().getId()
            );

            // 예외로 컨트롤러에 메시지 전달
            throw new IllegalStateException("이미 팀이 있는 사용자 입니다.");
        }


        request.approved();


        Team team = request.getTeam();       // 해당 팀


        TeamMember newMember = TeamMember.builder()
                .member(request.getMember())
                .team(request.getTeam())
                .introduction("팀장 승인됨")
                .teamLeaderStatus(false)
                .build();

        teamMemberRepository.save(newMember);

        // ✅ member 테이블에 team_id 업데이트
        member.setTeam(team);
        memberRepository.save(member); // 명시적으로 저장 (선택사항이지만 안전)

        Member applicant = request.getMember();
        String message = "[가입 승인] " + team.getTeamName() + "팀 가입이 승인되었습니다.";
        String url = "team/team/" + team.getId(); // or /my-team if you redirect them to their team page

        notificationService.sendNotification(applicant, message, url);


        ChatRoom teamGroupChatRoom = Optional.ofNullable(team.getChatRoom())
                .filter(ChatRoom::getIsGroupChat)
                .orElseThrow(() -> new IllegalStateException("해당 팀의 채팅방이 존재하지 않습니다."));

        chatService.addParticipantToRoom(teamGroupChatRoom, member); // 👈 add member to chat

    }

    @Transactional
    public void rejectJoinRequest(Long requestId) {
        TeamJoinRequest request = teamJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        if (request.getJoinRequestStatus() != Status.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        request.denied();
        Member applicant = request.getMember();
        Team team = request.getTeam();

        String message = "[가입 거절] " + team.getTeamName() + "팀 가입 신청이 거절되었습니다.";
        String url = "team/team/" + team.getId(); // optional: could just send them to team list

        notificationService.sendNotification(applicant, message, url);
    }

    @Transactional(readOnly = true)
    public boolean isTeamLeader(Long teamId, String userEmail) {
        Member member = memberRepository.findByMemberEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        return teamMemberRepository.existsByTeamAndMemberAndTeamLeaderStatusTrue(
                teamRepository.findByIdNotDeleted(teamId).orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다.")),
                member
        );
    }

    @Transactional
    public void deleteTeam(Long teamId, CustomUser user) {

        Team team = teamRepository.findByIdNotDeleted(teamId)
                .orElseThrow(() -> new IllegalArgumentException("삭제된 팀이거나 존재하지 않습니다."));
        Member member = memberRepository.findByMemberEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        boolean isLeader = teamMemberRepository.existsByTeamAndMemberAndTeamLeaderStatusTrue(team, member);
        if (!isLeader) throw new IllegalArgumentException("팀 리더만 삭제할 수 있습니다.");

        team.softDelete(); // if you support soft delete

        // 2. Break association: remove team from all members
        List<Member> teamMembers = memberRepository.findAllByTeam(team);
        for (Member m : teamMembers) {
            m.setTeam(null); // Break the link
        }
        memberRepository.saveAll(teamMembers);


        teamRepository.save(team);
    }


    @Transactional(readOnly = true)
    public ReqTeamDto getTeamEditForm(Long teamId, CustomUser user) {
        Team team = teamRepository.findByIdNotDeleted(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        Member member = memberRepository.findByMemberEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        boolean isLeader = teamMemberRepository.existsByTeamAndMemberAndTeamLeaderStatusTrue(team, member);
        if (!isLeader) {
            throw new IllegalArgumentException("팀 리더만 수정할 수 있습니다.");
        }

        List<String> recruitingPositionNames = team.getRecruitingPositions().stream()
                .map(rp -> rp.getPositions().getPositionName().name())
                .collect(Collectors.toList());

        return ReqTeamDto.builder()
                .teamId(team.getId())
                .teamName(team.getTeamName())
                .teamIntroduction(team.getTeamIntroduction())
                .teamRegion(team.getTeamRegion().name())
                .teamRatingAverage(team.getTeamRatingAverage())
                .recruitmentStatus(team.getRecruitmentStatus())
                .recruitingPositions(recruitingPositionNames)
                // Note: teamImageFile is not set here because it's a file upload, not stored in entity
                .build();
    }
    @Transactional
    public void updateTeam(ReqTeamDto dto, CustomUser user) {
        if (dto.getTeamId() == null) {
            throw new IllegalArgumentException("팀 ID가 없습니다.");
        }


        if (dto.getRecruitingPositions() == null || dto.getRecruitingPositions().isEmpty()) {
            throw new IllegalArgumentException("한 개 이상의 포지션을 선택해야 합니다.");
        }


        Team team = teamRepository.findByIdNotDeleted(dto.getTeamId())

                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        if (!team.getTeamName().equals(dto.getTeamName())) {
            boolean exists = teamRepository.existsByTeamNameAndIsDeletedFalse(dto.getTeamName());
            if (exists) {
                throw new IllegalArgumentException("이미 존재하는 팀 이름입니다.");
            }
        }

        Member member = memberRepository.findByMemberEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        boolean isLeader = teamMemberRepository.existsByTeamAndMemberAndTeamLeaderStatusTrue(team, member);
        if (!isLeader) {
            throw new IllegalArgumentException("팀 리더만 수정할 수 있습니다.");
        }

        // Update fields
        team.updateInfo(dto.getTeamName(), dto.getTeamIntroduction(),
                RegionType.valueOf(dto.getTeamRegion()), dto.getTeamRatingAverage(), dto.getRecruitmentStatus());

        // Remove and re-insert recruiting positions
        recruitingPositionRepository.deleteByTeam(team);

        em.flush();



        for (String posName : dto.getRecruitingPositions()) {
            PositionName enumValue = PositionName.valueOf(posName.trim());
            Positions position = positionsRepository.findByPositionName(enumValue)
                    .orElseThrow(() -> new IllegalArgumentException("포지션 정보 오류: " + posName));

            RecruitingPosition rp = RecruitingPosition.builder()
                    .team(team)
                    .positions(position)
                    .build();

            recruitingPositionRepository.save(rp);
        }

        // Update image if necessary
        if (dto.getTeamImageFile() != null && !dto.getTeamImageFile().isEmpty()) {
            updateFile(dto.getTeamImageFile(), team);
        }
    }
    //리뷰 답변 쓰기

    @Transactional
    public void writeReviewResponse(Long reviewId, String content, CustomUser user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));

        Team team = review.getTeam();
        Member leader = memberRepository.findByMemberEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        if (!teamMemberRepository.existsByTeamAndMemberAndTeamLeaderStatusTrue(team, leader)) {
            throw new IllegalArgumentException("팀 리더만 답변을 작성할 수 있습니다.");
        }

        if (responseRepository.existsByReviewAndIsDeletedFalse(review)) {
            throw new IllegalArgumentException("이미 답변이 작성된 리뷰입니다.");
        }

        Response response = Response.builder()
                .review(review)
                .reviewResponse(content)
                .build();

        responseRepository.save(response);
    }

    //답변 존재여부 상관 없이 모든 리뷰 보기
    @Transactional(readOnly = true)
    public List<ResReviewDto> getAllReviewsWithResponses(Long teamId, CustomUser user) {
        Team team = teamRepository.findByIdNotDeleted(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));

        Member leader = memberRepository.findByMemberEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        if (!teamMemberRepository.existsByTeamAndMemberAndTeamLeaderStatusTrue(team, leader)) {
            throw new IllegalArgumentException("팀 리더만 볼 수 있습니다.");
        }

        List<Review> reviews = reviewRepository.findReviewsByTeamId(teamId);

        return reviews.stream()
                .map(r -> {
                    Optional<Response> response = responseRepository.findByReviewAndIsDeletedFalse(r);
                    return ResReviewDto.from(r, response.orElse(null), attachmentRepository, awsS3Utils);
                })
                .collect(Collectors.toList());
    }

    //답변 존재하는 리뷰 보기
    @Transactional(readOnly = true)
    public List<ResReviewDto> getAnsweredReviews(Long teamId, CustomUser user) {
        Team team = teamRepository.findByIdNotDeleted(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));

        Member leader = memberRepository.findByMemberEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        if (!teamMemberRepository.existsByTeamAndMemberAndTeamLeaderStatusTrue(team, leader)) {
            throw new IllegalArgumentException("팀 리더만 볼 수 있습니다.");
        }

        return reviewRepository.findReviewsByTeamId(teamId).stream()
                .map(r -> responseRepository.findByReviewAndIsDeletedFalse(r)
                        .map(resp -> ResReviewDto.from(r, resp, attachmentRepository, awsS3Utils))
                        .orElse(null))
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }

    public Long getTeamIdByReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        return review.getTeam().getId();
    }


    //답변 수정하기
    @Transactional
    public void updateReviewResponse(Long responseId, String updatedText, CustomUser user) {
        // 1. Fetch the response
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new CustomException("답변을 찾을 수 없습니다."));

        // 2. Check that the user is the leader of the team related to the review
        Review review = response.getReview();
        Team team = review.getTeam();

        // Fetch the TeamMember entry for this user and team
        TeamMember teamMember = teamMemberRepository.findByMember_IdAndTeam_Id(
                user.getMember().getId(), team.getId()
        ).orElseThrow(() -> new CustomException("팀 멤버 정보를 찾을 수 없습니다."));

        if (!teamMember.getTeamLeaderStatus()) {
            throw new AccessDeniedException("팀장이 아니므로 답변을 수정할 권한이 없습니다.");
        }

        // 3. Update the content and timestamp
        response.updateContent(updatedText);

        // 4. Save (if not using dirty checking)
        responseRepository.save(response);
    }


    //답변 삭제
    @Transactional
    public void deleteResponse(Long responseId, CustomUser user) {
        // 1. Find the response entity
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new CustomException("답변을 찾을 수 없습니다."));

        // 2. Get the associated team from the review linked to this response
        Review review = response.getReview();
        Team team = review.getTeam();

        // 3. Get member info from CustomUser
        Member member = user.getMember();

        // 4. Check if this member is the team leader
        TeamMember teamMember = teamMemberRepository.findByMember_IdAndTeam_Id(member.getId(), team.getId())
                .orElseThrow(() -> new CustomException("팀 멤버 정보를 찾을 수 없습니다."));

        if (!Boolean.TRUE.equals(teamMember.getTeamLeaderStatus())) {
            throw new AccessDeniedException("팀 리더만 답변을 삭제할 수 있습니다.");
        }

        // 5. Perform deletion
        responseRepository.delete(response);
    }

    public ResJoinRequestDetailDto getJoinRequestDetail(Long requestId, CustomUser user) {
        TeamJoinRequest joinRequest = teamJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("가입 요청을 찾을 수 없습니다."));

        Team team = joinRequest.getTeam();
        Member currentUser = memberRepository.findByMemberEmailAndIsDeletedFalse(user.getUsername())
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        // ✅ Check if the user is the leader using existing method
        boolean isLeader = teamMemberRepository.existsByTeamAndMemberAndTeamLeaderStatusTrue(team, currentUser);
        if (!isLeader) {
            throw new AccessDeniedException("팀 리더만 가입 요청 상세를 볼 수 있습니다.");
        }

        Member requester = joinRequest.getMember();


        Attachment attachment = em.createQuery(
                        "SELECT a FROM Attachment a WHERE a.boardType = :boardType AND a.boardNumber = :boardNumber AND a.isDeleted = false ORDER BY a.fileOrder ASC",
                        Attachment.class)
                .setParameter("boardType", BoardType.MEMBER)
                .setParameter("boardNumber", requester.getId())
                .setMaxResults(1)
                .getResultStream() // avoids NoResultException
                .findFirst()
                .orElse(null);

        String profileImageUrl = attachment != null
                ? awsS3Utils.createPresignedGetUrl(attachment.getSavePath(), attachment.getSavedName())
                : "/img/default-avatar.png";


        return ResJoinRequestDetailDto.builder()
                .requestId(joinRequest.getId())
                .applicantId(requester.getId()) // ✅ Inject applicant's ID
                .nickname(requester.getMemberName())
                .position(
                        requester.getPositions() != null
                                ? translatePosition(requester.getPositions().getPositionName())
                                : "미정"
                )
                .temperature(requester.getMyTemperature())
                .preferredTime(
                        requester.getTimeType() != null
                                ? translateTimeType(requester.getTimeType())
                                : "미정"
                )
                .introduction(joinRequest.getIntroduction())

                .profileImageUrl(profileImageUrl) // ✅ Make sure this exists!

                .build();
    }

    private String translatePosition(PositionName positionName) {
        if (positionName == null) return "미정";
        return switch (positionName) {
            case GOALKEEPER -> "골키퍼";
            case CENTER_BACK -> "센터백";
            case LEFT_RIGHT_BACK -> "좌/우 풀백";
            case LEFT_RIGHT_WING_BACK -> "좌/우 윙백";
            case CENTRAL_DEFENSIVE_MIDFIELDER -> "수비형 미드필더";
            case CENTRAL_MIDFIELDER -> "중앙 미드필더";
            case CENTRAL_ATTACKING_MIDFIELDER -> "공격형 미드필더";
            case LEFT_RIGHT_WING -> "좌/우 윙";
            case STRIKER_CENTER_FORWARD -> "스트라이커";
            case SECOND_STRIKER -> "세컨드 스트라이커";
            case LEFT_RIGHT_WINGER -> "좌/우 윙어";
        };
    }


    private String translateTimeType(TimeType timeType) {
        if (timeType == null) return "미정";
        return switch (timeType) {
            case WEEKDAY_MORNING -> "평일 오전";
            case WEEKDAY_AFTERNOON -> "평일 오후";
            case WEEKDAY_EVENING -> "평일 저녁";
            case WEEKEND_MORNING -> "주말 오전";
            case WEEKEND_AFTERNOON -> "주말 오후";
            case WEEKEND_EVENING -> "주말 저녁";
        };
    }


    public ResTeamDto findMyTeam(CustomUser user) {
        Member member = memberRepository.findByMemberEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        Team team = member.getTeam();
        if (team == null) {
            throw new IllegalArgumentException("소속된 팀이 없습니다."); // This will trigger the alert
        }
        Optional<Attachment> attachment = attachmentRepository.findLatestAttachment(BoardType.TEAM, team.getId());

        String imageUrl = attachment
                .map(att -> awsS3Utils.createPresignedGetUrl(att.getSavePath(), att.getSavedName()))
                .orElse("/img/default-team.png"); // fallback if no image

        double avgRating = calculateAverageRating(team.getId());
        return ResTeamDto.from(team, imageUrl, avgRating);
    }



    public double calculateAverageRating(Long teamId) {
        List<Review> reviews = reviewRepository.findReviewsByTeamId(teamId);
        if (reviews.isEmpty()) return 0.0;

        double avg = reviews.stream()
                .mapToInt(Review::getReviewRating)
                .average()
                .orElse(0.0);

        return Math.round(avg * 10) / 10.0; // Round to 1 decimal place
    }


    public List<ResTeamDto> findAllWithoutPaging() {
        List<Team> teams = teamRepository.findAll();

        return teams.stream().map(team -> {
            Optional<Attachment> attachment = attachmentRepository.findLatestAttachment(BoardType.TEAM, team.getId());
            String imageUrl = attachment
                    .map(att -> awsS3Utils.createPresignedGetUrl(att.getSavePath(), att.getSavedName()))
                    .orElse("/img/default-team.png");

            double avgRating = calculateAverageRating(team.getId());
            return ResTeamDto.from(team, imageUrl, avgRating);
        }).collect(Collectors.toList());
    }

    public PageResponseDto<ResReviewDto> getPagedReviews(Long teamId, PageRequest pageRequest) {
        Page<Review> page = reviewRepository.findByTeamId(teamId, pageRequest);

        List<ResReviewDto> dtoList = page.getContent().stream()
                .map(review -> {
                    Optional<Response> response = responseRepository.findByReviewAndIsDeletedFalse(review);
                    return ResReviewDto.from(review, response.orElse(null), attachmentRepository, awsS3Utils);
                })
                .toList();

        PageResponseDto.PageInfoDto pageInfo = PageResponseDto.PageInfoDto.builder()
                .page(page.getNumber())
                .size(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();

        return new PageResponseDto<>(dtoList, pageInfo); // ✅ now correct
    }
    public int countPendingJoinRequests(Long teamId) {
        return teamJoinRequestRepository.countPendingByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<ResJoinRequestDto> getJoinRequestsForTeam(Long teamId, CustomUser user, PageRequest pageRequest) {
        Team team = teamRepository.findByIdNotDeleted(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        Member currentUser = memberRepository.findByMemberEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        boolean isLeader = teamMemberRepository.existsByTeamAndMemberAndTeamLeaderStatusTrue(team, currentUser);
        if (!isLeader) {
            throw new IllegalArgumentException("팀 리더만 신청 목록을 조회할 수 있습니다.");
        }

        Page<TeamJoinRequest> requests = teamJoinRequestRepository.findByTeam(team, pageRequest);

        Page<ResJoinRequestDto> dtoPage = requests.map(ResJoinRequestDto::from);

        return PageResponseDto.<ResJoinRequestDto>builder()
                .items(dtoPage.getContent())
                .pageInfo(PageResponseDto.PageInfoDto.builder()
                        .page(dtoPage.getNumber())
                        .size(dtoPage.getNumberOfElements())
                        .totalElements(dtoPage.getTotalElements())
                        .totalPages(dtoPage.getTotalPages())
                        .isFirst(dtoPage.isFirst())
                        .isLast(dtoPage.isLast())
                        .build())
                .build();
    }
}

//    public PageResponseDto<ResTeamDto> findAllWithPaging(
//            PageRequest pageRequest,
//            String recruitingPosition,
//            String region,
//            Double teamRatingAverage) {
//
//        // Convert enums safely
//        PositionName positionName = null;
//        if (recruitingPosition != null && !recruitingPosition.isBlank()) {
//            positionName = PositionName.valueOf(recruitingPosition.trim());
//        }
//
//        RegionType regionType = null;
//        if (region != null && !region.isBlank()) {
//            regionType = RegionType.valueOf(region.trim());
//        }
//
//

//        Page<Team> teamPage = teamRepository.findTeamListWithPaging(

//                positionName, regionType, teamRatingAverage, pageRequest);
//
//
//        Page<ResTeamDto> dtoPage = teamPage.map(ResTeamDto::from);
//
//        return PageResponseDto.<ResTeamDto>builder()
//                .items(dtoPage.getContent())
//                .pageInfo(PageResponseDto.PageInfoDto.builder()
//                        .page(dtoPage.getNumber())
//                        .size(dtoPage.getNumberOfElements())
//                        .totalElements(dtoPage.getTotalElements())
//                        .totalPages(dtoPage.getTotalPages())
//                        .isFirst(dtoPage.isFirst())
//                        .isLast(dtoPage.isLast())
//                        .build())
//                .build();
//    }

//}

