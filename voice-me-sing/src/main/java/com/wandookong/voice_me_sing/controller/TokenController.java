package com.wandookong.voice_me_sing.controller;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TokenController {

    @PostMapping("/refresh")
    public ResponseEntity<?> checkRefreshToken(@CookieValue(value = "refresh", required = false) String refreshToken) {
        if (refreshToken != null) {
            // 성공적으로 refresh token이 존재할 경우
            return ResponseEntity.ok(new ResponseDTO<>("success", "Refresh token exists", true));
        } else {
            // refresh token이 없을 경우
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
            Cookie cookie = new Cookie("access", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setSecure(true);
            cookie.setAttribute("SameSite", "None");

            ResponseDTO<String> responseDTO = new ResponseDTO<>("success", "access token in the header", null);

            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);

        }
        else {
            ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "no valid tokens in the cookies", null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }
    }

}
