package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.dto.TrainVoiceDTO;
import com.wandookong.voice_me_sing.service.VoiceService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Voice API", description = "Endpoints for managing voice files and voice models. 음성 파일 및 음성 모델을 관리하는 엔드포인트")
public class VoiceController {

    private final VoiceService voiceService;

    @Operation(
            summary = "Upload a voice file and create voice model\n음성 파일 업로드 및 음성 모델 생성",
            description = "Handles the upload of an voice file and creates a voice model from it.\n" +
                    "The uploaded file is processed, and a new voice model is created for the provided model name.\n" +
                    "음성 파일을 업로드하고 사용자가 지정한 모델 이름으로 음성 모델을 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File uploaded and voice model created successfully.\n파일이 업로드되었고 음성 모델이 성공적으로 생성",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"file upload success\",\"data\":" +
                                    "{\"voiceFileName\":\"file1.model\",\"modelName\":\"내 음성 모델1\"}}")
                    )
            )
    })
    @PostMapping("/train-voice") // 1. FE->BE::사용자 음성 저장 2. BE->AI::AI 서버에 음성 **모델 생성** 요청
    public ResponseEntity<?> uploadVoiceFile(
            @RequestHeader("access") String accessToken,
            @RequestPart(name = "modelName", required = true) String voiceModelName,
            @RequestPart(name = "voiceFile", required = true) MultipartFile voiceFile) throws IOException { // *** IO

        TrainVoiceDTO trainVoiceDTO = new TrainVoiceDTO(voiceModelName, voiceFile);
        boolean success = voiceService.saveVoiceFile(trainVoiceDTO, accessToken);

        // 저장 결과 리턴
        if (success) {
            ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>("success", "file uploaded", Map.of(
                    "voiceFileName", voiceFile.getOriginalFilename(),
                    "modelName", voiceModelName
            ));
            return ResponseEntity.ok().body(responseDTO);
        }
        else return null;
    }

    @Operation(
            summary = "Retrieve user's voice model list\n사용자의 음성 모델 리스트 조회",
            description = "Fetches the list of voice models for a user using the provided access token.\n제공된 액세스 토큰을 사용하여 사용자의 음성 모델 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, no user found with the provided access token.\n사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"no user\",\"data\":null}")
                    )),
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of voice models.\n사용자의 음성 모델 리스트를 성공적으로 조회",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"get voice models successfully\",\"data\":" +
                                    "[{\"voiceModelId\":\"4\",\"voiceModelName\":\"내 음성 모델1\"},{\"voiceModelId\":\"12\",\"voiceModelName\":\"내 음성 모델2\"}]}")
                    ))
    })
    @GetMapping("/collection-model") // **모델 리스트**
    public ResponseEntity<?> getVoiceModels( // *** 분리하기
            @Parameter(description = "access token", required = true)
            @RequestHeader("access") String accessToken) {

        return voiceService.getVoiceModels(accessToken);

    }

}
