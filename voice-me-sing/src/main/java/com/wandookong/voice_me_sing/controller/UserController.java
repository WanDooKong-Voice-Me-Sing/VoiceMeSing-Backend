package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.dto.UserProfileDTO;
import com.wandookong.voice_me_sing.dto.UserUpdateDTO;
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

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "Operations related to user\n사용자 관련 작업")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "사용자 프로필 조회",
            description = "사용자의 프로필 정보를 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 조회 성공\nProfile retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"get profile\",\"data\":{\"nickname\":\"userNickname\",\"introduction\":\"userIntroduction\",\"isSocialLogin\":true}}")
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "사용자 찾을 수 없음\nNo user found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"no user found\",\"data\":null}")
                    )
            )
    })
    @GetMapping("/profile")
    // 사용자 프로필 조회
    public ResponseEntity<?> getProfile(
            @Parameter(description = "Access token for authentication\n인증을 위한 엑세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken) {

        // 프로필 조회 프로세스
        UserProfileDTO userProfileDTO = userService.getProfile(accessToken);

        // 응답 생성
        if (userProfileDTO == null) {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "no user found", null);
            return ResponseEntity.status(401).body(responseDTO);
        } else {
            ResponseDTO<UserProfileDTO> responseDTO = new ResponseDTO<>("success", "get profile", userProfileDTO);
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
    // 회원 탈퇴
    public ResponseEntity<?> deleteAccount(
            @Parameter(description = "Access token for authentication\n인증을 위한 엑세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken) {

        // 회원 삭제 프로세스
        boolean deleted = userService.deleteAccount(accessToken);

        // 응답 생성
        if (deleted) {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("success", "account deleted", null);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "account not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    @Operation(
            summary = "사용자 프로필 업데이트",
            description = "사용자의 프로필 정보를 업데이트"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 업데이트 성공\nProfile updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"profile updated\",\"data\":{\"nickname\":\"newNickname\",\"introduction\":\"newIntroduction\",\"isSocialLogin\":true}}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로필을 찾을 수 없음\nProfile not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"profile not found\",\"data\":null}")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "닉네임 중복\nDuplicate nickname",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"duplicate nickname\",\"data\":null}")
                    )
            )
    })
    @PatchMapping("/profile-update")
    // 사용자 프로필 업데이트
    public ResponseEntity<?> updateProfile(
            @Parameter(description = "Access token for authentication\n인증을 위한 액세스 토큰", required = true)
            @RequestHeader(value = "access") String accessToken,

            @Parameter(description = "DTO containing fields for updating the user profile\n사용자 프로필 업데이트를 위한 필드를 포함한 DTO", required = true)
            @RequestBody UserUpdateDTO userUpdateDTO) {

        // 프로필 업데이트 프로세스
        Object updatedProfile = userService.updateProfile(accessToken, userUpdateDTO);

        // 응답 생성
        if ("DUPLICATE_NICKNAME".equals(updatedProfile)) {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "duplicate nickname", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
        } else if (updatedProfile != null) {
            ResponseDTO<Object> responseDTO = new ResponseDTO<>("success", "profile updated", updatedProfile);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "profile not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }
}
