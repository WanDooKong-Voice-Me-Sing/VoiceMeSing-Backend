package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.dto.UpdateDTO;
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

    public boolean deleteAccount(String accessToken) {

        String email = jwtUtil.getEmail(accessToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        if (optionalUserEntity.isPresent()) {
            userRepository.delete(optionalUserEntity.get());
            return true;
        }
        return false;
    }

    public String updateProfile(String accessToken, UpdateDTO updateDTO) {
        String email = jwtUtil.getEmail(accessToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);

        if (optionalUserEntity.isEmpty()) {
            return null;
        }

        UserEntity userEntity = optionalUserEntity.get();

        if (updateDTO.getNickname() != null) {
            Optional<UserEntity> byNickname = userRepository.findByNickname(updateDTO.getNickname());
            if (byNickname.isPresent()) {
                return "DUPLICATE_NICKNAME";
            }
            userEntity.setNickname(updateDTO.getNickname());
        }

        if (updateDTO.getPassword() != null) {
            userEntity.setPassword(bCryptPasswordEncoder.encode(updateDTO.getPassword()));
        }

        userRepository.save(userEntity);

        return userEntity.getNickname();
    }
}
