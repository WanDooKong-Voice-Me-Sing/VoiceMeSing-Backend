package com.wandookong.voice_me_sing.oauth2;

import com.wandookong.voice_me_sing.dto.oauth2.CustomOAuth2User;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    // 로그인 성공
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String email = customOAuth2User.getEmail();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // email 과 role 값으로 JWT 생성
        String token = jwtUtil.createJwt(email, role, 60*60*60L);

        response.addCookie(createCookie("Authorization", token));
        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect("http://localhost:8080/");
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);

        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60*60*60);
//        cookie.setSecure(true);

        System.out.println("cookie created");

        return cookie;
    }
}
