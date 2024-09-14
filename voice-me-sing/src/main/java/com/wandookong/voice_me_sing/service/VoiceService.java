package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.dto.TrainVoiceProcessDTO;
import com.wandookong.voice_me_sing.entity.VoiceTempEntity;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import com.wandookong.voice_me_sing.repository.VoiceTempRepository;
import com.wandookong.voice_me_sing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VoiceService {

    private final VoiceTempRepository voiceTempRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public ResponseEntity<?> saveAudioFile(TrainVoiceProcessDTO trainVoiceProcessDTO) throws IOException {

        // 파일 이름 설정 후 지정 폴더에 저장
        MultipartFile multipartFile = trainVoiceProcessDTO.getVoiceFile(); // 파일 추출
        String originalVoiceFileName = multipartFile.getOriginalFilename(); // 이름 추출
        String storedVoiceFileName = System.currentTimeMillis() + "_" + originalVoiceFileName; // 저장 이름 설정 (같은 이름으로 또 올릴 수도 있기 때문에 랜덤 이름 설정)
        String savePath = "C:\\Users\\RKB\\Desktop\\새 폴더\\" + storedVoiceFileName; // ***수정 // 저장 폴더 지정
        multipartFile.transferTo(new File(savePath)); // 저장

        // 사용자가 설정한 모델 이름 추출
        String modelName = trainVoiceProcessDTO.getModelName();

        // 파일 경로 디비 저장
        VoiceTempEntity voiceTempEntity = new VoiceTempEntity(originalVoiceFileName, storedVoiceFileName, savePath, modelName);
        voiceTempRepository.save(voiceTempEntity);

        // python 서버 요청 로직 *** -> voiceModel 테이블에 모델 저장
        /* voiceModel 테이블
            1. voice_model_id
            2. voice_model_name
            3. voice_model_file
            4. user_email *** 나중에 Id로 변경 (service 로직도 수정)
        */

        // 저장 결과 리턴
        ResponseDTO responseDTO = new ResponseDTO("success", "file upload success", Map.of(
                "voiceFileName", originalVoiceFileName,
                "modelName", modelName
        ));
        return ResponseEntity.ok().body(responseDTO);
    }

    public ResponseEntity<?> getVoiceModels(String userToken) {
        String email = jwtUtil.getEmail(userToken);



        return null;
    }
}
