package com.wandookong.voice_me_sing.repository;

import com.wandookong.voice_me_sing.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);
//    Optional<UserEntity> findByEmail(String email);
}
