package com.wandookong.voice_me_sing.repository;

import com.wandookong.voice_me_sing.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

}
