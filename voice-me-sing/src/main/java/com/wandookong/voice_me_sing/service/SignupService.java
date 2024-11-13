package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.dto.SignupDTO;
import com.wandookong.voice_me_sing.entity.UserEntity;
import com.wandookong.voice_me_sing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 회원가입 로직
    public boolean signup(SignupDTO signupDTO) {
//        String username = signupDTO.getUsername();
        String password = signupDTO.getPassword();
        String email = signupDTO.getEmail();
        String nickname = signupDTO.getNickname();

        // 회원 존재 여부 확인
        boolean isExist = userRepository.existsByEmail(email);
        if (isExist) return false;

        // 회원 저장
        UserEntity userEntity = new UserEntity();

//        userEntity.setUsername(username);
        userEntity.setPassword(bCryptPasswordEncoder.encode(password));
        userEntity.setEmail(email);
        userEntity.setNickname(nickname);
        userEntity.setRole("ROLE_USER");
        userEntity.setIsSocialLogin(false);

        userRepository.save(userEntity);

        return true;
    }
}
