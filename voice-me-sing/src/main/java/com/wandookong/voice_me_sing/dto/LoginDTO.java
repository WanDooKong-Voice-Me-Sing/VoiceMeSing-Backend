package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "이메일 기반 로그인에 필요한 데이터 전송 객체")
@Getter @Setter
public class LoginDTO { // 일반 이메일 로그인시 JSON 매핑

    @Schema(description = "사용자의 이메일 주소", example = "user@example.com")
    private String email;

    @Schema(description = "사용자의 비밀번호", example = "password123")
    private String password;
}
