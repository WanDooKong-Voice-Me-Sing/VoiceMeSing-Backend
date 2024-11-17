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

    // 음성 모델 생성 요청 API
    public void toPythonVoiceModel(String voiceId) {
        String pythonServerEndpoint = pythonServerUrl + "/api/model/create";
        VoiceModelRequestDTO voiceModelRequestDTO = new VoiceModelRequestDTO(voiceId);

        restTemplate.postForObject(pythonServerEndpoint, voiceModelRequestDTO, String.class);

        System.out.println("AiService.toPythonCoverSong: Sent to python server");

//        return message != null;\
//        return true;
    }

    // 커버곡 생성 요청 API
    public void toPythonCoverSong(String coverSongId) {
        String pythonServerEndpoint = pythonServerUrl + "/api/coversong/create";
        CoverSongRequestDTO coverSongRequestDTO = new CoverSongRequestDTO(coverSongId);

        restTemplate.postForObject(pythonServerEndpoint, coverSongRequestDTO, String.class);

        System.out.println("AiService.toPythonCoverSong: Sent to python server");

//        return message != null;
//        return true;
    }

    // 수정 전 메소드:
//    public boolean toPythonCoverSong(Long userId, String savePath, Long voiceModelId, String resultSongName) {
//
//        CoverSongRequestDTO coverSongRequestDTO = new CoverSongRequestDTO(userId, savePath, voiceModelId, resultSongName);
//
//        String message = restTemplate.postForObject(pythonServerUrl, coverSongRequestDTO, String.class);
//
//        System.out.println("AiServer:" + message);
//        System.out.println("AiService.toPythonCoverSong: Sent to python server");
//
//        return message != null;
//    }
//    public boolean toPythonVoiceModel(Long userId, String savePath, String voiceModelName) {
//
//        VoiceModelRequestDTO voiceModelRequestDTO = new VoiceModelRequestDTO(userId, savePath, voiceModelName);
//
//        String message = restTemplate.postForObject(pythonServerUrl, voiceModelRequestDTO, String.class);
//
//        System.out.println("AiServer:" + message);
//        System.out.println("AiService.toPythonVoiceModel: Sent to python server");
//
//        return message != null;
//    }
}
