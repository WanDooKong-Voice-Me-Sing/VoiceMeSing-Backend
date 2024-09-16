package com.wandookong.voice_me_sing.jwt;

import com.wandookong.voice_me_sing.dto.CustomUserDetails;
import com.wandookong.voice_me_sing.entity.UserEntity;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = null;
        Cookie[] cookies = request.getCookies();

//        System.out.println("cookies = " + Arrays.toString(cookies));

        // 쿠키와 헤더 사용에 따른 분리
        // case 0: 쿠키 존재
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access")) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        // case 1: 쿠키 존재 X
        if (accessToken == null) {
            accessToken = request.getHeader("access");
        }

//        System.out.println("accessToken = " + accessToken);

        // accessToken null 확인
        if (accessToken == null) {
//            System.out.println("access token null");
            filterChain.doFilter(request, response);

            return;
        }

//        System.out.println("authorization begin");

        // accessToken 만료 확인
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            PrintWriter writer = response.getWriter();
            writer.print("access token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        // "access" token 인지 확인
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

//        System.out.println("authorization done");

        String email = jwtUtil.getEmail(accessToken);
        String role = jwtUtil.getRole(accessToken);

        UserEntity userEntity = new UserEntity();

        userEntity.setEmail(email);
        userEntity.setPassword("password");
        userEntity.setRole(role);

        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        System.out.println("temporary session for the request created");

        filterChain.doFilter(request, response);
    }
}
