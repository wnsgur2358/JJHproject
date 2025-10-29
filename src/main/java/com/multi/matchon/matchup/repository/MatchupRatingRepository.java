package com.multi.matchon.matchup.repository;


import com.multi.matchon.matchup.domain.MatchupRating;
import com.multi.matchon.matchup.dto.res.ResMatchupMyGameListDto;
import com.multi.matchon.matchup.dto.res.ResMatchupRatingDto;
import com.multi.matchon.matchup.dto.res.ResMatchupRatingListDto;
import com.multi.matchon.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MatchupRatingRepository extends JpaRepository<MatchupRating, Long> {


    @Query("""
            select
            distinct
            new com.multi.matchon.matchup.dto.res.ResMatchupMyGameListDto(
            t2.id,
            t2.matchDatetime,
            t2.matchEndtime,
            t2.sportsType.sportsTypeName,
            t2.sportsFacilityName,
            t2.sportsFacilityAddress,
            t2.isRatingInitialized,
            t2.currentParticipantCount,
            t2.maxParticipants
            )
            from MatchupRequest t1
            right join t1.matchupBoard t2
            where (t2.isDeleted=false and t2.matchDatetime<CURRENT_TIMESTAMP ) and
            (
                (t2.writer=:loginMember) or
                (
                    (t1 is not null and t1.member=:loginMember) and
                    (
                        (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted=false) or
                        (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=0 and t1.isDeleted=false) or
                        (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                        (t1.matchupStatus=com.multi.matchon.common.domain.Status.APPROVED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                        (t1.matchupStatus=com.multi.matchon.common.domain.Status.CANCELREQUESTED and t1.matchupRequestSubmittedCount=1 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false) or
                        (t1.matchupStatus=com.multi.matchon.common.domain.Status.CANCELREQUESTED and t1.matchupRequestSubmittedCount=2 and t1.matchupCancelSubmittedCount=1 and t1.isDeleted=false)
                    )
                )
            )
            order by t2.matchDatetime desc
            """)
    Page<ResMatchupMyGameListDto> findAllMyGames(Pageable pageable, @Param("loginMember") Member loginMember);


    @Query("""
            select
            new com.multi.matchon.matchup.dto.res.ResMatchupRatingListDto(
                t1.matchupBoard.id,
                t1.memberEval.memberName,
                t1.memberTarget.memberName,
                t1.memberEval.id,
                t1.memberTarget.id,
                t1.mannerScore,
                t1.skillScore,
                t1.review,
                t1.isCompleted,
                t2.memberEval.id,
                t2.memberTarget.id,
                t2.mannerScore,
                t2.skillScore,
                t2.review,
                t2.isCompleted
            )
            from MatchupRating t1
            join MatchupRating t2 on t1.memberTarget=t2.memberEval
            where t1.matchupBoard.id =:boardId and t2.matchupBoard.id=:boardId and
                t1.memberEval=:loginMember and t2.memberTarget=:loginMember
            
            """)
    Page<ResMatchupRatingListDto> findAllMyRatings(Pageable pageable,@Param("loginMember") Member loginMember, @Param("boardId") Long boardId);


    @Query("""
            select
            new com.multi.matchon.matchup.dto.res.ResMatchupRatingDto(
                t1.matchupBoard.id,
                t1.memberEval.id,
                t1.memberEval.memberName,
                t1.memberTarget.id,
                t1.memberTarget.memberName
            )
            from MatchupRating t1
            where t1.matchupBoard.id =:boardId and t1.memberEval.id =:evalId and t1.memberTarget.id=:targetId and t1.isCompleted=false
            """)
    Optional<ResMatchupRatingDto> findResMatchupRatingDtoByBoardIdAndEvalIdAndTargetId(@Param("boardId") Long boardId, @Param("evalId") Long evalId,@Param("targetId") Long targetId);


    @Query("""
            select t1
            from MatchupRating t1
            where t1.matchupBoard.id =:boardId and t1.memberEval.id =:evalId and t1.memberTarget.id=:targetId and t1.isCompleted=false
            """)
    Optional<MatchupRating> findByBoardIdAndEvalIdAndTargetId(@Param("boardId") Long boardId, @Param("evalId") Long evalId,@Param("targetId") Long targetId);


    @Query("""
            select
            new com.multi.matchon.matchup.dto.res.ResMatchupRatingDto(
            t1.matchupBoard.id,
            t1.memberEval.memberName,
            t1.memberTarget.memberName,
            t1.mannerScore,
            t1.skillScore,
            t1.review
            )
            from MatchupRating t1
            where t1.matchupBoard.id=:boardId and t1.memberEval.id =:evalId and t1.memberTarget.id=:targetId and t1.isCompleted=true
            """)
    Optional<ResMatchupRatingDto> findDetailResMatchupRatingDtoByBoardIdAndEvalIdAndTargetId(@Param("boardId") Long boardId, @Param("evalId") Long evalId,@Param("targetId") Long targetId);
}




























