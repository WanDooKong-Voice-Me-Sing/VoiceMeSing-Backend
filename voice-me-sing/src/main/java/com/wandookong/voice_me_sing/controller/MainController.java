package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Main API", description = "main page\n메인 페이지")
public class MainController {

    @GetMapping("/")
    @ApiResponse(
            responseCode = "200",
            description = "main page successfully loaded\n메인 페이지 로드",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDTO.class),
                    examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"main page\",\"data\":null}")
            )
    )
    public ResponseEntity<?> mainProcess() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        ResponseDTO<String> responseDTO = new ResponseDTO<>("success", "main page", null);
        return ResponseEntity.ok().body(responseDTO);

//        return "main page";
    }

}
