package com.wandookong.voice_me_sing.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "", description = "")
public class SongController {

    private final SongService songService;

    @Operation(
            summary = "",
            description = ""
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject("{\"status\":\"success\",\"message\":\"get profile\",\"data\":{" +
                                    "\"nickname\":\"exampleNickname123\"}}")
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject("{\"status\":\"fail\",\"message\":\"no user found\",\"data\":null}")
                    )
            )
    })
    @PostMapping("/create-song")
    public ResponseEntity<?> uploadCoverSong(
            @Parameter(description = "", required = true)
            @RequestHeader(value = "access", required = false) String accessToken,
            CreateSongDTO createSongDTO) throws IOException {

        boolean success = songService.createCoverSong(createSongDTO, accessToken);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> createSongDTOMap = objectMapper.convertValue(createSongDTO, new TypeReference<Map<String, Object>>() {});
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>("success", "cover song uploaded", createSongDTOMap);

        if (success) {
            return ResponseEntity.ok().body(responseDTO);
        }
        return null;
    }

}
