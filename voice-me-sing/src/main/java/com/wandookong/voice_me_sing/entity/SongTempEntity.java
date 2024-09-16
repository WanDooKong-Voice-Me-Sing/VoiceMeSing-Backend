package com.wandookong.voice_me_sing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "song_temp")
@NoArgsConstructor
public class SongTempEntity {

    public SongTempEntity(String originalSongFileName, String storedSongFileName, String songFilePath) {
        this.originalSongFileName = originalSongFileName;
        this.storedSongFileName = storedSongFileName;
        this.songFilePath = songFilePath;
    }


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long songId;

    private String originalSongFileName;
    private String storedSongFileName;
    private String songFilePath;
}
