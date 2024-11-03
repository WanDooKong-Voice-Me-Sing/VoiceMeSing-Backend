package com.wandookong.voice_me_sing.dto;

import com.wandookong.voice_me_sing.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Schema(description = "회원 정보 조회에 필요한 데이터 전송 객체")
@ToString
public class UserProfileDTO {

    @Schema(description = "수정할 닉네임", example = "newNickname")
    String nickname;

    @Schema(description = "수정할 자기소개", example = "newIntroduction")
    String introduction;

    public static UserProfileDTO toUserProfileDTO(UserEntity userEntity) {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setNickname(userEntity.getNickname());
        userProfileDTO.setIntroduction(userEntity.getIntroduction());
        return userProfileDTO;
    }
}
