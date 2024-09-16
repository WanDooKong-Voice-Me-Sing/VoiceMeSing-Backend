package com.wandookong.voice_me_sing.aiserver;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "")
@Getter
@Setter
@ToString
public class CoverSongResponseDTO {

    @Schema(description = "", example = "")
    private String userId;

    @Schema(description = "", example = "")
    private String coverSongId;

}
