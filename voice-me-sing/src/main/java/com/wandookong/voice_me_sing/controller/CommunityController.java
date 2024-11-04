package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.CoverSongDTO;
import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "Community Forum API", description = "Handles operations related to cover songs\n커뮤니티와 관련된 작업을 처리")
public class CommunityController {

    private final SongService songService;

    @Operation(
            summary = "전체 공개 커버곡 조회",
            description = "모든 공개된 커버곡 리스트를 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "전체 커버곡 조회 성공\nSuccessfully retrieved all cover songs",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"get all cover songs successfully\",\"data\":[{\"coverSongId\":1,\"resultSongName\":\"Song A\",\"coverSongFile\":\"fileA.mp3\",\"isPublic\":true},{\"coverSongId\":2,\"resultSongName\":\"Song B\",\"coverSongFile\":\"fileB.mp3\",\"isPublic\":true}]}")
                    )
            )
    })
    @GetMapping("/community")
    public ResponseEntity<?> getAllPublicCoverSongs() {

        // 공개된 전체 커버곡 조회
        List<CoverSongDTO> coverSongDTOList = songService.findAllCoverSongs();

        // 응답 생성
        ResponseDTO<List<CoverSongDTO>> responseDTO = new ResponseDTO<>("success", "get all cover songs successfully", coverSongDTOList);
        return ResponseEntity.ok().body(responseDTO);
    }
}
