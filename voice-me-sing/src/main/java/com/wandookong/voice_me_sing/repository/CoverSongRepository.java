package com.wandookong.voice_me_sing.repository;

import com.wandookong.voice_me_sing.entity.CoverSongEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoverSongRepository extends JpaRepository<CoverSongEntity, Long> {

    List<CoverSongEntity> findAllByIsPublic(Boolean isPublic);
}
