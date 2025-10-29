package com.multi.matchon.common.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name="sports_type")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class SportsType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sports_type_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="sports_type_name", nullable = false)
    private SportsTypeName sportsTypeName;
}
