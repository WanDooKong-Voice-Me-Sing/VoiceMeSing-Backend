package com.wandookong.voice_me_sing.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wandookong.voice_me_sing.dto.CustomUserDetails;
import com.wandookong.voice_me_sing.dto.LoginDTO;
import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.entity.RefreshTokenEntity;
import com.wandookong.voice_me_sing.oauth2.CookieUtil;
import com.wandookong.voice_me_sing.repository.RefreshTokenRepository;
import com.wandookong.voice_me_sing.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieUtil cookieUtil;
    private final UserRepository userRepository;

    // 로그인 로직 (검증)
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        LoginDTO loginDTO;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginDTO = objectMapper.readValue(messageBody, LoginDTO.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        // email 과 password 로 로그인 검증
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password, null);

        return authenticationManager.authenticate(authenticationToken);
    }

    // 로그인 성공 시 실행 (-> JWT 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        System.out.println("login success");

        // 유저 정보
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        // 1. email
        String email = customUserDetails.getEmail();

        // 2. role
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        String role = authority.getAuthority();

        // email 과 role 값으로 토큰 생성 (access, refresh)
        String access = jwtUtil.createJwt("access", email, role, 600000L); // 10분
        String refresh = jwtUtil.createJwt("refresh", email, role, 86400000L); // 24시간

        // refresh 토큰 DB 저장
        addRefreshEntity(email, refresh); // 24시간

        // 응답 설정
//        response.addHeader("Authorization", "Bearer " + token);
//        response.addHeader("access", access);
//        response.addCookie(cookieUtil.createCookie("refresh", refresh));
//        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("access", access);
        response.addCookie(cookieUtil.createCookie("refresh", refresh));
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String nickname = userRepository.findByEmail(email).get().getNickname();

        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>("success", "login success",
                Map.of(
                        "email", customUserDetails.getEmail(),
                        "nickname", nickname
                )
        );

        PrintWriter writer = response.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(responseDTO));
        writer.flush();
    }

    // 로그인 실패 시 실행
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
//        System.out.println("login failed");
//
//        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ResponseDTO<String> responseDTO = new ResponseDTO<>("fail", "login fail", null);

        PrintWriter writer = response.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(responseDTO));
        writer.flush();
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
