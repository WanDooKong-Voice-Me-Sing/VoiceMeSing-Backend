package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.entity.RefreshTokenEntity;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import com.wandookong.voice_me_sing.oauth2.CookieUtil;
import com.wandookong.voice_me_sing.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieUtil cookieUtil;

    public ResponseEntity<?> reissueAccessRefresh(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;
        Cookie[] cookies = request.getCookies();

        // get refresh token
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        System.out.println("refreshToken = " + refreshToken);

        // refreshToken null 확인
        if (refreshToken == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // refreshToken 만료 확인
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // "refresh" token 인지 확인
        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // DB에 refresh 토큰 존재하는지 확인
        Boolean isExist = refreshTokenRepository.existsByRefreshToken(refreshToken);
        if (!isExist) {
            return new ResponseEntity<>("refresh token not exist", HttpStatus.BAD_REQUEST);
        }

        System.out.println("valid refresh token");

        // 새로운 access/refresh token 생성
        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt("access", email, role, 600000L); // 10분
        String newRefreshToken = jwtUtil.createJwt("refresh", email, role, 86400000L); // 24시간

        // refresh 토큰 삭제 후 저장
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
        addRefreshEntity(email, newRefreshToken);

        // 응답 설정
        response.addCookie(cookieUtil.createCookie("refresh", newRefreshToken));
        response.setHeader("access", newAccessToken);

        return new ResponseEntity<>("new access/refresh token issued", HttpStatus.OK);
    }

    private void addRefreshEntity(String email, String refresh) {

        Date date = new Date(System.currentTimeMillis() + 86400000L); // 24시간

        RefreshTokenEntity refreshEntity = new RefreshTokenEntity();
        refreshEntity.setEmail(email);
        refreshEntity.setRefreshToken(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshTokenRepository.save(refreshEntity);
    }

}
