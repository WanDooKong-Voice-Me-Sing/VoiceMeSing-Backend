package com.wandookong.voice_me_sing.aiserver;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "AI 서버가 커버곡을 생성하는 데 필요한 정보를 담은 데이터 전송 객체")
@Getter
@Setter
@ToString
@AllArgsConstructor
public class CoverSongRequestDTO {

    @Schema(description = "", example = "2")
    private String coverSongId;

//    @Schema(description = "", example = "")
//    private Long userId;
//
//    @Schema(description = "", example = "")
//    private String savePath;
//
//    @Schema(description = "", example = "")
//    private Long voiceModelId;
//
//    @Schema(description = "", example = "")
//    private String resultSongName;
}
