package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.service.UserService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "Operations related to user\n사용자 관련 작업")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get user profile\n사용자 프로필 조회",
            description = "Retrieves the user's profile information based on the access token.\n액세스 토큰을 기반으로 사용자의 프로필 정보 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user profile\n사용자 프로필 성공적으로 조회",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject("{\"status\":\"success\",\"message\":\"get profile\",\"data\":{" +
                                    "\"nickname\":\"wandookong123\"}}")
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No user found\n사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject("{\"status\":\"fail\",\"message\":\"no user found\",\"data\":null}")
                    )
            )
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@Parameter(description = "Access token for authentication\n인증을 위한 access 토큰", required = true)
                                        @RequestHeader(value = "access") String accessToken) {

//        String accessToken = request.getHeader("access");
//        String email = jwtUtil.getEmail(accessToken);

        String nickname = userService.getNickname(accessToken);

        if (nickname == null) {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "no user found", null);
            return ResponseEntity.status(401).body(responseDTO);
        } else {
            ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>("success", "get profile",
                    Map.of("nickname", nickname));
            return ResponseEntity.status(200).body(responseDTO);
        }
    }
}
