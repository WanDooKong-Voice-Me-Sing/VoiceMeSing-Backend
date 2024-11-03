package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "회원 가입에 필요한 데이터 전송 객체")
@Getter
@Setter
public class SignupDTO {
//    String username;

    @Schema(description = "사용자의 이메일 주소", example = "user@example.com")
    String email;

    @Schema(description = "사용자의 비밀번호", example = "password1234")
    String password;

    @Schema(description = "사용자의 닉네임", example = "wandookong123")
    String nickname;
}
