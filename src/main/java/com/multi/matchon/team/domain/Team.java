package com.multi.matchon.team.domain;


import com.multi.matchon.chat.domain.ChatRoom;
import com.multi.matchon.common.domain.BaseEntity;
import com.multi.matchon.common.domain.Positions;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name="team")
public class Team extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="team_id")
    private Long id;

    @Column(name = "team_name", nullable = false, length = 200)
    private String teamName;

    @Column(name="team_region", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RegionType teamRegion;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="position_id",nullable = false)
//    private Positions position;

    @Column(name="team_rating_average",nullable = false)
    private Double teamRatingAverage;

    @Column(name="recruitment_status",nullable = false)
    @Builder.Default
    private Boolean recruitmentStatus = false;

    @Column(name="team_introduction",nullable = false,columnDefinition = "TEXT")
    private String teamIntroduction;

    @Column(name="team_attachment_enabled",nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE check (team_attachment_enabled=true)")
    @Builder.Default
    private Boolean teamAttachmentEnabled = true;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitingPosition> recruitingPositions = new ArrayList<>();

    @Column(name="is_deleted")
    @Builder.Default
    private Boolean isDeleted=false;

    public void softDelete() {
        this.isDeleted = true;
    }

    @Column(name = "created_person", nullable = false, columnDefinition = "VARCHAR(100)")
    private String createdPerson;

    @Setter
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public void updateInfo(String name, String intro, RegionType region, Double rating, Boolean recruitStatus) {
        this.teamName = name;
        this.teamIntroduction = intro;
        this.teamRegion = region;
        this.teamRatingAverage = rating;
        this.recruitmentStatus = recruitStatus;
    }
    public void updateRating(double averageRating) {
        this.teamRatingAverage = averageRating;
    }


}
