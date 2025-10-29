package com.multi.matchon.common.domain;

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
//@Setter: JPA entity에서 setter사용은 자제, test용
@Table(name="positions")
public class Positions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="position_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="position_name", nullable = false, columnDefinition = "ENUM('GOALKEEPER', 'CENTER_BACK', 'LEFT_RIGHT_BACK', 'LEFT_RIGHT_WING_BACK', 'CENTRAL_DEFENSIVE_MIDFIELDER', 'CENTRAL_MIDFIELDER', 'CENTRAL_ATTACKING_MIDFIELDER', 'LEFT_RIGHT_WING', 'STRIKER_CENTER_FORWARD', 'SECOND_STRIKER', 'LEFT_RIGHT_WINGER') NOT NULL")
    private PositionName positionName;


}
