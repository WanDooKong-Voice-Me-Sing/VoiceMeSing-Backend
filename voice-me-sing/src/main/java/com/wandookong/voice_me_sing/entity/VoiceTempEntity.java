package com.wandookong.voice_me_sing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "voice_temp")
@NoArgsConstructor
public class VoiceTempEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voiceId;

    @Lob // 바이너리 데이터를 위한 annotation
    private byte[] voiceFile;

    private String voiceModelName;

    private String userId;
}
