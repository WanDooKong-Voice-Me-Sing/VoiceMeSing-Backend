package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.CreateSongDTO;
import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Song API", description = "Operations related to song creation\n노래 생성과 관련된 작업")
public class SongController {

    private final SongService songService;

    @Operation(
            summary = "Create a cover song\n커버 곡 생성",
            description = "Creates a cover song using the original audio and the provided voice model.\n원본 음원과 제공된 음성 모델을 사용하여 커버 곡을 생성"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File uploaded and created cover song successfully\n파일 업로드 및 커버 곡 생성 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject("{\"status\":\"success\",\"message\":\"cover song uploaded\"," +
                                    "\"data\":{\"resultSongName\":\"My Cover Song1\",\"songFile\":\"cover_song.mp3\"," +
                                    "\"voiceModelId\":123}}")
                    )
            )
    })
    @PostMapping(value = "/create-song", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadCoverSong(
            @Parameter(description = "Access token for authentication\n인증을 위한 access 토큰", required = true)
            @RequestHeader(value = "access") String accessToken,

            @Parameter(description = "The name of the result cover song\n결과 커버곡의 이름")
            @RequestPart(name = "resultSongName") String resultSongName,

            @RequestPart(name = "songFile") MultipartFile songFile,

            @Parameter(description = "ID of the voice model to be used\n사용할 음성 모델의 ID", required = true)
            @RequestPart(name = "voiceModelId") String voiceModelId) throws IOException {

        CreateSongDTO createSongDTO = new CreateSongDTO(resultSongName, songFile, Long.parseLong(voiceModelId));
        boolean success = songService.createCoverSong(createSongDTO, accessToken);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>("success", "cover song uploaded",
                Map.of(
                        "resultSongName", createSongDTO.getResultSongName(),
                        "songFileName", createSongDTO.getSongFile().getOriginalFilename(),
                        "voiceModelId", createSongDTO.getVoiceModelId()
                ));

        if (success) {
            return ResponseEntity.ok().body(responseDTO);
        } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO<String>("fail", "cover song upload failed", null));
    }

    @GetMapping("/collection-coversong")
    public ResponseEntity<?> getCoverSongs() {
        return null;
    }
}
