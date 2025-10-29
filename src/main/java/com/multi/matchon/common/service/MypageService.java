package com.multi.matchon.common.service;

import com.multi.matchon.common.domain.*;
import com.multi.matchon.common.jwt.repository.RefreshTokenRepository;
import com.multi.matchon.common.repository.AttachmentRepository;
import com.multi.matchon.common.repository.PositionsRepository;
import com.multi.matchon.common.util.AwsS3Utils;
import com.multi.matchon.event.domain.HostProfile;
import com.multi.matchon.event.repository.HostProfileRepository;
import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.domain.MemberRole;
import com.multi.matchon.common.domain.Positions;
import com.multi.matchon.member.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MypageService {

    private final PositionsRepository positionsRepository;
    private final HostProfileRepository hostProfileRepository;
    private final AwsS3Utils awsS3Utils;
    private final AttachmentRepository attachmentRepository;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @PersistenceContext
    private EntityManager em;

    @Value("${spring.cloud.aws.s3.base-url}")
    private String S3BaseUrl;

    private static final String PROFILE_DIR = "attachments/profile/";

    public Map<String, Object> getMypageInfo(Member member) {
        Map<String, Object> data = new HashMap<>();

        data.put("memberRole", member.getMemberRole() != null ? member.getMemberRole().name() : "UNKNOWN");
        data.put("memberName", member.getMemberName());
        data.put("myTemperature", member.getMyTemperature());
        data.put("teamName", member.getTeam() != null ? member.getTeam().getTeamName() : "팀이 없습니다");

        if (MemberRole.HOST.equals(member.getMemberRole())) {
            hostProfileRepository.findByMember(member).ifPresent(host -> {
                data.put("hostName", host.getHostName());
            });
        }


        Optional<Attachment> profileAttachment = attachmentRepository.findLatestAttachment(BoardType.MEMBER, member.getId());

        boolean isDefaultProfile = profileAttachment.isEmpty();
        String imageUrl = profileAttachment
                .map(att -> awsS3Utils.createPresignedGetUrl(PROFILE_DIR, att.getSavedName()))
                .orElse("/img/common-profile.png");

        data.put("profileImageUrl", imageUrl);
        data.put("isDefaultProfile", isDefaultProfile);

        return data;
    }

    public void updateHostName(Member member, String newHostName) {
        if (hostProfileRepository.findByHostName(newHostName).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 기관명입니다.");
        }

        HostProfile profile = hostProfileRepository.findByMember(member)
                .orElseGet(() -> {
                    HostProfile newProfile = HostProfile.builder()
                            .member(member)
                            .hostName(newHostName)
                            .build();
                    return hostProfileRepository.save(newProfile);
                });

        profile.setHostName(newHostName);
        hostProfileRepository.save(profile);
    }

    public void uploadProfileImage(Member member, MultipartFile file) {

        // 확장자 검사
        String ext = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        if (!List.of("jpg", "jpeg", "png").contains(ext)) {
            throw new IllegalArgumentException("jpg, jpeg, png 파일만 업로드할 수 있습니다.");
        }

        // 1. 기존 첨부 파일이 있으면 삭제
        Optional<Attachment> existingAttachmentOpt =
                attachmentRepository.findLatestAttachment(BoardType.MEMBER, member.getId());

        existingAttachmentOpt.ifPresent(att -> {
            awsS3Utils.deleteFile(att.getSavePath(), att.getSavedName());
            attachmentRepository.delete(att); // DB에서도 삭제
        });

        // 2. 새 파일 저장
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String savedName = uuid + "." + ext;

        awsS3Utils.saveFile(PROFILE_DIR, uuid, file); // 확장자 내부에서 붙임

        Attachment attachment = Attachment.builder()
                .boardType(BoardType.MEMBER)
                .boardNumber(member.getId())
                .fileOrder(0)
                .originalName(file.getOriginalFilename())
                .savedName(savedName)
                .savePath(PROFILE_DIR)
                .build();

        attachmentRepository.save(attachment);
    }

    @Transactional
    public void updateMypage(String email, PositionName positionName, TimeType timeType) {
        Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Positions position = positionsRepository.findByPositionName(positionName)
                .orElseThrow(() -> new IllegalArgumentException("포지션 없음"));

        member.setPositions(position);
        member.setTimeType(timeType);
        //member.setMyTemperature(temperature);

        memberRepository.saveAndFlush(member);
        em.clear();
    }

    @Transactional
    public void withdraw(Member member) {
        // 토큰 삭제
        refreshTokenRepository.deleteByMember(member);

        // 프로필 이미지 soft delete + S3 삭제
        attachmentRepository.findLatestAttachment(BoardType.MEMBER, member.getId())
                .ifPresent(att -> {
                    awsS3Utils.deleteFile(att.getSavePath(), att.getSavedName());
                    att.delete(true); // isDeleted = true로 소프트 삭제
                    attachmentRepository.save(att); // 저장
                });

        // 개인정보 초기화
        member.clearPersonalInfo(); // 이름, 온도, 팀, 포지션, 프로필사진정보 등 null 처리

        // 탈퇴 처리
        member.markAsDeleted(); // is_deleted = true
    }

    // 프로필 이미지 삭제
    public void deleteProfileImage(Member member) {
        attachmentRepository.findLatestAttachment(BoardType.MEMBER, member.getId())
                .ifPresent(att -> {
                    awsS3Utils.deleteFile(att.getSavePath(), att.getSavedName());
                    att.delete(true); // 소프트 삭제
                    attachmentRepository.save(att);
                });
    }

    // 이메일 동의 업데이트
    public void updateEmailAgreement(Member member, boolean agreement) {
        member.setEmailAgreement(agreement);
        memberRepository.save(member);
    }
}