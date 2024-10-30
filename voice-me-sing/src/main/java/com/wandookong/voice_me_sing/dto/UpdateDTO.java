package com.wandookong.voice_me_sing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Schema(description = "회원 정봇 수정에 필요한 데이터 전송 객체")
@ToString
public class UpdateDTO {

    @Schema(description = "수정할 닉네임", example = "newNickname")
    String nickname;

    @Schema(description = "수정할 비밀번호", example = "newPassword")
    String password;
}
