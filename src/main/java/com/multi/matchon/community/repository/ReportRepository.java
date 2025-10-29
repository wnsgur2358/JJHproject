package com.multi.matchon.community.repository;

import com.multi.matchon.community.domain.*;
import com.multi.matchon.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findByReportTypeAndTargetIdAndReporter(ReportType type, Long targetId, Member reporter);

    Page<Report> findAll(Pageable pageable);

    int countByReportTypeAndTargetId(ReportType type, Long targetId);

    @Query("""
    SELECT r FROM Report r
    WHERE (:reportType IS NULL OR r.reportType = :reportType)
      AND (:reasonType IS NULL OR r.reasonType = :reasonType)
""")
    Page<Report> findByReportTypeAndReasonTypeWithPaging(
            @Param("reportType") ReportType reportType,
            @Param("reasonType") ReasonType reasonType,
            Pageable pageable);

    Page<Report> findByReportTypeAndReasonType(ReportType reportType, ReasonType reasonType, Pageable pageable);
    Page<Report> findByReportType(ReportType reportType, Pageable pageable);
    Page<Report> findByReasonType(ReasonType reasonType, Pageable pageable);

}

