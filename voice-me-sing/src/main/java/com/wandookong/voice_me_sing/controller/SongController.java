package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.CoverSongDTO;
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
import java.util.List;
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
            @Parameter(description = "Access token for authentication\n인증을 위한 엑세스 토큰", required = true)
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
        } else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO<String>("fail", "cover song upload failed", null));
    }

    @Operation(
            summary = "Get user cover song list\n사용자의 커버 음악 리스트 조회",
            description = "Retrieves the list of cover songs associated with the authenticated user\n인증된 사용자의 커버 음악 리스트를 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the cover song list\n커버 음악 리스트를 성공적으로 조회",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"get cover songs successfully\",\"data\":[{\"coverSongId\":1,\"coverSongName\":\"Song One\"},{\"coverSongId\":2,\"coverSongName\":\"Song Two\"}]}")
                    )
            )
    })
    @GetMapping("/collection-coversong")
    // 사용자의 커버 음악 리스트 조회
    public ResponseEntity<?> getCoverSongs(@Parameter(description = "Access token for authentication\n인증을 위한 엑세스 토큰", required = true)
                                           @RequestHeader(value = "access") String accessToken) {

        List<CoverSongDTO> coverSongDTOs = songService.getCoverSongs(accessToken);

        ResponseDTO<List<CoverSongDTO>> responseDTO = new ResponseDTO<>("success", "get cover songs successfully", coverSongDTOs);
        return ResponseEntity.ok().body(responseDTO);

//        if (coverSongDTOs.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<String>("fail", "no user", null));
//        } else {
//
//        }
    }

    @Operation(
            summary = "Delete user's cover song\n사용자의 커버 음악 삭제",
            description = "Deletes the specified cover song based on the provided cover song ID\n제공된 커버 음악 ID를 기반으로 해당 사용자의 커버 음악을 삭제"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully deleted the cover song\n커버 음악을 성공적으로 삭제",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"song deleted\",\"data\":null}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cover song not found\n커버 음악을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"song not found\",\"data\":null}")
                    )
            )
    })
    @DeleteMapping("/coversong-delete")
    // 사용자의 커버 음악 삭제
    public ResponseEntity<?> deleteCoverSong(@RequestParam String coverSongId) {

        boolean deleted = songService.deleteCoverSong(Long.parseLong(coverSongId));

        if (deleted) {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("success", "song deleted", null);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "song not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }
}
