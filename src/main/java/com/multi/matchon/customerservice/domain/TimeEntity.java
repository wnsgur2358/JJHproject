package com.multi.matchon.customerservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 테이블로 매핑하지 않고, 자식 Entity에게 매핑정보를 상속하기 위한 어노테이션.
@EntityListeners(AuditingEntityListener.class) // JPA에게 해당 Entity는 Auditing 기능을 사용한다는 것을 알리는 어노테이션.
// 데이터 조작 시 자동으로 날짜를 수정해주는 JPA의 AUditiong 기능을 사용하는 Entity.
public class TimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
