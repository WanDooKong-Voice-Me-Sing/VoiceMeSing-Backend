package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.dto.JoinDTO;
import com.wandookong.voice_me_sing.entity.UserEntity;
import com.wandookong.voice_me_sing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void joinProcess(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        boolean isUsernameExist = userRepository.existsByUsername(username);

        if(isUsernameExist) {
            return;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(bCryptPasswordEncoder.encode(password));
        userEntity.setRole("ROLE_ADMIN");

        userRepository.save(userEntity);
    }
}
