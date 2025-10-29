package com.multi.matchon.community.domain;

import com.multi.matchon.common.domain.BaseEntity;
import com.multi.matchon.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name="report")
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="report_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    @Column(nullable = false)
    private Long targetId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Member reporter;

    @Size(max = 500)
    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReasonType reasonType;

    @Column(nullable = false)
    @Builder.Default
    private boolean suspended = false;

    @Column(name = "target_is_admin", nullable = false)
    @Builder.Default
    private boolean targetIsAdmin = false;

    @Column(name = "target_member_id")
    private Long targetMemberId;

    @Column(name = "target_writer_name", length = 100)
    private String targetWriterName;
}
