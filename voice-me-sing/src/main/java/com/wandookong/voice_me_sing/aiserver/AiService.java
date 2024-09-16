package com.wandookong.voice_me_sing.aiserver;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;

    @Value("${spring.pythonServerUrl}")
    private String pythonServerUrl;

    public boolean toPythonVoiceModel(Long userId, Long voiceId, String voiceModelName) {

        VoiceModelRequestDTO voiceModelRequestDTO = new VoiceModelRequestDTO(userId, voiceId, voiceModelName);

        String message = restTemplate.postForObject(pythonServerUrl, voiceModelRequestDTO, String.class);

        System.out.println("AiServer:" + message);
        System.out.println("AiService.toPythonVoiceModel: Sent to python server");

        return message != null;
    }

    public boolean toPythonCoverSong(Long userId, Long songId, Long voiceModelId) {

        CoverSongRequestDTO coverSongRequestDTO = new CoverSongRequestDTO(songId, userId, voiceModelId);

        String message = restTemplate.postForObject(pythonServerUrl, coverSongRequestDTO, String.class);
        
        System.out.println("AiServer:" + message);
        System.out.println("AiService.toPythonCoverSong: Sent to python server");

        return message != null;
    }

}
