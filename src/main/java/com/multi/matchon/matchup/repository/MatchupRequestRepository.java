package com.multi.matchon.matchup.repository;


import com.multi.matchon.common.domain.SportsTypeName;
import com.multi.matchon.matchup.domain.MatchupRequest;
import com.multi.matchon.matchup.dto.res.ResMatchupRequestDto;
import com.multi.matchon.matchup.dto.res.ResMatchupRequestListDto;
import com.multi.matchon.matchup.dto.res.ResMatchupRequestOverviewListDto;
import com.multi.matchon.member.domain.Member;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchupRequestRepository extends JpaRepository<MatchupRequest, Long> {

    @Query("""
            
            select new com.multi.matchon.matchup.dto.res.ResMatchupRequestListDto(
            t2.id,
            t1.id,
            t3.sportsTypeName,
            t2.sportsFacilityName,
            t2.sportsFacilityAddress,
            t2.matchDatetime,
            t2.matchEndtime,
            t2.currentParticipantCount,
            t2.maxParticipants,
            t1.participantCount,
            t1.matchupStatus,
            t1.matchupRequestSubmittedCount,
            t1.matchupCancelSubmittedCount,
            t2.chatRoom.id,
            t1.isDeleted
            )
            from MatchupRequest t1
            join t1.matchupBoard t2
            join t2.sportsType t3
            where t1.member.id=:memberId and t1.member.isDeleted =false and
                    t2.isDeleted = false and
                    (:sportsType is null or t3.sportsTypeName =:sportsType) and
                    (:matchDate is null or DATE(t2.matchDatetime) >=:matchDate) and
                    (:availableFilter is false or (:availableFilter=true and t2.matchDatetime > CURRENT_TIMESTAMP ))
                    order by t1.createdDate DESC
            """)
    Page<ResMatchupRequestListDto> findAllResMatchupRequestListDtosByMemberIdAndSportsTypeAndMatchDateWithPaging(PageRequest pageRequest, @Param("memberId") Long memberId, @Param("sportsType") SportsTypeName sportsTypeName, @Param("matchDate") LocalDate matchDate, @Param("availableFilter") Boolean availableFilter);


    @Query("""
            select new com.multi.matchon.matchup.dto.res.ResMatchupRequestDto(
            t5.memberEmail,
            t5.memberName,
            t2.memberEmail,
            t2.memberName,
            t3.id,
            t1.id,
            t4.sportsTypeName,
            t3.sportsFacilityName,
            t3.sportsFacilityAddress,
            t3.matchDatetime,
            t3.matchEndtime,
            t3.currentParticipantCount,
            t3.maxParticipants,
            t1.participantCount,
            t1.selfIntro,
            t1.matchupStatus,
            t1.matchupRequestSubmittedCount,
            t1.matchupCancelSubmittedCount,
            t1.isDeleted
            )
            from MatchupRequest t1
            join t1.member t2
            join t1.matchupBoard t3
            join t3.sportsType t4
            join t3.writer t5
            where t1.id=:requestId and t3.isDeleted=false and t2.isDeleted=false and t5.isDeleted=false  
            """)
    Optional<ResMatchupRequestDto> findResMatchupRequestDtoByRequestId(Long requestId);


//    @Query("""
//            select case
//                    when count(t1)>0 then true
//                    else false
//                end
//                from MatchupRequest t1
//                where t1.matchupBoard.id =:boardId and t1.member.id =:memberId and t1.isDeleted=true and
//                t1.matchupRequestSubmittedCount >=2
//            """)
//    Boolean hasCanceledMatchRequestMoreThanOnce(@Param("boardId") Long boardId, @Param("memberId") Long memberId);
//
//
//    @Query("""
//        select case
//                when count(t1) > 0 then true
//                else false
//           end
//        from MatchupRequest t1
//        where (t1.matchupBoard.id =:boardId and t1.member.id=:memberId and t1.isDeleted=false) and
//                  t1.matchupStatus in (
//                    com.multi.matchon.common.domain.Status.PENDING,
//                    com.multi.matchon.common.domain.Status.APPROVED,
//                    com.multi.matchon.common.domain.Status.CANCELREQUESTED
//                )
//        """)
//    Boolean isAlreadyMatchupRequestedByBoardIdAndMemberId(@Param("boardId") Long boardId,@Param("memberId") Long memberId); // true: 중복된 요청이 존재, false: 중복된 요청이 없음
//
//    @Query("""
//            select case
//                    when count(t1) >0 then true
//                    else false
//                end
//            from MatchupRequest t1
//            where t1.matchupBoard.id =:boardId and t1.member.id =:memberId and t1.isDeleted=false and
//                t1.matchupStatus =com.multi.matchon.common.domain.Status.DENIED and
//                t1.matchupRequestSubmittedCount>=1
//            """)
//    Boolean hasExceededTwoMatchupRequestsByBoardIdAndMemberId(@Param("boardId") Long boardId, @Param("memberId") Long memberId);

    @Query("""
            select
             t1
            from MatchupRequest t1
            where  t1.matchupBoard.id=:boardId and t1.matchupBoard.isDeleted=false and
                    t1.member.id =:applicantId and t1.member.isDeleted=false
            """)
    Optional<MatchupRequest> findByMatchupBoardIdAndApplicantId(Long boardId, Long applicantId);

    @Query("""
            select
             t1
            from MatchupRequest t1
            join fetch t1.matchupBoard t2
            join fetch t2.writer
            join fetch t1.member
            where  t1.id=:requestId and t1.matchupBoard.isDeleted=false and t1.matchupBoard.writer.isDeleted = false
            """)
    Optional<MatchupRequest> findById(Long requestId);

    @Query("""
            
            select new com.multi.matchon.matchup.dto.res.ResMatchupRequestOverviewListDto(
            t2.id,
            t2.matchDatetime,
            t2.currentParticipantCount,
            t2.maxParticipants,
            t1.id,
            t3.memberName,
            t1.selfIntro,
            t1.participantCount,
            t1.matchupStatus,
            t1.matchupRequestSubmittedCount,
            t1.matchupCancelSubmittedCount,
            t1.isDeleted
            )
            from MatchupRequest t1
            join t1.matchupBoard t2
            join t1.member t3
            where t1.matchupBoard.id=:boardId and
                    t2.isDeleted = false
                    order by t1.createdDate DESC
            """)
    Page<ResMatchupRequestOverviewListDto> findAllResMatchupRequestOverviewListDtoByBoardIdAndSportsTypeWithPaging(PageRequest pageRequest,@Param("boardId") Long boardId);

    @Query("""
            select
            t1
            from MatchupRequest  t1
            join fetch t1.matchupBoard t2
            join fetch t2.writer
            join fetch t1.member t3
            where t1.id=:requestId and t2.id=:boardId and t3.id=:applicantId and
                    t2.isDeleted=false and t2.writer.isDeleted=false
            """)
    Optional<MatchupRequest> findMatchupRequestWithMatchupBoardByRequestIdAndBoardIDAndApplicantId(@Param("requestId") Long requestId, @Param("boardId") Long boardId, @Param("applicantId") Long applicantId);


    @Query("""
            select
            t1
            from MatchupRequest  t1
            join fetch t1.matchupBoard t2
            join fetch t2.writer t3
            where t1.id=:requestId and t2.id=:boardId and t3.id=:writerId and
                    t2.isDeleted=false and t3.isDeleted=false
            """)
    Optional<MatchupRequest> findMatchupRequestWithMatchupBoardByRequestIdAndBoardIDAndWriterId(@Param("requestId") Long requestId, @Param("boardId") Long boardId, @Param("writerId") Long writerId);


    @Query("""
            select t1
            from MatchupRequest t1
            join t1.matchupBoard t2
            join fetch t1.member
            where t2.isDeleted=false and t2.id=:boardId and t2.matchDatetime < CURRENT_TIMESTAMP and t2.writer=:loginMember and
            (
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.CANCELREQUESTED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.CANCELREQUESTED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false)
            )
            
            """)
    List<MatchupRequest> findByBoardIdAndMemberAndGameParticipantConditionAndAfterMatchupDatetime(@Param("boardId") Long boardId, @Param("loginMember") Member loginMember);


    @Query("""
            select t1
            from MatchupRequest t1
            join t1.matchupBoard t2
            join fetch t1.member
            where t2.isDeleted=false and  t2.matchDatetime < CURRENT_TIMESTAMP and t1.member.isDeleted=false and
            (
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.CANCELREQUESTED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.CANCELREQUESTED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false)
            )
            
            """)
    List<MatchupRequest> findByGameParticipantConditionAndAfterMatchupDatetime();


    @Query("""
            select t1.member
            from MatchupRequest t1
            join t1.matchupBoard t2
            where t2.id =:boardId and t2.isDeleted=false and t2.matchDatetime>CURRENT_TIMESTAMP and
            (
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.PENDING and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.PENDING and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted =false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.CANCELREQUESTED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.CANCELREQUESTED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false)
            )
            
            """)
    List<Member> findByBoardIdAndActiveRequests(@Param("boardId") Long boardId);


    @Query("""
            select t1
            from MatchupRequest t1
            join t1.matchupBoard t2
            join fetch t1.member
            where t2.isDeleted=false and t2.matchDatetime <=:threeHoursLater and t1.member.isDeleted=false and
            (
                 (t1.matchupStatus=com.multi.matchon.common.domain.Status.PENDING and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.PENDING and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted =false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.CANCELREQUESTED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.CANCELREQUESTED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false)
            )
            """)
    List<MatchupRequest> findUnnotifiedRequestsAtThreeHoursBeforeMatch(@Param("threeHoursLater") LocalDateTime threeHoursLater);


    @Query("""
            select t1
            from MatchupRequest t1
            join t1.matchupBoard t2
            where t1.member=:loginMember and t2.isDeleted =false and
            (:startTime<t2.matchEndtime and :endTime > t2.matchDatetime )
            """)
    List<MatchupRequest> findByMemberAndStartTimeAndEndTime(@Param("loginMember") Member loginMember,@Param("startTime") LocalDateTime startTime,@Param("endTime") LocalDateTime endTime);
}
