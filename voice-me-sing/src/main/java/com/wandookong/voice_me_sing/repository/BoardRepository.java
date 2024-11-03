package com.wandookong.voice_me_sing.repository;

import com.wandookong.voice_me_sing.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    List<BoardEntity> findByBoardWriter(String nickname);
}
