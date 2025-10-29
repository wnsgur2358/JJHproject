package com.multi.matchon.community.repository;

import com.multi.matchon.community.domain.Comment;
import com.multi.matchon.community.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardAndIsDeletedFalse(Board board);

    void deleteAllByBoard(Board board);

    int countByBoardIdAndIsDeletedFalse(Long boardId);


}

