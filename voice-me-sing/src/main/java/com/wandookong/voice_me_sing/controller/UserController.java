package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.dto.UpdateDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getProfile(
            @Parameter(description = "Access token for authentication\n인증을 위한 엑세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken) {

//        String accessToken = request.getHeader("access");
//        String email = jwtUtil.getEmail(accessToken);

        String nickname = userService.getNicknameByToken(accessToken);

        if (nickname == null) {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "no user found", null);
            return ResponseEntity.status(401).body(responseDTO);
        } else {
            ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>("success", "get profile",
                    Map.of("nickname", nickname));
            return ResponseEntity.status(200).body(responseDTO);
        }
    }

    @Operation(
            summary = "Delete user account\n사용자 계정 삭제",
            description = "Deletes the user's account based on the access token.\n액세스 토큰을 기반으로 사용자의 계정을 삭제"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully deleted user account\n사용자 계정 성공적으로 삭제",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject("{\"status\":\"success\",\"message\":\"account deleted\",\"data\":null}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found\n계정을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject("{\"status\":\"fail\",\"message\":\"account not found\",\"data\":null}")
                    )
            )
    })
    @DeleteMapping("/account-delete")
    public ResponseEntity<?> deleteAccount(
            @Parameter(description = "Access token for authentication\n인증을 위한 엑세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken) {

        boolean deleted = userService.deleteAccount(accessToken);

        if (deleted) {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("success", "account deleted", null);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "account not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    @Operation(
            summary = "Update user profile\n사용자 프로필 업데이트",
            description = "Updates the user profile with provided information such as nickname or password.\n닉네임이나 비밀번호 등 사용자 프로필을 업데이트"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile successfully updated\n프로필 성공적으로 업데이트",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"profile updated\",\"data\":{\"nickname\":\"newNickname\"}}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profile not found\n프로필을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"profile not found\",\"data\":null}")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate nickname\n중복된 닉네임",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"duplicate nickname\",\"data\":null}")
                    )
            )
    })
    @PatchMapping("/profile-update")
    public ResponseEntity<?> updateProfile(
            @Parameter(description = "Access token for authentication\n인증을 위한 액세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken,
            @RequestBody UpdateDTO updateDTO) {

        String updatedProfile = userService.updateProfile(accessToken, updateDTO);

        if ("DUPLICATE_NICKNAME".equals(updatedProfile)) {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "duplicate nickname", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
        } else if (updatedProfile != null) {
            ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>("success", "profile updated",
                    Map.of("nickname", updatedProfile));
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "profile not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }
}
