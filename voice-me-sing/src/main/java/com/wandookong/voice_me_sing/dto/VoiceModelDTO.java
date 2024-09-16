package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "음성 모델의 식별자와 이름을 담고 있는 데이터 전송 객체, 사용자에게 음성 모델 리스트 제공 시 사용")
@Getter @Setter
@AllArgsConstructor
public class VoiceModelDTO {

    @Schema(description = "음성 모델의 식별자", example = "12")
    private Long voiceModelId;

    @Schema(description = "음성 모델의 이름", example = "My Voice Model")
    private String voiceModelName;
}
