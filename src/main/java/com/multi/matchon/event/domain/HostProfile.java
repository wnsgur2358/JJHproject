package com.multi.matchon.event.domain;

import com.multi.matchon.common.domain.BaseTimeEntity;
import com.multi.matchon.common.domain.SportsType;
import com.multi.matchon.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name="host_profile", uniqueConstraints = {@UniqueConstraint(name="UK_host_name",columnNames = {"hostName"})
})
//@Setter: JPA entity에서 setter사용은 자제, test용
public class HostProfile extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="host_profile_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="host_id",nullable = false)
    private Member member;

    // 기관명 변경용 setter
    @Setter
    @Column(name="host_name", nullable = true, unique = true, length = 100)
    private String hostName;

    @Column(name="picture_attachment_enabled",nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE CHECK (picture_attachment_enabled = TRUE)")
    @Builder.Default
    private Boolean pictureAttachmentEnabled=true;


}
