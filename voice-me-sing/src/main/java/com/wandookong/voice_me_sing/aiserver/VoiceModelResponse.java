package com.wandookong.voice_me_sing.aiserver;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
public class VoiceModelResponse {

    private String userId;
    private String voiceModelId;

}
