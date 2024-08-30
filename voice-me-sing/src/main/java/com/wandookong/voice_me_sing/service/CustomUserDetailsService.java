package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.dto.CustomUserDetails;
import com.wandookong.voice_me_sing.entity.UserEntity;
import com.wandookong.voice_me_sing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        UserEntity userEntity = userRepository.findByEmail(email);

        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);

        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            return new CustomUserDetails(userEntity);
        }

        return null;
    }
}
