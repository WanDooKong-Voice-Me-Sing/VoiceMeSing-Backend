package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.CoverSongDTO;
import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommunityController {

    private final SongService songService;

    @GetMapping("/community")
    public ResponseEntity<?> getAllPublicCoverSongs() {

        // 공개된 전체 커버곡 조회
        List<CoverSongDTO> coverSongDTOList = songService.findAllCoverSongs();

        // 응답 생성
        ResponseDTO<List<CoverSongDTO>> responseDTO = new ResponseDTO<>("success", "get all cover songs successfully", coverSongDTOList);
        return ResponseEntity.ok().body(responseDTO);
    }
}
