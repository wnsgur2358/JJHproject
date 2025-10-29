package com.multi.matchon.customerservice.controller;

import com.multi.matchon.customerservice.domain.CustomerServiceType;
import com.multi.matchon.customerservice.dto.res.FaqDto;
import com.multi.matchon.customerservice.service.FaqService;
import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;
    private final MemberRepository memberRepository;

    @GetMapping("/cs")
    public String faqShow(@RequestParam(required = false) CustomerServiceType category,
                          @RequestParam(required = false) String keyword,
                          @PageableDefault(size = 8, sort = "createdDate", direction = Sort.Direction.ASC) Pageable pageable,
                          Principal principal,
                          Model model) {

        Page<FaqDto> faqPage = (keyword != null && !keyword.isBlank()) ?
                (category != null
                        ? faqService.searchByCategoryAndTitlePaged(category, keyword, pageable)
                        : faqService.searchByTitlePaged(keyword, pageable))
                : faqService.getFaqListPaged(category, pageable);

        model.addAttribute("faqList", faqPage.getContent());
        model.addAttribute("page", faqPage);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("isAdmin", isAdmin(principal));

        return "cs/cs";
    }

    @GetMapping("/faq/detail/{id}")
    public String detail(@PathVariable Long id, Model model, Principal principal) {
        FaqDto faqDto = faqService.getFaqById(id);
        model.addAttribute("faqDto", faqDto);

        boolean isAdmin = false;
        if (principal != null) {
            String email = principal.getName();
            Member member = memberRepository.findByMemberEmail(email).orElse(null);
            if (member != null && member.getMemberRole().name().equals("ADMIN")) {
                isAdmin = true;
            }
        }
        System.out.println("isAdmin = " + isAdmin);
        model.addAttribute("isAdmin", isAdmin);

        return "cs/cs-faq-detail";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/faq/register")
    public String showRegisterPage() {
        return "cs/cs-faq-register";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/faq/register")
    public String registerFaq(@RequestParam String faqTitle,
                              @RequestParam String faqContent,
                              @RequestParam CustomerServiceType faqCategory,
                              Principal principal) {

        String email = principal.getName();
        Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new RuntimeException("로그인한 사용자를 찾을 수 없습니다."));

        FaqDto faqDto = FaqDto.builder()
                .faqTitle(faqTitle)
                .faqContent(faqContent)
                .faqCategory(faqCategory)
                .build();

        faqService.savePost(faqDto.withMember(member));
        return "redirect:/cs";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/faq/edit/{id}")
    public String showEditPage(@PathVariable Long id, Model model, Principal principal) {
        FaqDto faqDto = faqService.getFaqById(id);
        model.addAttribute("faqDto", faqDto);
        model.addAttribute("isAdmin", isAdmin(principal));
        return "cs/cs-faq-edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/faq/edit/{id}")
    public String updateFaq(@PathVariable Long id,
                            @RequestParam String faqTitle,
                            @RequestParam String faqContent,
                            @RequestParam CustomerServiceType faqCategory,
                            Principal principal,
                            Model model) {
        faqService.updateFaq(id, faqTitle, faqContent, faqCategory);
        model.addAttribute("isAdmin", isAdmin(principal));
        return "redirect:/faq/detail/" + id;
    }

    private boolean isAdmin(Principal principal) {
        if (principal == null) return false;
        String email = principal.getName();
        Member member = memberRepository.findByMemberEmail(email).orElse(null);
        return member != null && member.getMemberRole().name().equals("ADMIN");
    }

    @PostMapping("/faq/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 관리자만 삭제 가능
    public String deleteFaq(@PathVariable Long id) {
        faqService.deleteFaqById(id); // 실제 삭제 로직 수행
        return "redirect:/cs"; // 목록 페이지로 리디렉션
    }
}
