package com.wandookong.voice_me_sing.oauth2;

import com.wandookong.voice_me_sing.dto.oauth2.CustomOAuth2User;
import com.wandookong.voice_me_sing.entity.RefreshTokenEntity;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import com.wandookong.voice_me_sing.repository.RefreshTokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.frontEndServerUrl}")
    private String frontEndServerUrl;

    // 로그인 성공
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String email = customOAuth2User.getEmail();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // email 과 role 값으로 토큰 생성 (access, refresh)
        String access = jwtUtil.createJwt("access", email, role, 600000L); // 10분
        String refresh = jwtUtil.createJwt("refresh", email, role, 86400000L); // 24시간

        addRefreshEntity(email, refresh);

//        response.addHeader("access", access);
        response.addCookie(cookieUtil.createCookie("access", access)); // ?
        response.addCookie(cookieUtil.createCookie("refresh", refresh));
        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect(frontEndServerUrl);
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
