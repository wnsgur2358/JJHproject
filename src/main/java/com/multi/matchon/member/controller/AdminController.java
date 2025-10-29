package com.multi.matchon.member.controller;

import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.domain.Status;
import com.multi.matchon.common.service.NotificationService;
import com.multi.matchon.community.domain.Report;
import com.multi.matchon.community.dto.res.ReportResponse;
import com.multi.matchon.community.service.ReportService;
import com.multi.matchon.customerservice.domain.Inquiry;
import com.multi.matchon.customerservice.domain.InquiryAnswer;
import com.multi.matchon.customerservice.dto.res.InquiryResDto;
import com.multi.matchon.customerservice.repository.InquiryAnswerRepository;
import com.multi.matchon.customerservice.repository.InquiryRepository;
import com.multi.matchon.event.domain.EventRequest;
import com.multi.matchon.event.dto.res.EventResDto;
import com.multi.matchon.event.repository.EventRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final EventRepository eventRepository;
    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final ReportService reportService;
    private final NotificationService notificationService;

    @GetMapping
    public String adminHome() {
        return "admin/admin-home";
    }

    @GetMapping("/inquiry")
    public String listAdminInquiries(@RequestParam(defaultValue = "0") int page,
                                     Model model,
                                     HttpServletRequest request) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Inquiry> inquiryPage = inquiryRepository.findAllByIsDeletedFalse(pageable);

        model.addAttribute("inquiries", inquiryPage);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/admin-inquiry-list :: adminInquiryTableArea"; // fragment만 반환
        }

        return "admin/admin-inquiry-list";
    }

    @GetMapping("/inquiry/{id}")
    public String getInquiryDetail(@PathVariable Long id, Model model) {
        Inquiry inquiry = inquiryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NoSuchElementException("문의 없음"));

        InquiryResDto dto = new InquiryResDto(inquiry); // DTO 변환
        model.addAttribute("inquiry", dto);
        return "admin/admin-inquiry-answer";
    }

    @PostMapping("/inquiry/{id}/answer")
    @Transactional
    public String submitAnswer(@PathVariable Long id,
                               @RequestParam String answerContent,
                               @AuthenticationPrincipal CustomUser admin) {
        Inquiry inquiry = inquiryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NoSuchElementException("문의가 존재하지 않습니다."));

        // 답변 존재 여부만으로 판단
        if (inquiryAnswerRepository.findActiveAnswerByInquiryId(inquiry.getId()).isPresent()) {
            throw new IllegalStateException("이미 답변이 등록된 문의입니다.");
        }

        InquiryAnswer answer = InquiryAnswer.builder()
                .inquiry(inquiry)
                .member(admin.getMember())
                .answerContent(answerContent)
                .build();

        inquiry.setAnswer(answer);
        inquiryAnswerRepository.save(answer);

        inquiry.complete(); // 상태는 저장 이후 변경

        notificationService.sendNotification(
                inquiry.getMember(), // 알림 받을 사용자
                "문의에 답변이 등록되었습니다.",
                "/inquiry/" + inquiry.getId()
        );

        return "redirect:/admin/inquiry";
    }

    @PostMapping("/inquiry/{id}/delete")
    @Transactional
    public String deleteInquiry(@PathVariable Long id) {
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        inquiry.markDeleted();
        return "redirect:/admin/inquiry";
    }


    @GetMapping(value = "/event", produces = MediaType.TEXT_HTML_VALUE)
    public String getAdminEventPage(Model model) {
        return "admin/admin-event-list";
    }

    @ResponseBody
    @GetMapping("/event/page")
    public Page<EventResDto> getEventPage(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdDate").descending());
        Page<EventRequest> eventPage = eventRepository.findAll(pageable);

        return eventPage.map(e -> new EventResDto(
                e.getId(),
                e.getEventTitle(),
                e.getMember().getMemberName(),
                e.getEventStatus(),
                e.getCreatedDate()
        ));
    }

    @GetMapping("/event/{id}")
    public String adminEventDetail(@PathVariable Long id, Model model) {
        EventRequest event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 대회를 찾을 수 없습니다."));

        String regionLabel = switch (event.getEventRegionType()) {
            case CAPITAL_REGION -> "수도권";
            case YEONGNAM_REGION -> "영남권";
            case HONAM_REGION -> "호남권";
            case CHUNGCHEONG_REGION -> "충청권";
            case GANGWON_REGION -> "강원권";
            case JEJU -> "제주권";
        };

        String statusLabel = switch (event.getEventStatus()) {
            case PENDING -> "대기중";
            case APPROVED -> "승인";
            case DENIED -> "반려";
            default -> "알 수 없음";
        };

        model.addAttribute("event", event);
        model.addAttribute("regionLabel", regionLabel);
        model.addAttribute("statusLabel", statusLabel);
        model.addAttribute("isAdmin", true);

        return "admin/admin-event-detail";
    }

    @PostMapping("/event/{id}/status")
    @Transactional
    public String updateEventStatus(@PathVariable Long id, @RequestParam("status") Status status) {
        eventRepository.updateEventStatus(id, status);
        EventRequest event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 대회를 찾을 수 없습니다."));

        // 승인 or 반려 시 알림 전송
        if (status == Status.APPROVED || status == Status.DENIED) {
            String message = (status == Status.APPROVED) ? "대회가 승인되었습니다." : "대회가 반려되었습니다.";
            notificationService.sendNotification(
                    event.getMember(),
                    message,
                    "/event/" + event.getId()
            );
        }
        return "redirect:/admin/event";
    }

    @PostMapping("/event/{id}/delete")
    @Transactional
    public String deleteApprovedEvent(@PathVariable Long id) {
        EventRequest event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 대회를 찾을 수 없습니다."));

        // 승인 상태가 아닌 경우 삭제 불가
        if (event.getEventStatus() != Status.APPROVED) {
            throw new IllegalStateException("승인된 대회만 삭제할 수 있습니다.");
        }

        event.markAsDeleted();
        eventRepository.save(event);
        return "redirect:/admin/event";
    }

    @GetMapping("/reports")
    public String redirectToReportsPage() {
        return "redirect:/admin/reports/page";
    }

}