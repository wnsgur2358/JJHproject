package com.multi.matchon.common.repository;


import com.multi.matchon.common.domain.Attachment;
import com.multi.matchon.common.domain.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    @Query("SELECT a FROM Attachment a WHERE a.boardType = :boardType AND a.boardNumber = :boardNumber ORDER BY a.id DESC LIMIT 1")
    Optional<Attachment> findLatestAttachment(@Param("boardType") BoardType boardType, @Param("boardNumber") Long boardNumber);


    @Query("""
            select t1
            from Attachment t1
            where t1.boardType=:boardType and t1.boardNumber=:matchupBoardId and t1.isDeleted=false
            """)
    List<Attachment> findAllByBoardTypeAndBoardNumber(@Param("boardType") BoardType boardType,@Param("matchupBoardId") Long matchupBoardId);


    // 소프트 삭제용
    @Modifying
    @Transactional
    @Query("UPDATE Attachment a SET a.isDeleted = true " +
            "WHERE a.boardType = :boardType AND a.boardNumber = :boardNumber")
    void softDeleteAllByBoardTypeAndBoardNumber(@Param("boardType") BoardType boardType,
                                                @Param("boardNumber") Long boardNumber);

    //커뮤니티 전용 다운로드용 메서드
    @Query("SELECT a FROM Attachment a WHERE a.savedName = :savedName AND a.boardType = com.multi.matchon.common.domain.BoardType.BOARD")
    Optional<Attachment> findCommunityAttachmentBySavedName(@Param("savedName") String savedName);


}