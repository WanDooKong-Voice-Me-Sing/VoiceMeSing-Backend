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

    public void signup(SignupDTO signUpDTO) {
        String username = signUpDTO.getUsername();
        String password = signUpDTO.getPassword();
        String email = signUpDTO.getEmail();
        String nickname = signUpDTO.getNickname();

        boolean isUsernameExist = userRepository.existsByUsername(username);

        if(isUsernameExist) {
            return;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(username);
        userEntity.setPassword(bCryptPasswordEncoder.encode(password));
        userEntity.setEmail(email);
        userEntity.setNickname(nickname);
        userEntity.setRole("ROLE_USER");

        userRepository.save(userEntity);
    }
}
