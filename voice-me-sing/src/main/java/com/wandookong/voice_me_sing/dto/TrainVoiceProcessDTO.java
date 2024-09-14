package com.wandookong.voice_me_sing.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@ToString
public class TrainVoiceProcessDTO {
    private String modelName; // 음성 모델 학습 후 이름 설정
    private MultipartFile voiceFile; // 음성 파일
}
