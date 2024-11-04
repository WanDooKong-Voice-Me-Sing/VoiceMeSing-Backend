package com.wandookong.voice_me_sing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cover_song")
@NoArgsConstructor
@AllArgsConstructor
public class CoverSongEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coverSongId;

    private String resultSongName;

    private byte[] coverSongFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
