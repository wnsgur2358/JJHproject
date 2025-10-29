package com.multi.matchon.community.controller;

import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.community.domain.Board;
import com.multi.matchon.community.domain.Comment;
import com.multi.matchon.community.dto.req.CommentRequest;
import com.multi.matchon.community.dto.res.CommentResponse;
import com.multi.matchon.community.service.BoardService;
import com.multi.matchon.community.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;

    @PostMapping("/{id}/comments")
    @ResponseBody
    public ResponseEntity<?> addComment(@PathVariable Long id,
                                        @Valid @RequestBody CommentRequest commentRequest,
                                        @AuthenticationPrincipal CustomUser userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Board board = boardService.findById(id);
        Comment comment = Comment.builder()
                .board(board)
                .member(userDetails.getMember())
                .content(commentRequest.getContent())
                .build();

        Comment saved = commentService.save(comment);

        return ResponseEntity.ok(new CommentResponse(
                saved.getMember().getMemberName(),
                saved.getCreatedDate().toString(),
                saved.getContent(),
                saved.getId(),
                saved.getMember().getId()
        ));
    }

    @DeleteMapping("/{boardId}/comments/{commentId}")
    @ResponseBody
    public ResponseEntity<?> deleteComment(@PathVariable Long boardId,
                                           @PathVariable Long commentId,
                                           @AuthenticationPrincipal CustomUser userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Comment comment = commentService.findById(commentId);

        boolean isOwner = comment.getMember().getId().equals(userDetails.getMember().getId());
        boolean isAdmin = userDetails.getMember().getMemberRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).body("삭제 권한이 없습니다.");
        }

        commentService.softDelete(commentId);
        return ResponseEntity.ok("삭제 성공");
    }
}