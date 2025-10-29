package com.multi.matchon.chat.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResChatErrorDto {

    private String errorMessage;

    private String receiverEmail;

    public void setReceiverEmail(String receiverEmail){
        this.receiverEmail = receiverEmail;
    }
}
