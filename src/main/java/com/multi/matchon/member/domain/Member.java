package com.multi.matchon.member.domain;

import com.multi.matchon.common.domain.BaseTimeEntity;
import com.multi.matchon.common.domain.Positions;
import com.multi.matchon.common.domain.SportsType;
import com.multi.matchon.common.domain.TimeType;
import com.multi.matchon.team.domain.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
//@Setter: JPA entity에서 setter사용은 자제, test용
@Table(name="member", uniqueConstraints = {@UniqueConstraint(name="UK_member_email",columnNames = {"member_email"})

})

public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_email", nullable = false, length = 100)
    private String memberEmail;

    @Column(name = "member_password", nullable = false, length = 100)
    private String memberPassword;

    @Column(name = "member_name", nullable = false, length = 50)
    private String memberName;

    @Column(name = "member_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Positions positions;

    @Setter
    @Column(name = "preferred_time")
    @Enumerated(EnumType.STRING)
    private TimeType timeType;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Setter
    @Column(name = "my_temperature")
    private Double myTemperature;

    @Column(name = "picture_attachment_enabled")
    private Boolean pictureAttachmentEnabled;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "previous_password", length = 100)
    private String previousPassword;

    @Column(name = "temporary_password", length = 100)
    private String temporaryPassword;

    @Column(name = "is_temporary_password", nullable = false)
    @Builder.Default
    private Boolean isTemporaryPassword = false;

    @Column(name = "email_agreement", nullable = false)
    @Builder.Default
    private Boolean emailAgreement = false; // 기본값 false

    @Column(name = "suspended_until")
    private LocalDateTime suspendedUntil;

    // ==== 상태 관련 메서드 ====
    public boolean isSuspended() {
        return suspendedUntil != null && LocalDateTime.now().isBefore(suspendedUntil);
    }

    public void suspend(int days) {
        this.suspendedUntil = LocalDateTime.now().plusDays(days);
    }

    public void suspendPermanently() {
        this.suspendedUntil = LocalDateTime.of(9999, 12, 31, 23, 59);
    }

    public void unsuspend() {
        this.suspendedUntil = null;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public void unmarkAsDeleted() {
        this.isDeleted = false;
    }

    // ==== 복원/비밀번호 관련 메서드 ====
    public void restoreAsUser(String encodedPassword, String name) {
        unmarkAsDeleted();
        this.memberPassword = encodedPassword;
        this.memberName = name;
        this.memberRole = MemberRole.USER;
        this.pictureAttachmentEnabled = true;
        this.myTemperature = 36.5;
        this.positions = null;
        this.timeType = null;
    }

    public void restoreAsHost(String encodedPassword, String name) {
        unmarkAsDeleted();
        this.memberPassword = encodedPassword;
        this.memberName = name;
        this.memberRole = MemberRole.HOST;
        this.pictureAttachmentEnabled = true;
    }

    public void updatePassword(String encodedPassword) {
        this.previousPassword = this.memberPassword;
        this.memberPassword = encodedPassword;
    }

    public void updatePasswordWithHistory(String newEncodedPassword) {
        this.previousPassword = this.memberPassword;
        this.memberPassword = newEncodedPassword;
    }

    // ==== 세터 메서드 ====
//    public void setIsTemporaryPassword(boolean isTemporaryPassword) {
//        this.isTemporaryPassword = isTemporaryPassword;
//    }

    public void setTemporaryPassword(String encodedTempPassword) {
        this.temporaryPassword = encodedTempPassword;
    }

    public void clearPersonalInfo() {
        this.positions = null;
        this.timeType = null;
        this.myTemperature = null;
        this.pictureAttachmentEnabled = null;
    }

    public void clearTemporaryPassword() {
        this.temporaryPassword = null;
    }


    public void setIsTemporaryPassword(boolean isTemporaryPassword) {
        this.isTemporaryPassword = isTemporaryPassword;
    }

    public void updateMyTemperature(Double myTemperature){
        this.myTemperature = myTemperature;
    }

    public void setEmailAgreement(Boolean emailAgreement) {
        this.emailAgreement = emailAgreement;
    }
}
