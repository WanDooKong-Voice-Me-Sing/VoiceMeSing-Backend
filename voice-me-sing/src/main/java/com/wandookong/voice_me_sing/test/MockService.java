package com.wandookong.voice_me_sing.test;

import com.wandookong.voice_me_sing.entity.CoverSongEntity;
import com.wandookong.voice_me_sing.repository.CoverSongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MockService {

    private final CoverSongRepository coverSongRepository;

    public void saveMockFile(MultipartFile songFile) throws IOException {
        CoverSongEntity coverSongEntity = new CoverSongEntity();
        coverSongEntity.setCoverSongFile(songFile.getBytes());

        coverSongRepository.save(coverSongEntity);

    }
}
