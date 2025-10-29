package com.multi.matchon.community.controller;

import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.community.domain.ReasonType;
import com.multi.matchon.community.domain.ReportType;
import com.multi.matchon.community.dto.res.ReportResponse;
import com.multi.matchon.community.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;


    // 신고 접수 - 일반 사용자용 (POST /community/reports)
    @PostMapping("/community/reports")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> reportContent(
            @RequestParam ReportType type,
            @RequestParam Long targetId,
            @RequestParam String reason,
            @RequestParam ReasonType reasonType,
            @AuthenticationPrincipal CustomUser user
    ) {
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            reportService.report(type, targetId, reason, reasonType, user.getMember());
            return ResponseEntity.ok("신고가 접수되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // 관리자용 신고 목록 페이지 (GET /admin/reports/page)
    @GetMapping("/admin/reports/page")
    @PreAuthorize("hasRole('ADMIN')")
    public String reportManagePage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) ReportType reportType,
            @RequestParam(required = false) ReasonType reasonType,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("createdDate").descending());
        Page<ReportResponse> reportPage = reportService.getPagedReportsWithFilter(pageable, reportType, reasonType);

        model.addAttribute("reportPage", reportPage);
        model.addAttribute("reports", reportPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("reportType", reportType != null ? reportType.name() : "");
        model.addAttribute("reasonType", reasonType != null ? reasonType.name() : "");
        return "admin/report";
    }



    // fragment 요청 (GET /admin/reports/reportBody)
    @GetMapping("/admin/reports/reportBody")
    @PreAuthorize("hasRole('ADMIN')")
    public String getReportBodyFragment(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(required = false) ReportType reportType,
                                        @RequestParam(required = false) ReasonType reasonType,
                                        Model model) {

        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<ReportResponse> reportPage = reportService.getPagedReportsFiltered(pageable, reportType, reasonType);

        model.addAttribute("reportPage", reportPage);
        model.addAttribute("reports", reportPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("selectedReportType", reportType);
        model.addAttribute("selectedReasonType", reasonType);

        return "admin/report :: reportBody, pagination";
    }

}
