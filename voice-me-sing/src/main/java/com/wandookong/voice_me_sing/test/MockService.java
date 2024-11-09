package com.wandookong.voice_me_sing.test;

import com.wandookong.voice_me_sing.entity.CoverSongEntity;
import com.wandookong.voice_me_sing.repository.CoverSongRepository;
import com.wandookong.voice_me_sing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MockService {

    private final CoverSongRepository coverSongRepository;
    private final UserRepository userRepository;

    public void saveMockFile(MultipartFile songFile, String resultSongName, String userid) throws IOException {
        CoverSongEntity coverSongEntity = new CoverSongEntity();
        coverSongEntity.setResultSongName(resultSongName);
        coverSongEntity.setCoverSongFile(songFile.getBytes());
        coverSongEntity.setPublic(true);
        coverSongEntity.setUserEntity(userRepository.findById(Long.valueOf(userid)).get());

        coverSongRepository.save(coverSongEntity);
    }
}
