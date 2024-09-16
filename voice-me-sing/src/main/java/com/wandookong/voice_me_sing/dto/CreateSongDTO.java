package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "커버 곡 생성에 필요한 데이터 전송 객체")
@Getter
@Setter
@ToString
@AllArgsConstructor
public class CreateSongDTO {

    @Schema(description = "생성될 커버곡의 이름", example = "My Cover Song")
    private String resultSongName;

    @Schema(description = "업로드할 음원 파일", type = "string", format = "binary", example = "cover_song.mp3")
    private MultipartFile songFile;

    @Schema(description = "사용할 음성 모델의 ID", example = "123")
    private Long voiceModelId;
}
