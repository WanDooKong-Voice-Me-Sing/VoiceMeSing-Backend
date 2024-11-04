package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "커버곡 식별을 위한 데이터 전송 객체\nData Transfer Object for identifying a cover song")
public class CoverSongIdDTO {

    @Schema(description = "커버곡의 식별자", example = "3")
    private String coverSongId;
}