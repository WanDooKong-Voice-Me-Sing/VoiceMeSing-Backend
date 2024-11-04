package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "커버 음원의 식별자와 이름을 담고 있는 데이터 전송 객체, 사용자에게 커버 음원 리스트 제공 시 사용")
@Getter
@Setter
@AllArgsConstructor
public class CoverSongDTO {

    @Schema(description = "커버 음원의 식별자", example = "12")
    private Long coverSongId;

    @Schema(description = "커버 음원의 이름", example = "My Cover Song.mp3")
    private String coverSongName;

    @Schema(description = "커버곡 파일", example = "My Cover Song.mp3")
    private byte[] coverSongFile;
}
