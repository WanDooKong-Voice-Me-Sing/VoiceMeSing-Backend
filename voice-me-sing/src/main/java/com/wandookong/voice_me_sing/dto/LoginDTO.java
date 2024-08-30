package com.wandookong.voice_me_sing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginDTO { // 일반 이메일 로그인시 JSON 매핑

    private String email;
    private String password;
}
