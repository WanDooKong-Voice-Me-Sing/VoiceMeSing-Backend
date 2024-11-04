package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.CoverSongDTO;
import com.wandookong.voice_me_sing.dto.CoverSongIdDTO;
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
            description = "Creates a cover song using the original audio file and a specified voice model ID. The user must provide an access token for authentication, along with the name of the resulting cover song and the audio file to be transformed\n사용자는 인증을 위해 액세스 토큰을 제공해야 하며, 커버곡 이름과 변환할 오디오 파일을 함께 전송하여 커버 곡을 생성"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File uploaded and cover song created successfully\n파일 업로드 및 커버 곡 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"cover song uploaded\",\"data\":{\"resultSongName\":\"My Cover Song1\",\"songFileName\":\"cover_song.mp3\",\"voiceModelId\":123}}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cover song creation failed due to invalid data\n유효하지 않은 데이터로 인한 커버 곡 생성 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"cover song upload failed\",\"data\":null}")
                    )
            )
    })
    @PostMapping(value = "/create-song", consumes = "multipart/form-data")
    // 커버곡 생성
    public ResponseEntity<?> uploadSong(
            @Parameter(description = "Access token for authentication\n인증을 위한 액세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken,

            @Parameter(description = "Name of the resulting cover song\n생성할 커버곡의 이름", required = true, example = "My Cover Song")
            @RequestPart(name = "resultSongName") String resultSongName,

            @Parameter(description = "Audio file to be transformed\n변환할 오디오 파일", required = true, example = "cover_song.mp3", schema = @Schema(type = "string", format = "binary"))
            @RequestPart(name = "songFile") MultipartFile songFile,

            @Parameter(description = "ID of the voice model to be used\n사용할 음성 모델의 ID", required = true, example = "123")
            @RequestPart(name = "voiceModelId") String voiceModelId) throws IOException {

        // 커버곡 생성 프로세스
        CreateSongDTO createSongDTO = new CreateSongDTO(resultSongName, songFile, voiceModelId);
        boolean success = songService.saveSongBlobFile(createSongDTO, accessToken);

        // 응답 생성
        if (success) {
            ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>("success", "cover song uploaded",
                    Map.of(
                            "resultSongName", createSongDTO.getResultSongName(),
                            "songFileName", createSongDTO.getSongFile().getOriginalFilename(),
                            "voiceModelId", createSongDTO.getVoiceModelId()
                    ));
            return ResponseEntity.ok().body(responseDTO);
        } else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO<String>("fail", "cover song upload failed", null));
    }

    @Operation(
            summary = "사용자의 커버 음악 리스트 조회",
            description = "사용자의 인증된 커버 음악 리스트를 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "커버 음악 리스트 조회 성공\nCover songs retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"get cover songs successfully\",\"data\":[{\"coverSongId\":12,\"coverSongName\":\"My Cover Song.mp3\",\"coverSongFile\":\"<byte array>\",\"isPublic\":true}]}")
                    )
            )
    })
    @GetMapping("/collection-coversong")
    // 사용자의 커버 음악 리스트 조회
    public ResponseEntity<?> getCoverSongs(
            @Parameter(description = "Access token for authentication\n인증을 위한 엑세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken) {

        // 커버곡 리스트 조회 프로세스
        List<CoverSongDTO> coverSongDTOs = songService.getCoverSongs(accessToken);

        // 응답 생성
        ResponseDTO<List<CoverSongDTO>> responseDTO = new ResponseDTO<>("success", "get cover songs successfully", coverSongDTOs);
        return ResponseEntity.ok().body(responseDTO);
    }

    @Operation(
            summary = "Delete a user's cover song\n사용자의 커버 음악 삭제",
            description = "Deletes a specified cover song based on its ID\n지정된 ID에 해당하는 커버곡을 삭제"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cover song deleted successfully\n커버곡 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"song deleted\",\"data\":null}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cover song not found\n커버곡을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"song not found\",\"data\":null}")
                    )
            )
    })
    @DeleteMapping("/coversong-delete")
    // 사용자의 커버곡 삭제
    public ResponseEntity<?> deleteCoverSong(
            @Parameter(description = "Access token for user authentication\n사용자 인증을 위한 엑세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken,

            @Parameter(description = "DTO containing the ID of the cover song to delete\n삭제할 커버곡의 ID를 포함하는 DTO", required = true)
            @RequestBody CoverSongIdDTO coverSongIdDTO) {

        // 커버곡 삭제 프로세스
        String coverSongId = coverSongIdDTO.getCoverSongId();
        boolean deleted = songService.deleteCoverSong(accessToken, Long.valueOf(coverSongId));

        // 응답 생성
        if (deleted) {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("success", "song deleted", null);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "song not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    @Operation(
            summary = "Toggle the public status of a cover song\n커버곡의 공개 여부 토글",
            description = "Toggles the visibility status of a specified cover song based on its ID\n지정된 ID에 해당하는 커버곡의 공개 여부를 토글"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Visibility toggled successfully\n공개 여부가 성공적으로 변경됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"visibility toggled\",\"data\":true}")
                    )
            )
    })
    @PatchMapping("/coversong-toggle")
    public ResponseEntity<?> togglePublicStatus(
            @Parameter(description = "Access token for user authentication\n사용자 인증을 위한 엑세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken,

            @Parameter(description = "DTO containing the ID of the cover song to toggle the visibility\n공개 여부를 수정할 커버곡의 ID를 포함하는 DTO", required = true)
            @RequestBody CoverSongIdDTO coverSongIdDTO) {

        // 공개 여부 토글 프로세스
        Object result = songService.togglePublicStatus(accessToken, coverSongIdDTO);

        // 응답 생성
        if (result instanceof Boolean) {
            ResponseDTO<Boolean> responseDTO = new ResponseDTO<>("success", "visibility toggled", (Boolean) result);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else return null;
    }
}
