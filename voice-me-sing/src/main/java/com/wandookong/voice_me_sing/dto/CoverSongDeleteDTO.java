package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoverSongDeleteDTO {

    @Schema(description = "삭제할 커버곡의 식별자", example = "3")
    private String coverSongId;
}
