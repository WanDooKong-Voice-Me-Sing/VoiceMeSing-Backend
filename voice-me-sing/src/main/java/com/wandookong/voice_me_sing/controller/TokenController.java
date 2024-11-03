package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.service.ReissueService;
import com.wandookong.voice_me_sing.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Tag(name = "Token Management API", description = "Handles token-related operations\n토큰 관련 작업을 처리")
public class TokenController {

    private final ReissueService reissueService;
    private final CookieUtil cookieUtil;

    @Operation(
            summary = "Reissue access and refresh tokens\n엑세스 토큰과 리프레시 토큰 재발급",
            description = "Reissue access and refresh tokens if a valid refresh token is provided.\n유효한 리프레시 토큰이 제공되면 엑세스 토큰과 리프레시 토큰을 재발급")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Access and refresh tokens reissued\n엑세스 토큰과 리프레시 토큰 재발급",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"success\",\"message\":\"access and refresh tokens reissued\",\"data\":null}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400", description = "Invalid refresh token\n유효하지 않은 리프레시 토큰",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":\"fail\",\"message\":\"invalid refresh token\",\"data\":null}")
                    )
            )
    })
    @PostMapping("/reissue")
    // 리프레시 토큰을 기반으로 리프레시 토큰과 엑세스 토큰 재발급
    public ResponseEntity<?> reissue(
            @Parameter(description = "Refresh token for authentication\n인증을 위한 리프레시 토큰", required = true)
            @CookieValue(value = "refresh") String refreshToken, HttpServletResponse response) {
//        String refreshToken = null;
//        Cookie[] cookies = request.getCookies();
//
//        // get refresh token
//        for (Cookie cookie : cookies) {
//            if (cookie.getName().equals("refresh")) {
//                refreshToken = cookie.getValue();
//                break;
//            }
//        }
//        System.out.println("refreshToken = " + refreshToken);

        // 토큰 재발급 프로세스
        Map<String, String> result = reissueService.reissueAccessRefresh(refreshToken);
        String newRefreshToken = result.get("newRefreshToken");
        String newAccessToken = result.get("newAccessToken");

        // 응답 생성
        if (newRefreshToken == null || newAccessToken == null) {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", result.get("message"), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        } else {
            // 쿠키와 헤더 설정
            response.addCookie(cookieUtil.createCookie("refresh", newRefreshToken));
            response.setHeader("access", newAccessToken);

            ResponseDTO<String> responseDTO = new ResponseDTO<>("success", "access and refresh tokens reissued", null);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }

    }

    @Operation(
            summary = "Check refresh token existence\n리프레시 토큰 소유 여부 확인",
            description = "Checks whether the user has a valid refresh token. This is used to distinguish between logged-in and non-logged-in users.\n유효한 리프레시 토큰의 존재 여부를 확인하여 로그인 유저와 비로그인 유저를 구별"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Refresh token existence check completed\n리프레시 토큰 소유 여부 확인 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject("{\"status\":\"success\",\"message\":\"Refresh token exists\",\"data\":true}")
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "No valid refresh token found\n유효한 리프레시 토큰이 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject("{\"status\":\"success\",\"message\":\"No valid refresh token\",\"data\":false}")
                    )
            )
    })
    @PostMapping("/refresh-check")
    // refresh token 소유 여부 확인 (프론트: 새로고침 시 AT 소실, 따라서 매 요청마다 AT 발급 필. RT 없는 비로그인 유저 구별 위함)
    public ResponseEntity<?> checkRefreshToken(
            @Parameter(description = "Refresh token stored in cookies\n쿠키에 저장된 리프레시 토큰", required = false)
            @CookieValue(value = "refresh", required = false) String refreshToken) {

        if (refreshToken != null) {
            return ResponseEntity.ok(new ResponseDTO<>("success", "Refresh token exists", true));
        } else {
            return ResponseEntity.ok(new ResponseDTO<>("success", "No valid refresh token", false));
        }
    }

    @Operation(
            summary = "Reformat tokens\n토큰 재구성",
            description = "Moves the access token from the cookie to the response header if both access and refresh tokens are present in cookies.\n쿠키에 액세스 토큰과 리프레시 토큰이 모두 존재할 경우, 액세스 토큰을 응답 헤더로 이동"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully moved access token to header\n액세스 토큰을 헤더로 성공적으로 이동",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject("{\"status\":\"success\",\"message\":\"access token moved to the header\",\"data\":null}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "No valid tokens found in cookies\n쿠키에 유효한 토큰이 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject("{\"status\":\"fail\",\"message\":\"no valid tokens in the cookies\",\"data\":null}")
                    )
            )
    })
    @PostMapping("/token-reformat")
    // 소셜 로그인 후 쿠키로 발급된 엑세스 토큰을 헤더로 재전송하는 로직
    public ResponseEntity<?> reformatTokens(
            HttpServletResponse response,

            @Parameter(description = "Refresh token stored in cookies\n쿠키에 저장된 리프레시 토큰", required = false)
            @CookieValue(value = "refresh", required = false) String refreshToken,

            @Parameter(description = "Access token stored in cookies\n쿠키에 저장된 액세스 토큰", required = false)
            @CookieValue(value = "access", required = false) String accessToken) {

        // 모두 존재 시 accessToken 헤더값으로 재전송
        if (refreshToken != null && accessToken != null) {

            // 1. accessToken 헤더로 전송
            response.addHeader("access", accessToken);

            // 2. Cookie 의 accessToken 값을 expire
            Cookie expiredCookie = cookieUtil.createExpiredCookie("access");
            response.addCookie(expiredCookie);

            // 응답 생성
            ResponseDTO<String> responseDTO = new ResponseDTO<>("success", "access token moved to the header", null);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        } else {
            // 응답 생성
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "no valid tokens in the cookies", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }
    }

}
