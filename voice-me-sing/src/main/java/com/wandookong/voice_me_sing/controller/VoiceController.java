package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.dto.TrainVoiceDTO;
import com.wandookong.voice_me_sing.dto.VoiceModelDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Voice API", description = "Operations for managing voice files and voice models. 음성 파일 및 음성 모델을 관리하는 작업")
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
                    description = "File uploaded and voice model created successfully.\n파일 업로드 및 음성 모델 생성 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"file upload success\",\"data\":" +
                                    "{\"voiceFileName\":\"voice.mp3\",\"modelName\":\"My Voice Model1\"}}")
                    )
            )
    })
    @PostMapping(value = "/train-voice", consumes = "multipart/form-data")
    // 음성 모델 생성: 1. 사용자 음성 저장(FE->BE) 2. AI 서버에 음성 모델 생성 요청(BE->AI)
    public ResponseEntity<?> uploadVoiceFile(
            @RequestHeader("access") String accessToken,
            @RequestPart(name = "voiceModelName") String voiceModelName,
            @RequestPart(name = "voiceFile") MultipartFile voiceFile) throws IOException { // *** IO

        TrainVoiceDTO trainVoiceDTO = new TrainVoiceDTO(voiceModelName, voiceFile);
        boolean success = voiceService.saveVoiceFile(trainVoiceDTO, accessToken);

        // 저장 결과 리턴
        if (success) {
            ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>("success", "file uploaded", Map.of(
                    "voiceFileName", voiceFile.getOriginalFilename(),
                    "modelName", voiceModelName
            ));
            return ResponseEntity.ok().body(responseDTO);
        } else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO<String>("fail", "file upload failed", null));
    }

    @Operation(
            summary = "Retrieve user's voice model list\n사용자의 음성 모델 리스트 조회",
            description = "Fetches the list of voice models for a user using the provided access token\n제공된 액세스 토큰을 사용하여 사용자의 음성 모델 리스트 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of voice models\n사용자의 음성 모델 리스트를 성공적으로 조회",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"get voice models successfully\",\"data\":" +
                                    "[{\"voiceModelId\":4,\"voiceModelName\":\"My Voice Model1\"},{\"voiceModelId\":12,\"voiceModelName\":\"My Voice Model2\"}]}")
                    )
            )
    })
    @GetMapping("/collection-model")
    // 사용자의 음성 모델 리스트 조회
    public ResponseEntity<?> getVoiceModels(
            @Parameter(description = "Access token for authentication\n인증을 위한 엑세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken) {

        List<VoiceModelDTO> voiceModelDTOs = voiceService.getVoiceModels(accessToken);

        // 응답 데이터 생성
        ResponseDTO<List<VoiceModelDTO>> responseDTO = new ResponseDTO<>("success", "get voice models successfully", voiceModelDTOs);
        return ResponseEntity.ok().body(responseDTO);

//        if (voiceModelDTOs.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<String>("fail", "no user", null));
//        } else {
//
//        }
    }

    @Operation(
            summary = "Delete voice model\n음성 모델 삭제",
            description = "Deletes the specified voice model based on the provided ID.\n지정된 ID를 기반으로 음성 모델을 삭제"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully deleted the voice model\n성공적으로 음성 모델을 삭제",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject("{\"status\":\"success\",\"message\":\"model deleted\",\"data\":null}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Voice model not found\n음성 모델을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject("{\"status\":\"fail\",\"message\":\"model not found\",\"data\":null}")
                    )
            )
    })
    @DeleteMapping("/model-delete")
    public ResponseEntity<?> deleteVoiceModel(
            @Parameter(description = "Voice model ID to delete\n삭제할 음성 모델의 ID", required = true)
            @RequestParam String voiceModelId) {

        boolean deleted = voiceService.deleteVoiceModel(Long.valueOf(voiceModelId));

        if (deleted) {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("success", "model deleted", null);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "model not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

}
