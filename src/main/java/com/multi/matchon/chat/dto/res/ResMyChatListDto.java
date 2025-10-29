package com.multi.matchon.chat.dto.res;

import com.multi.matchon.chat.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResMyChatListDto {

    private Long roomId;
    private String roomName;
    private Boolean isGroupChat;
    private Boolean isBlock; // 내가 차단했는지 여부
    private Long unReadCount;


    public static ResMyChatListDto from(ChatRoom room, Long memberId, boolean isBlocked, Long unreadCount) {
        return ResMyChatListDto.builder()
                .roomId(room.getId())
                .roomName(room.getChatRoomName())
                .isGroupChat(room.getIsGroupChat())
                .isBlock(isBlocked)
                .unReadCount(unreadCount)
                .build();
    }
}
