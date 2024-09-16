package com.wandookong.voice_me_sing.aiserver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;

    public boolean toPythonVoiceModel(Long userId, Long voiceId, String voiceModelName) {

        String pythonServerUrl = "http://219.254.44.166:8001/get_voice"; // ***

        VoiceModelRequestDTO voiceModelRequestDTO = new VoiceModelRequestDTO(userId, voiceId, voiceModelName);

        String message = restTemplate.postForObject(pythonServerUrl, voiceModelRequestDTO, String.class);

        System.out.println("AiServer:" + message);
        System.out.println("AiService.toPythonVoiceModel: Sent to python server");

        return message != null;
    }

    public boolean toPythonCoverSong(Long userId, Long songId, Long voiceModelId) {

        String pythonServerUrl = "http://219.254.44.166:8001/get_voice"; // ***

        CoverSongRequestDTO coverSongRequestDTO = new CoverSongRequestDTO(songId, userId, voiceModelId);

        String message = restTemplate.postForObject(pythonServerUrl, coverSongRequestDTO, String.class);
        
        System.out.println("AiServer:" + message);
        System.out.println("AiService.toPythonCoverSong: Sent to python server");

        return message != null;
    }

}
