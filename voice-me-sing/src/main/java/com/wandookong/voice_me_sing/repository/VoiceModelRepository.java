package com.wandookong.voice_me_sing.repository;

import com.wandookong.voice_me_sing.entity.VoiceModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceModelRepository extends JpaRepository<VoiceModelEntity, Long> {
}
