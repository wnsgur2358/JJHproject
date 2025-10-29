package com.multi.matchon.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@EntityListeners({AuditingEntityListener.class})
public abstract class BaseEntity {
    @CreationTimestamp
    @Column(name="created_date")
    private LocalDateTime createdDate;

    @Column(name="created_person", length = 100)
    @CreatedBy
    private String createdPerson;

    @UpdateTimestamp
    @Column(name="modified_date")
    private LocalDateTime modifiedDate;

    @Column(name="modified_person", length = 100)
    @LastModifiedBy
    private String modifiedPerson;
}
