package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.dto.TrainVocalDTO;
import com.wandookong.voice_me_sing.entity.AudioTempEntity;
import com.wandookong.voice_me_sing.repository.AudioTempRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SongService {

    private final AudioTempRepository audioTempRepository;

    public void saveAudioFile(TrainVocalDTO trainVocalDTO) throws IOException {

        // 파일 이름 설정 후 지정 폴더에 저장
        MultipartFile multipartFile = trainVocalDTO.getVoiceFile(); // 파일 추출
        String originalFilename = multipartFile.getOriginalFilename(); // 이름 추출
        String storedFileName = System.currentTimeMillis() + "_" + originalFilename; // 저장 이름 설정
        String savePath = "C:\\Users\\RKB\\Desktop\\새 폴더\\" + storedFileName; // 변경 // 지정 폴더에 저장
        multipartFile.transferTo(new File(savePath));

        // 파일 경로 디비 저장
        AudioTempEntity audioTempEntity = new AudioTempEntity(originalFilename, storedFileName, savePath);
        audioTempRepository.save(audioTempEntity);


    }
}
