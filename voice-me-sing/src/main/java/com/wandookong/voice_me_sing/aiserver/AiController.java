package com.wandookong.voice_me_sing.aiserver;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiController {

    @PostMapping("/api/voice-model")
    public String toJava(@RequestBody VoiceModelResponse response) {
        String userId = response.getUserId();
        String voiceModelId = response.getVoiceModelId();

        return "Received from Python server";
    }
}
