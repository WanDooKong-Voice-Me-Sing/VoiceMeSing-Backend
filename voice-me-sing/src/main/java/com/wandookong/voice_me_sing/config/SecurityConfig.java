package com.wandookong.voice_me_sing.config;

import com.wandookong.voice_me_sing.jwt.JWTFilter;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import com.wandookong.voice_me_sing.jwt.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    //비밀번호 해싱
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf((csrf) -> csrf.disable());
        http.formLogin((formLogin) -> formLogin.disable());
        http.httpBasic((httpBasic) -> httpBasic.disable());

        //경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/login", "/signup").permitAll()
                .anyRequest().authenticated()
        );

        //세션 설정
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 필터 설정
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        return http.build();
    }
}
