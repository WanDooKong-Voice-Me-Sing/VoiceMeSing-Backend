package com.wandookong.voice_me_sing.aiserver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;

    public void toPython(String userId, String voiceId) {

        String pythonServerUrl = "http://219.254.44.166:8001/get_voice"; // ***

        VoiceModelRequest voiceModelRequest = new VoiceModelRequest(userId, voiceId);

        String result = restTemplate.postForObject(pythonServerUrl, voiceModelRequest, String.class);

        System.out.println(result);
        System.out.println("Sent to python server");
    }

}
