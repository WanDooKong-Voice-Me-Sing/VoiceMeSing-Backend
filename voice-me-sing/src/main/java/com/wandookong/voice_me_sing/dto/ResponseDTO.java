package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "API 응답을 담고 있는 데이터 전송 객체")
@Getter
@Setter
@AllArgsConstructor
public class ResponseDTO<T> {

    @Schema(description = "응답 상태", example = "success")
    private String status;

    @Schema(description = "응답 메시지", example = "Operation completed successfully.")
    private String message;

    @Schema(description = "응답 데이터", implementation = Object.class)
    private T data;
}
