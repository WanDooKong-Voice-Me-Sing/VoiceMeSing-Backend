package com.wandookong.voice_me_sing.aiserver;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "")
@Getter
@Setter
@ToString
@AllArgsConstructor
public class CoverSongRequestDTO {
    @Schema(description = "", example = "")
    private Long userId;

    @Schema(description = "", example = "")
    private String savePath;

    @Schema(description = "", example = "")
    private Long voiceModelId;

    @Schema(description = "", example = "")
    private String resultSongName;
}
