package com.multi.matchon.team.domain;

import com.multi.matchon.common.domain.Positions;
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
@Table(name="recruiting_position", uniqueConstraints = {@UniqueConstraint(name="UK_team_id_2_position_id",columnNames = {"team_id","position_id"})

})
//@Setter: JPA entity에서 setter사용은 자제, test용
public class RecruitingPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="recruiting_postion_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="position_id", nullable = false)
    private Positions positions;
}
