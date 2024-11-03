package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoiceModelDeleteDTO {

    @Schema(description = "삭제할 음성 모델의 식별자", example = "2")
    private String voiceModelId;
}
