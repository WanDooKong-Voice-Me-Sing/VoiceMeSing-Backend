package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoverSongIdDTO {

    @Schema(description = "커버곡의 식별자", example = "3")
    private String coverSongId;
}
