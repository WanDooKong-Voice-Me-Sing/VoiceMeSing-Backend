package com.wandookong.voice_me_sing.aiserver;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "AI 서버가 음성 모델을 생성하는 데 필요한 정보를 담은 데이터 전송 객체")
@Setter
@Getter
@ToString
@AllArgsConstructor
public class VoiceModelRequestDTO {

    @Schema(description = "", example = "4")
    private String voiceId;

//    @Schema(description = "", example = "")
//    private Long userId;
//
//    @Schema(description = "", example = "")
//    private String savePath;
//
//    @Schema(description = "", example = "")
//    private String voiceModelName;
}
