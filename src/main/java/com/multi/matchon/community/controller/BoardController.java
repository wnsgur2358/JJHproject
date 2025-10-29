package com.multi.matchon.community.controller;

import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.domain.Attachment;
import com.multi.matchon.common.domain.BoardType;
import com.multi.matchon.common.dto.UploadedFile;
import com.multi.matchon.common.repository.AttachmentRepository;
import com.multi.matchon.common.util.AwsS3Utils;
import com.multi.matchon.common.util.FileUploadHelper;
import com.multi.matchon.community.domain.Board;
import com.multi.matchon.community.domain.Category;
import com.multi.matchon.community.dto.req.BoardRequest;
import com.multi.matchon.community.dto.req.CommentRequest;
import com.multi.matchon.community.dto.res.BoardListResponse;
import com.multi.matchon.community.service.BoardService;
import com.multi.matchon.community.service.CommentService;
import com.multi.matchon.community.service.ReportService;
import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.domain.MemberRole;
import io.awspring.cloud.s3.S3Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class BoardController {

    private static final String COMMUNITY_DIR = "community/";
    private static final String IMAGE_DIR = "community/images/";
    private static final String FORM_VIEW = "community/form";

    private final BoardService boardService;
    private final CommentService commentService;
    private final ReportService reportService;
    private final AwsS3Utils awsS3Utils;
    private final AttachmentRepository attachmentRepository;

    @GetMapping
    public String list(@RequestParam(defaultValue = "FREEBOARD") Category category,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 6, Sort.by("createdDate").descending());
        Page<BoardListResponse> boardsPage = boardService.findBoardsWithCommentCount(category, pageable);

        List<BoardListResponse> pinnedPosts = boardService.findPinnedByCategory(category).stream()
                .map(board -> new BoardListResponse(
                        board.getId(),
                        board.getTitle(),
                        board.getCategory().getDisplayName(),
                        board.getMember().getMemberName(),
                        board.getCreatedDate(),
                        commentService.getCommentsByBoard(board).size(),
                        board.isPinned()
                ))
                .toList();

        // 페이징 버튼 계산
        int currentPage = boardsPage.getNumber();         // 현재 페이지
        int totalPages = boardsPage.getTotalPages();      // 전체 페이지 수
        int pageBlockSize = 6;                            // 최대 표시할 페이지 수

        int startPage = Math.max(0, currentPage - pageBlockSize / 2);
        int endPage = Math.min(startPage + pageBlockSize - 1, totalPages - 1);

        if (endPage - startPage + 1 < pageBlockSize) {
            startPage = Math.max(0, endPage - pageBlockSize + 1);
        }

        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            pageNumbers.add(i);
        }

        model.addAttribute("boardsPage", boardsPage);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", Category.values());
        model.addAttribute("pinnedPosts", pinnedPosts);
        model.addAttribute("pageNumbers", pageNumbers);

        return "community/view";
    }


    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Board board = boardService.findById(id);
        List<Attachment> attachments = attachmentRepository.findAllByBoardTypeAndBoardNumber(BoardType.BOARD, board.getId());

        model.addAttribute("board", board);
        model.addAttribute("attachments", attachments);
        model.addAttribute("commentRequest", new CommentRequest());
        model.addAttribute("comments", commentService.getCommentsByBoard(board));
        return "community/detail";
    }

    @GetMapping("/new")
    public String form(Model model, @AuthenticationPrincipal CustomUser user) {
        if (user == null) return "redirect:/login";
        boolean isAdmin = user.getMember().getMemberRole() == MemberRole.ADMIN;

        model.addAttribute("boardRequest", new BoardRequest());
        model.addAttribute("categories", Category.values());
        model.addAttribute("memberName", user.getMember().getMemberName());
        model.addAttribute("formAction", "/community");
        model.addAttribute("isAdmin", isAdmin);
        return FORM_VIEW;
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("boardRequest") BoardRequest boardRequest,
                         BindingResult bindingResult,
                         @RequestParam("files") MultipartFile[] files,
                         Model model,
                         @AuthenticationPrincipal CustomUser user) {

        boolean isAdmin = user.getMember().getMemberRole() == MemberRole.ADMIN;

        if (!isAdmin && boardRequest.getCategory() == Category.ANNOUNCEMENT) {
            bindingResult.rejectValue("category", "accessDenied", "공지사항은 관리자만 작성할 수 있습니다.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", Category.values());
            model.addAttribute("memberName", user.getMember().getMemberName());
            model.addAttribute("formAction", "/community");
            model.addAttribute("isAdmin", isAdmin);
            return FORM_VIEW;
        }

        Member member = user.getMember();
        Board board = Board.builder()
                .title(boardRequest.getTitle())
                .content(boardRequest.getContent())
                .category(boardRequest.getCategory())
                .member(member)
                .boardAttachmentEnabled(false)
                .pinned(isAdmin && boardRequest.isPinned())
                .build();

        boardService.save(board);

        int fileOrder = 0;
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                UploadedFile uploaded = FileUploadHelper.uploadToS3(file, COMMUNITY_DIR, awsS3Utils);
                Attachment attachment = Attachment.builder()
                        .boardType(BoardType.BOARD)
                        .boardNumber(board.getId())
                        .fileOrder(fileOrder++)
                        .originalName(uploaded.getOriginalFileName())
                        .savedName(uploaded.getSavedFileName())
                        .savePath(COMMUNITY_DIR + uploaded.getSavedFileName())
                        .build();
                attachmentRepository.save(attachment);
                board.setBoardAttachmentEnabled(true);
            }
        }

        return "redirect:/community?category=" + boardRequest.getCategory().name();

    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUser user) {
        Board board = boardService.findById(id);
        if (!board.getMember().getId().equals(user.getMember().getId())) {
            return "redirect:/community";
        }

        boolean isAdmin = user.getMember().getMemberRole() == MemberRole.ADMIN;

        BoardRequest request = new BoardRequest(board.getTitle(), board.getContent(), board.getCategory(), board.isPinned());

        model.addAttribute("boardRequest", request);
        model.addAttribute("categories", Category.values());
        model.addAttribute("memberName", user.getMember().getMemberName());
        model.addAttribute("formAction", "/community/" + id + "/edit");
        model.addAttribute("boardId", id);
        model.addAttribute("isAdmin", isAdmin);

        // 기존 첨부파일 전달
        List<Attachment> attachments = attachmentRepository.findAllByBoardTypeAndBoardNumber(BoardType.BOARD, id);
        model.addAttribute("attachments", attachments);

        return FORM_VIEW;
    }


    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("boardRequest") BoardRequest boardRequest,
                         BindingResult bindingResult,
                         @RequestParam("files") MultipartFile[] files,
                         @RequestParam(value = "deletedAttachmentIds", required = false) List<Long> deletedAttachmentIds,
                         Model model,
                         @AuthenticationPrincipal CustomUser user) throws IOException {

        boolean isAdmin = user.getMember().getMemberRole() == MemberRole.ADMIN;

        if (!isAdmin && boardRequest.getCategory() == Category.ANNOUNCEMENT) {
            bindingResult.rejectValue("category", "accessDenied", "공지사항은 관리자만 작성할 수 있습니다.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", Category.values());
            model.addAttribute("memberName", user.getMember().getMemberName());
            model.addAttribute("formAction", "/community/" + id + "/edit");
            model.addAttribute("boardId", id);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("attachments", attachmentRepository.findAllByBoardTypeAndBoardNumber(BoardType.BOARD, id));
            return FORM_VIEW;
        }

        Board board = boardService.findById(id);
        if (!board.getMember().getId().equals(user.getMember().getId())) {
            return "redirect:/community";
        }

        board.update(boardRequest.getTitle(), boardRequest.getContent(), boardRequest.getCategory());

        if (isAdmin) {
            board.setPinned(boardRequest.isPinned());
        }

        if (deletedAttachmentIds != null) {
            for (Long fileId : deletedAttachmentIds) {
                attachmentRepository.findById(fileId).ifPresent(att -> {
                    att.delete(true);
                    String savePath = att.getSavePath();
                    int slashIdx = savePath.lastIndexOf('/');
                    String dir = savePath.substring(0, slashIdx + 1);
                    String filename = savePath.substring(slashIdx + 1);
                    awsS3Utils.deleteFile(dir, filename);
                });
            }
        }

        int fileOrder = 0;
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                UploadedFile uploaded = FileUploadHelper.uploadToS3(file, "community/", awsS3Utils);
                Attachment attachment = Attachment.builder()
                        .boardType(BoardType.BOARD)
                        .boardNumber(board.getId())
                        .fileOrder(fileOrder++)
                        .originalName(uploaded.getOriginalFileName())
                        .savedName(uploaded.getSavedFileName())
                        .savePath("community/" + uploaded.getSavedFileName())
                        .build();
                attachmentRepository.save(attachment);
                board.setBoardAttachmentEnabled(true);
            }
        }

        boardService.save(board);
        return "redirect:/community/" + id;
    }


    @DeleteMapping("/attachments/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteAttachment(@PathVariable Long id, @AuthenticationPrincipal CustomUser user) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다."));

        Board board = boardService.findById(attachment.getBoardNumber());

        if (!board.getMember().getId().equals(user.getMember().getId())) {
            return ResponseEntity.status(403).body("삭제 권한이 없습니다.");
        }

        attachment.delete(true);  // 소프트 삭제

        String fullPath = attachment.getSavePath(); // 예: "community/abc123.png"
        int slashIndex = fullPath.lastIndexOf('/');
        String dir = fullPath.substring(0, slashIndex + 1); // "community/"
        String fileName = fullPath.substring(slashIndex + 1); // "abc123.png"

        awsS3Utils.deleteFile(dir, fileName);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/download-force/{filename}")
    public ResponseEntity<Resource> forceDownload(@PathVariable String filename) throws IOException {
        Optional<Attachment> optional = attachmentRepository.findCommunityAttachmentBySavedName(filename);
        if (optional.isEmpty()) return ResponseEntity.notFound().build();

        Attachment attachment = optional.get();
        S3Resource resource = awsS3Utils.downloadFileWithFullName(attachment.getSavePath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(attachment.getOriginalName(), StandardCharsets.UTF_8) + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .body(resource);
    }

    @PostMapping("/image-upload")
    @ResponseBody
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image) {
        if (image.isEmpty()) return ResponseEntity.badRequest().body("No file selected");

        String uuidFileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        awsS3Utils.saveFile(IMAGE_DIR, uuidFileName, image);
        String imageUrl = awsS3Utils.getObjectUrl(IMAGE_DIR, uuidFileName, image);

        return ResponseEntity.ok(Map.of("url", imageUrl));
    }

    @PostMapping("/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deletePost(@PathVariable Long id, @AuthenticationPrincipal CustomUser user) {
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Board board = boardService.findById(id);
        boolean isOwner = board.getMember().getId().equals(user.getMember().getId());
        boolean isAdmin = user.getMember().getMemberRole() == MemberRole.ADMIN;

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).body("삭제 권한이 없습니다.");
        }

        boardService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
