package com.wandookong.voice_me_sing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
//@Setter
@Table(name = "audio_temp")
@NoArgsConstructor
public class VoiceTempEntity {

    public VoiceTempEntity(String originalVoiceFileName, String storedVoiceFileName, String voiceFilePath, String modelName) {
        this.originalVoiceFileName = originalVoiceFileName;
        this.storedVoiceFileName = storedVoiceFileName;
        this.voiceFilePath = voiceFilePath;
        this.modelName = modelName;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalVoiceFileName;
    private String storedVoiceFileName;
    private String voiceFilePath;
    private String modelName;
}
