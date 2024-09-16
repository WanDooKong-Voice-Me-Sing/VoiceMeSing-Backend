package com.wandookong.voice_me_sing.aiserver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
@AllArgsConstructor
public class VoiceModelRequest {

    private String userId;
    private String voiceId;
}
