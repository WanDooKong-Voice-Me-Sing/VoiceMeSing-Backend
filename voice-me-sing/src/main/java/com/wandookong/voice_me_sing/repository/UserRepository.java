package com.wandookong.voice_me_sing.repository;

import com.wandookong.voice_me_sing.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByNickname(String nickname);

    @Query(value = "select u.nickname from UserEntity u where u.email = :email")
    String getNicknameByEmail(@Param("email") String email);
}
