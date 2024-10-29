package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class TokenController {

    private final CookieUtil cookieUtil;

    @PostMapping("/refresh-check")
    // refresh token 소유 여부 확인 (프론트: 새로고침 시 AT 소실, 따라서 매 요청마다 AT 발급 필. RT 없는 비로그인 유저 구별 위함)
    public ResponseEntity<?> checkRefreshToken(@CookieValue(value = "refresh", required = false) String refreshToken) {
        if (refreshToken != null) {
            return ResponseEntity.ok(new ResponseDTO<>("success", "Refresh token exists", true));
        } else {
            return ResponseEntity.ok(new ResponseDTO<>("success", "No valid refresh token", false));
        }
    }

    @PostMapping("/token-reformat")
    public ResponseEntity<?> reformatTokens(
            HttpServletResponse response,
            @CookieValue(value = "refresh", required = false) String refreshToken,
            @CookieValue(value = "access", required = false) String accessToken
    ) {
        // 모두 존재 시 accessToken 헤더값으로 재전송
        if (refreshToken != null && accessToken != null) {

            // 1. accessToken 헤더로 전송
            response.addHeader("access", accessToken);

            // 2. Cookie 의 accessToken 값을 expire
            Cookie expiredCookie = cookieUtil.createExpiredCookie("access");
            response.addCookie(expiredCookie);

            ResponseDTO<String> responseDTO = new ResponseDTO<>("success", "access token moved to the header", null);

            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        } else {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "no valid tokens in the cookies", null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }
    }

}
