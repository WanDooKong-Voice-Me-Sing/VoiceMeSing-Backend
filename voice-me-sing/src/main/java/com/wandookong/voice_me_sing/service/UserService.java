package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.dto.UserProfileDTO;
import com.wandookong.voice_me_sing.dto.UserUpdateDTO;
import com.wandookong.voice_me_sing.entity.UserEntity;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import com.wandookong.voice_me_sing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BoardService boardService;

    public String getNicknameByToken(String accessToken) {
        // accessToken 으로부터 사용자 정보(nickname) 추출
        String email = jwtUtil.getEmail(accessToken);
        return userRepository.getNicknameByEmail(email);
//        String email = jwtUtil.getEmail(accessToken);
//
//        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
//        if (optionalUserEntity.isPresent()) {
//            UserEntity userEntity = optionalUserEntity.get();
//            return userEntity.getNickname();
//        } else {
//            return null;
//        }
    }

    public UserProfileDTO getProfile(String accessToken) {
        // accessToken 으로부터 얻은 email을 기반으로 사용자 검색
        String email = jwtUtil.getEmail(accessToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);

        // DTO로 변환 후 리턴
        if (optionalUserEntity.isPresent()) {
            UserEntity userEntity = optionalUserEntity.get();
            return UserProfileDTO.toUserProfileDTO(userEntity);
        } else return null;
    }

    public boolean deleteAccount(String accessToken) {
        // accessToken 으로부터 얻은 email을 기반으로 사용자 검색
        String email = jwtUtil.getEmail(accessToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);

        // 존재할 경우 회원 삭제
        if (optionalUserEntity.isPresent()) {
            userRepository.delete(optionalUserEntity.get());
            return true;
        }
        return false;
    }

    public Object updateProfile(String accessToken, UserUpdateDTO userUpdateDTO) {
        // accessToken 으로부터 얻은 email을 기반으로 사용자 검색
        String email = jwtUtil.getEmail(accessToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);

        if (optionalUserEntity.isEmpty()) {
            return null;
        }
        UserEntity userEntity = optionalUserEntity.get();

        // 닉네임 수정
        if (userUpdateDTO.getNickname() != null) {
            Optional<UserEntity> byNickname = userRepository.findByNickname(userUpdateDTO.getNickname());
            if (byNickname.isPresent()) {
                return "DUPLICATE_NICKNAME";
            }
            boardService.changeBoardWriter(userEntity.getNickname(), userUpdateDTO.getNickname());
            userEntity.setNickname(userUpdateDTO.getNickname());
        }

        // 비밀번호 수정
        if (userUpdateDTO.getPassword() != null) {
            userEntity.setPassword(bCryptPasswordEncoder.encode(userUpdateDTO.getPassword()));
        }

        // 자기소개 수정
        if (userUpdateDTO.getIntroduction() != null) {
            userEntity.setIntroduction(userUpdateDTO.getIntroduction());
        }

        // 수정한 내용 반영 후 DTO로 변환 리턴 (내용 확인 위함)
        userRepository.save(userEntity);
        return UserProfileDTO.toUserProfileDTO(userEntity);
    }
}
