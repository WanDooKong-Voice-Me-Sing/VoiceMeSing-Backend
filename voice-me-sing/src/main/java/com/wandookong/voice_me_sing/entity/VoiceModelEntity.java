package com.wandookong.voice_me_sing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "voice_model")
public class VoiceModelEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voiceModelId;
    private String voiceModelName;
    private String FilePath; // *** voiceModelFile
}
