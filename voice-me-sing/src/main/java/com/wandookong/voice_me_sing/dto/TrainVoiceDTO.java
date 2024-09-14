package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "음성 파일 업로드와 모델 이름 설정에 필요한 데이터 전송 객체")
@Getter @Setter
@ToString
@AllArgsConstructor
public class TrainVoiceDTO {

    @Schema(description = "음성 모델 학습 후 설정할 모델 이름", example = "myVoiceModel")
    private String modelName;

    @Schema(description = "업로드할 음성 파일", type = "string", format = "binary")
    private MultipartFile voiceFile;
}
