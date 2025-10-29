package com.multi.matchon.common.domain;

import com.multi.matchon.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name="notification")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class Notification extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="notification_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="received_member_id", nullable = false)
    private Member receivedMember;

    @Column(name="notification_message",nullable = false, columnDefinition = "TEXT")
    private String notificationMessage;

    @Column(name="target_url",length = 500)
    private String targetUrl;

    @Column(name="is_read",columnDefinition = "BOOLEAN NULL DEFAULT FALSE")
    @Builder.Default
    private Boolean isRead=false;


    public void updateIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

}
