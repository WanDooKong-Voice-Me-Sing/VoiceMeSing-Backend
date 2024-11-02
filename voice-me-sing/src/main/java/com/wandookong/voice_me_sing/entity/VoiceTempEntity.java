package com.wandookong.voice_me_sing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
//@Setter
@Table(name = "voice_temp")
@NoArgsConstructor
public class VoiceTempEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voiceId;

    private String originalVoiceFileName;
    private String storedVoiceFileName;
    private String voiceFilePath;

    public VoiceTempEntity(String originalVoiceFileName, String storedVoiceFileName, String voiceFilePath) {
        this.originalVoiceFileName = originalVoiceFileName;
        this.storedVoiceFileName = storedVoiceFileName;
        this.voiceFilePath = voiceFilePath;
    }
//    private String modelName;
}
