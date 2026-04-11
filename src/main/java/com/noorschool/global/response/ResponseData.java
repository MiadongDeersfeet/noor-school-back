package com.noorschool.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponseData<T> {

    private String message;
    private T data;
    private boolean success;
    private String timestamp;
    
}
