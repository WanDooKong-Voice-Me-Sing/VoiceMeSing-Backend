package com.wandookong.voice_me_sing.aiserver;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "", description = "")
public class AiController {


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
                            examples = @ExampleObject()
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject()
                    )
            )
    })
    @PostMapping("/api/voice-model")
    public String toJava(@RequestBody VoiceModelResponseDTO response) {
        String userId = response.getUserId();
        String voiceModelId = response.getVoiceModelId();

        System.out.println(userId + " " + voiceModelId + " completed");

        return "Received " + userId + " " + voiceModelId + " from Python server";
    }


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
                            examples = @ExampleObject()
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject()
                    )
            )
    })
    @PostMapping("/api/cover-song")
    public String toJava(@RequestBody CoverSongResponseDTO response) {
        String userId = response.getUserId();
        String coverSongId = response.getCoverSongId();

        System.out.println(userId + " " + coverSongId + " completed");
        return "Received " + userId + " " + coverSongId + " from Python server";
    }
}
