package com.wandookong.voice_me_sing.util;

import com.wandookong.voice_me_sing.entity.RefreshTokenEntity;
import com.wandookong.voice_me_sing.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenUtil {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String email, String refresh) {

        Date expirationDate = new Date(System.currentTimeMillis() + 86400000L); // 24시간

        RefreshTokenEntity refreshEntity = new RefreshTokenEntity();
        refreshEntity.setEmail(email);
        refreshEntity.setRefreshToken(refresh);
        refreshEntity.setExpiration(expirationDate);

        refreshTokenRepository.save(refreshEntity);

        // expiration 이 지난 토큰은 삭제
        Date currentDate = new Date();
        refreshTokenRepository.deleteByExpirationBefore(currentDate);
    }
}
