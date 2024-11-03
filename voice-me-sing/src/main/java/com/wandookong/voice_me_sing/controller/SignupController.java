package com.wandookong.voice_me_sing.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.dto.SignupDTO;
import com.wandookong.voice_me_sing.service.SignupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Signup API", description = "Handles user signup process\n사용자 가입 프로세스 처리")
public class SignupController {

    private final SignupService signupService;

    @Operation(
            summary = "User signup\n사용자 가입",
            description = "Registers a new user by providing signup information such as email, password, and nickname.\n이메일, 비밀번호, 닉네임 등 가입 정보를 제공하여 새로운 사용자를 등록"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Signup failed due to conflict (e.g. user already exists)\n가입 실패: 충돌 발생 (예: 사용자 이미 존재)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"signup fail\",\"data\":null}")
                    )
            ),
            @ApiResponse(responseCode = "200", description = "User successfully signed up\n사용자가 성공적으로 가입",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"signup success\",\"data\":" +
                                    "{\"email\":\"user@example.com\",\"nickname\":\"wandookong123\"}}")
                    )
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signupProcess(@RequestBody SignupDTO signupDTO) {

        // 회원 가입 프로세스
        boolean signupSuccess = signupService.signup(signupDTO);

        // 응답 생성
        if (signupSuccess) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> signupInfo = objectMapper.convertValue(signupDTO, new TypeReference<Map<String, Object>>() {
            });
            signupInfo.remove("password");
            ResponseDTO<Map<String, Object>> success = new ResponseDTO<>("success", "signup success", signupInfo);

            return ResponseEntity.status(HttpStatus.CREATED).body(success);
        } else {
            ResponseDTO<Object> fail = new ResponseDTO<>("fail", "signup fail", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(fail);
        }

//        if (signupSuccess) return "signup success";
//        else return "signup failed";
    }
}
