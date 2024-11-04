package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "음성 모델 삭제를 위한 데이터 전송 객체\nData Transfer Object for deleting a voice model")
public class VoiceModelDeleteDTO {

    @Schema(description = "삭제할 음성 모델의 식별자", example = "2")
    private String voiceModelId;
}
