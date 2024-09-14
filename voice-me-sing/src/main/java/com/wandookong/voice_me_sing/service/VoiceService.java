package com.wandookong.voice_me_sing.service;

import com.wandookong.voice_me_sing.dto.ResponseDTO;
import com.wandookong.voice_me_sing.dto.TrainVoiceDTO;
import com.wandookong.voice_me_sing.dto.VoiceModelDTO;
import com.wandookong.voice_me_sing.entity.UserEntity;
import com.wandookong.voice_me_sing.entity.VoiceTempEntity;
import com.wandookong.voice_me_sing.jwt.JWTUtil;
import com.wandookong.voice_me_sing.repository.UserRepository;
import com.wandookong.voice_me_sing.repository.VoiceTempRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoiceService {

    private final VoiceTempRepository voiceTempRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public ResponseEntity<?> saveAudioFile(TrainVoiceDTO trainVoiceDTO) throws IOException {

        // 파일 이름 설정 후 지정 폴더에 저장
        MultipartFile multipartFile = trainVoiceDTO.getVoiceFile(); // 파일 추출
        String originalVoiceFileName = multipartFile.getOriginalFilename(); // 이름 추출
        String storedVoiceFileName = System.currentTimeMillis() + "_" + originalVoiceFileName; // 저장 이름 설정 (같은 이름으로 또 올릴 수도 있기 때문에 랜덤 이름 설정)
        String savePath = "C:\\Users\\RKB\\Desktop\\새 폴더\\" + storedVoiceFileName; // ***수정 // 저장 폴더 지정
        multipartFile.transferTo(new File(savePath)); // 저장

        // 사용자가 설정한 모델 이름 추출
        String modelName = trainVoiceDTO.getModelName();

        // 파일 경로 디비 저장
        VoiceTempEntity voiceTempEntity = new VoiceTempEntity(originalVoiceFileName, storedVoiceFileName, savePath, modelName);
        voiceTempRepository.save(voiceTempEntity);

        // python 서버 요청 로직 *** -> voiceModel 테이블에 모델 저장 -> python 서버는 filename 과 modelname 사용

        // 저장 결과 리턴
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>("success", "file upload success", Map.of(
                "voiceFileName", originalVoiceFileName,
                "modelName", modelName
        ));
        return ResponseEntity.ok().body(responseDTO);
    }

    public ResponseEntity<?> getVoiceModels(String userToken) {
        // 해당 사용자 검색
        String email = jwtUtil.getEmail(userToken);

        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        if (optionalUserEntity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<String>("fail", "no user", null));
        }
        UserEntity userEntity = optionalUserEntity.get();

        // 사용자 소유 VoiceModels 의 DTO 리스트 생성
        List<VoiceModelDTO> voiceModelDTOs = userEntity.getVoiceModels().stream()
                .map(vm -> new VoiceModelDTO(vm.getVoiceModelId(), vm.getVoiceModelName()))
                .toList();

        // 응답 데이터 생성
        ResponseDTO<List<VoiceModelDTO>> responseDTO = new ResponseDTO<>("success", "get voice models success", voiceModelDTOs);

        return ResponseEntity.ok().body(responseDTO);

    }
}
