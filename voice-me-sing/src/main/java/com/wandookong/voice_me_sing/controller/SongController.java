package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.TrainVoiceProcessDTO;
import com.wandookong.voice_me_sing.service.VoiceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SongController {

    private final VoiceService voiceService;

    @PostMapping("/train-voice") // 1. 사용자 음성 저장 2. AI 서버에 음성 **모델 생성** 요청
    public ResponseEntity<?> uploadAudio(TrainVoiceProcessDTO trainVoiceProcessDTO) throws IOException { // *** IO

        return voiceService.saveAudioFile(trainVoiceProcessDTO);
    }

    @GetMapping("/collection-model") // **모델 리스트**
    public ResponseEntity<?> getVoiceModels(HttpServletRequest request) {
        String userToken = request.getHeader("access");

        return voiceService.getVoiceModels(userToken);
    }



    @PostMapping("create-song")
    public String createSong() {
        return "create-song";
    }

    @GetMapping("/collection-song")
    public String collectionSong() {
        return "collection-song";
    }

    @GetMapping("/collection/{id}")
    public String collection(@PathVariable int id) {
        return "collection";
    }

    @GetMapping("profile")
    public String profile() {
        return "profile";
    }

}
