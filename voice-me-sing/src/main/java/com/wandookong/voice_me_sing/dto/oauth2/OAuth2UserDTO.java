package com.wandookong.voice_me_sing.dto.oauth2;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class OAuth2UserDTO {

    // email, password, nickname (E: + id)

//    private String username;
//    private String name;
    private String email;
    private String nickname;
    private String role;
}
