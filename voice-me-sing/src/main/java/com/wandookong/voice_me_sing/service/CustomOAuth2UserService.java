package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.dto.oauth2.*;
import com.wandookong.voice_me_sing.entity.UserEntity;
import com.wandookong.voice_me_sing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 외부 서비스 분류
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }

        assert oAuth2Response != null;

        // 회원 저장 및 업데이트
        String email = oAuth2Response.getEmail();
        String password = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();

        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);

        if (optionalUserEntity.isPresent()) { // 존재 O, 업데이트
            // 회원 정보 업데이트 (변경 가능 여부:: id:X email:O password:X nickname:O role:X)
            UserEntity userEntity = optionalUserEntity.get();

            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setNickname(oAuth2Response.getName());

            userRepository.save(userEntity);

            // 로그인 검증
            OAuth2UserDTO userDTO = new OAuth2UserDTO();

            userDTO.setEmail(oAuth2Response.getEmail());
            userDTO.setNickname(oAuth2Response.getName());
            userDTO.setRole(userEntity.getRole());

            return new CustomOAuth2User(userDTO);
        }
        else { // 존재 X, 저장
            // 회원 정보 저장
            UserEntity userEntity = new UserEntity();

//            userEntity.setUsername(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setPassword(bCryptPasswordEncoder.encode(password));
            userEntity.setNickname(oAuth2Response.getName());
            userEntity.setRole("ROLE_USER");

            userRepository.save(userEntity);

            // 로그인 검증
            OAuth2UserDTO userDTO = new OAuth2UserDTO();

            userDTO.setEmail(oAuth2Response.getEmail());
            userDTO.setNickname(oAuth2Response.getName());
            userDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(userDTO);
        }
    }
}
