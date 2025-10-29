package com.multi.matchon.common.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private Boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data){
        return new ApiResponse<>(true, "요청 성공", data);
    }

    public static <T> ApiResponse<T> fail(String message){
        return new ApiResponse<>(false, message, null);
    }
}
