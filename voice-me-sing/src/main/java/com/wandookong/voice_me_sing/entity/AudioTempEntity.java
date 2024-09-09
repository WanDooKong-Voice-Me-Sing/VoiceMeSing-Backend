package com.wandookong.voice_me_sing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
//@Setter
@Table(name = "audio_temp")
@NoArgsConstructor
public class AudioTempEntity {

    public AudioTempEntity(String originalFileName, String storedFileName, String filePath) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.filePath = filePath;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;
    private String storedFileName;
    private String filePath;
}
