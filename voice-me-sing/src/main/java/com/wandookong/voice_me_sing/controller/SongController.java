package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.dto.TrainVocalDTO;
import com.wandookong.voice_me_sing.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping("/train-vocal") // 1. 사용자 음성 저장 2. AI 서버에 음성 모델 생성 요청
    public ResponseEntity<?> uploadAudio(TrainVocalDTO trainVocalDTO) throws IOException {

        songService.saveAudioFile(trainVocalDTO);

        String fileName = trainVocalDTO.getVoiceFile().getOriginalFilename();
        ResponseDTO responseDTO = new ResponseDTO("success", "file upload success", Map.of("fileName", fileName));

        return ResponseEntity.ok().body(responseDTO);
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

    @GetMapping("/collection-model")
    public String collectionModel() {
        return "collection-model";
    }

    @GetMapping("profile")
    public String profile() {
        return "profile";
    }

}
