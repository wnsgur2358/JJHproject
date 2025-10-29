package com.multi.matchon.community.service;

import com.multi.matchon.community.domain.Board;
import com.multi.matchon.community.domain.Comment;
import com.multi.matchon.community.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getCommentsByBoard(Board board) {
        return commentRepository.findByBoardAndIsDeletedFalse(board);
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
    }

    public void softDelete(Long id) {
        Comment comment = findById(id);
        comment.setIsDeleted(true);
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteAllByBoard(Board board) {
        commentRepository.deleteAllByBoard(board);
    }
}
