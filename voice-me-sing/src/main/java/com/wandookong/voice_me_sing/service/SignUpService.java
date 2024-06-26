package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.dto.SignUpDTO;
import com.wandookong.voice_me_sing.entity.UserEntity;
import com.wandookong.voice_me_sing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void signUpProcess(SignUpDTO signUpDTO) {
        String username = signUpDTO.getUsername();
        String password = signUpDTO.getPassword();
        String email = signUpDTO.getEmail();

        boolean isUsernameExist = userRepository.existsByUsername(username);

        if(isUsernameExist) {
            return;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(bCryptPasswordEncoder.encode(password));
        userEntity.setEmail(email);
        userEntity.setRole("ROLE_USER");

        userRepository.save(userEntity);
    }
}
