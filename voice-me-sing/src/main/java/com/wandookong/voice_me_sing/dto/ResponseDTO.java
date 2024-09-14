package com.wandookong.voice_me_sing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ResponseDTO<T> {
    private String status;
    private String message;
    private T data;
}
