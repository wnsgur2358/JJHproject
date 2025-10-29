package com.multi.matchon.chat.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResGroupChatParticipantListDto {

    private Long memberId;

    private String memberName;

    private String memberEmail;

    private Boolean isBlock;

    private Boolean isMyPrivateChatPartner;

    private Long privateRoomId;

}
