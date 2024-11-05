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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long songId;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] coverSongFile;

    private String resultSongName;

    private String voiceModelId;

    private String userId;
}
