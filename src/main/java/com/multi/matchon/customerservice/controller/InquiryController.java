package com.multi.matchon.customerservice.controller;

import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.service.NotificationService;
import com.multi.matchon.customerservice.domain.CustomerServiceType;
import com.multi.matchon.customerservice.domain.Inquiry;
import com.multi.matchon.customerservice.dto.req.InquiryReqDto;
import com.multi.matchon.customerservice.dto.res.InquiryResDto;
import com.multi.matchon.customerservice.service.InquiryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final NotificationService notificationService;

    @GetMapping("/inquiry")
    public String listInquiries(@AuthenticationPrincipal CustomUser user,
                                @RequestParam Optional<String> keyword,
                                @RequestParam Optional<CustomerServiceType> category,
                                @PageableDefault(size = 10) Pageable pageable,
                                Model model, HttpServletRequest request) {
        Page<InquiryResDto> inquiries = inquiryService.findMyInquiries(user.getMember().getId(), keyword, category, pageable);
        model.addAttribute("inquiries", inquiries);
        model.addAttribute("keyword", keyword.orElse(null));
        model.addAttribute("category", category.orElse(null));

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "cs/inquiry/inquiry-list :: #inquiryContentArea";
        }

        return "cs/inquiry/inquiry-list";
    }

    @GetMapping("/inquiry/form")
    public String writeForm(Model model) {
        model.addAttribute("inquiry", new InquiryReqDto());
        return "cs/inquiry/inquiry-register";
    }

    @PostMapping("/inquiry/create")
    public String create(@AuthenticationPrincipal CustomUser user, @ModelAttribute InquiryReqDto dto, BindingResult result) {
        if (result.hasErrors()) {
            return "cs/inquiry/inquiry-register";
        }
        inquiryService.saveInquiry(user.getMember().getId(), dto);

        // 관리자 알림 전송
        notificationService.notifyAdmins(user.getMember().getMemberName() + " 님이 1:1 문의를 등록했습니다.", "/admin/inquiry", user.getMember());
        return "redirect:/inquiry";
    }

    @GetMapping("/inquiry/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("inquiry", inquiryService.findEditableInquiry(id));
        return "cs/inquiry/inquiry-register";
    }

    @PostMapping("/inquiry/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute InquiryReqDto dto, @AuthenticationPrincipal CustomUser user) {
        inquiryService.updateInquiry(id, dto);

        // 사용자 이름 가져오기
        String memberName = user.getMember().getMemberName();

        // 관리자 알림
        notificationService.notifyAdmins(user.getMember().getMemberName() + " 님의 1:1 문의가 수정되었습니다.", "/admin/inquiry/" + id, user.getMember());
        return "redirect:/inquiry";
    }

    @PostMapping("/inquiry/delete/{id}")
    public String delete(@PathVariable Long id) {
        inquiryService.deleteCompletedInquiry(id);
        return "redirect:/inquiry";
    }

    @GetMapping("/inquiry/{id}")
    public String getInquiryDetail(@PathVariable Long id, @AuthenticationPrincipal CustomUser user, Model model) {
        InquiryResDto dto = inquiryService.findByIdAndOwner(id, user.getMember().getId());
        model.addAttribute("inquiry", dto);
        return "cs/inquiry/inquiry-detail";
    }
}
