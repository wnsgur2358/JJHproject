package com.multi.matchon.common.jwt.domain;


import com.multi.matchon.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(
        name = "refresh_token",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_refresh_token_2_member", columnNames = {"member_id"})
        }
)

public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "refresh_token_data", nullable = false, length = 512)
    private String refreshTokenData;

    @Column(name = "refresh_token_expired_date", nullable = false)
    private LocalDateTime refreshTokenExpiredDate;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    // @CurrentTimestamp는 Hibernate에서 기본 제공하지 않는 어노테이션
    // 이 필드를 자동으로 LocalDateTime.now() 채움
    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

    // 토큰 정보 업데이트 메서드
    public void update(String newRefreshTokenData, LocalDateTime newExpiryDate) {
        this.refreshTokenData = newRefreshTokenData;
        this.refreshTokenExpiredDate = newExpiryDate;
    }
}
