package com.multi.matchon.matchup.domain;


import com.multi.matchon.chat.domain.ChatRoom;
import com.multi.matchon.common.domain.BaseEntity;
import com.multi.matchon.common.domain.SportsType;
import com.multi.matchon.matchup.dto.res.ResMatchupBoardDto;
import com.multi.matchon.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name="matchup_board")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class MatchupBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="matchup_board_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="writer_id",nullable = false)
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sports_type_id",nullable = false)
    private SportsType sportsType;

    @Column(name="reservation_attachment_enabled", nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE CHECK (reservation_attachment_enabled=true)")
    private Boolean reservationAttachmentEnabled;

    @Column(name="team_intro",nullable = false,columnDefinition = "TEXT")
    private String teamIntro;

    @Column(name="sports_facility_name",nullable = false,length = 100)
    private String sportsFacilityName;

    @Column(name="sports_facility_address",nullable = false, length = 100)
    private String sportsFacilityAddress;

    @Column(name="match_datetime",nullable = false)
    private LocalDateTime matchDatetime;

    @Column(name="match_endtime",nullable = false)
    private LocalDateTime matchEndtime;

    @Column(name="current_participant_count",nullable = false)
    private Integer currentParticipantCount;

    @Column(name="max_participants",nullable = false)
    private Integer maxParticipants;

    @Column(name="min_manner_temperature",nullable = false)
    private Double minMannerTemperature;

    @Column(name="match_description",nullable = false, columnDefinition = "TEXT")
    private String matchDescription;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="chat_room_id")
    private ChatRoom chatRoom;

    @Column(name="is_rating_initialized")
    @Builder.Default
    private Boolean isRatingInitialized = false;

    @Column(name="is_notified")
    @Builder.Default
    private Boolean isNotified = false;

    @Column(name="is_deleted")
    @Builder.Default
    private Boolean isDeleted=false;

    @OneToMany(mappedBy = "matchupBoard")
    @Builder.Default
    private List<MatchupRequest> matchupRequests = new ArrayList<>();


    public void update(SportsType sportsType, String teamIntro, String sportsFacilityName, String sportsFacilityAddress, Integer currentParticipantCount, Integer maxParticipants, Double minMannerTemperature, String matchDescription){

        this.sportsType = sportsType;
        this.teamIntro = teamIntro;
        this.sportsFacilityName = sportsFacilityName;
        this.sportsFacilityAddress = sportsFacilityAddress;
        this.currentParticipantCount = currentParticipantCount;
        this.maxParticipants = maxParticipants;
        this.minMannerTemperature = minMannerTemperature;
        this.matchDescription = matchDescription;
    }

    public void delete(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void increaseCurrentParticipantCount(Integer participantCount){
        this.currentParticipantCount += participantCount;
    }

    public void decreaseCurrentParticipantCount(Integer participantCount){
        this.currentParticipantCount -= participantCount;
    }

    public void setRatingInitialized(Boolean isRatingInitialized){
        this.isRatingInitialized = isRatingInitialized;
    }

    public void updateIsNotified(Boolean isNotified){
        this.isNotified = isNotified;
    }

    public void changeChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        if(chatRoom.getMatchupBoard()!=this)
            this.chatRoom.updateMatchupBoard(this);
    }
}
