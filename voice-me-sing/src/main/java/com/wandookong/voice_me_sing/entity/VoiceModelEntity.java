package com.wandookong.voice_me_sing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "voice_model")
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties({"userEntity"})
public class VoiceModelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voiceModelId;

    private String voiceModelName;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] voiceModelFile; // => filePath 로 변경 ?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
