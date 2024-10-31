package com.wandookong.voice_me_sing.aiserver;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "")
@Setter
@Getter
@ToString
@AllArgsConstructor
public class VoiceModelRequestDTO {

    @Schema(description = "", example = "")
    private Long userId;

    @Schema(description = "", example = "")
    private String savePath;

    @Schema(description = "", example = "")
    private String voiceModelName;
}
