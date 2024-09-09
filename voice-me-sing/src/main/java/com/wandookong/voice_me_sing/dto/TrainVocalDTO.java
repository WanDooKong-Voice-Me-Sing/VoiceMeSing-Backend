package com.wandookong.voice_me_sing.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@ToString
public class TrainVocalDTO {
//    private String voiceFileName;
    private MultipartFile voiceFile;
}
