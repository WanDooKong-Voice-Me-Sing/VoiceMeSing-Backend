package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.aiserver.AiService;
import com.wandookong.voice_me_sing.dto.CreateSongDTO;
import com.wandookong.voice_me_sing.entity.SongTempEntity;
import com.wandookong.voice_me_sing.entity.UserEntity;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import com.wandookong.voice_me_sing.repository.SongTempRepository;
import com.wandookong.voice_me_sing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.xpath.XPath;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SongService {

    private final AiService aiService;
    private final SongTempRepository songTempRepository;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${spring.savePath}")
    private String path;

    public boolean createCoverSong(CreateSongDTO createSongDTO, String accessToken) throws IOException {
        // 1. 음원 파일 이름 설정 후 지정 폴더에 저장 (temp)
        MultipartFile multipartFile = createSongDTO.getSongFile(); // 파일 추출
        String originalSongFileName = multipartFile.getOriginalFilename(); // 이름 추출
        String storedSongFileName = System.currentTimeMillis() + "_" +originalSongFileName; // 저장 이름 설정
        String savePath = path + storedSongFileName; // 저장 폴더 지정
        multipartFile.transferTo(new File(savePath)); // 저장

        // 2. 파일 디비 임시 저장
        SongTempEntity songTempEntity = new SongTempEntity(originalSongFileName, storedSongFileName, savePath);
        SongTempEntity savedEntity = songTempRepository.save(songTempEntity);

        // 3. 필요한 값 뽑아서 ai server 전달 :: userId, songId, modelId

        Long songId = savedEntity.getSongId();
        Long voiceModelId = createSongDTO.getVoiceModelId();

        // access 토큰으로부터 사용자 id 뽑아내기
        String email = jwtUtil.getEmail(accessToken);
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        assert optionalUserEntity.isPresent();
        UserEntity userEntity = optionalUserEntity.get();
        Long userId = userEntity.getUserId();

        // 4. 요청
        return true;
//        return aiService.toPythonCoverSong(userId, songId, voiceModelId); // 어떤 사용자가 어떤 음원을 어떤 모델로
    }
}
