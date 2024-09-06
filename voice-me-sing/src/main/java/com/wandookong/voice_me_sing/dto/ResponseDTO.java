package com.wandookong.voice_me_sing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
@AllArgsConstructor
public class ResponseDTO {
    private String status;
    private String message;
    private Map<String, Object> data;

}
