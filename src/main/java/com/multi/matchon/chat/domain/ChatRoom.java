package com.multi.matchon.chat.domain;

import com.multi.matchon.common.domain.BaseEntity;
import com.multi.matchon.matchup.domain.MatchupBoard;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name="chat_room")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chat_room_id")
    private Long id;

    @Column(name="is_group_chat")
    @Builder.Default
    private Boolean isGroupChat = true;

    @Column(name="chat_room_name", nullable = false, length = 255)
    private String chatRoomName;

    @OneToMany(mappedBy = "chatRoom")
    @Builder.Default
    private List<ChatParticipant> chatParticipants = new ArrayList<>();

    @OneToOne(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    private MatchupBoard matchupBoard;

    @Column(name="is_deleted")
    @Builder.Default
    private Boolean isDeleted=false;

    public void deleteChatRoom(Boolean isDeleted){
        this.isDeleted = isDeleted;
    }

    public void updateMatchupBoard(MatchupBoard matchupBoard){
        this.matchupBoard = matchupBoard;
    }

}
