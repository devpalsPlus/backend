package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.auth.dto.LoginRequest;
import hs.kr.backend.devpals.domain.auth.entity.SessionEntity;
import hs.kr.backend.devpals.domain.auth.repository.SessionRepository;
import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.facade.FacadeResponse;
import hs.kr.backend.devpals.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SessionRepository sessionRepository;

    @Transactional
    public ResponseEntity<Map<String, Object>> login(LoginRequest request) {
        // 이메일로 유저 조회
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorException.INVALID_PASSWORD);
        }

        sessionRepository.deleteByUserId(user.getId());
        sessionRepository.flush();


        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // 새로운 세션 저장
        SessionEntity session = new SessionEntity();
        session.setUserId(user.getId());
        session.setAccessToken(accessToken);
        session.setRefreshToken(refreshToken);
        session.setExpiresAt(LocalDateTime.now().plusDays(14)); // 14일 후 만료
        sessionRepository.save(session);

        Map<String, Object> tokenData = new LinkedHashMap<>();
        tokenData.put("accessToken", accessToken);
        tokenData.put("refreshToken", refreshToken);

        FacadeResponse<Map<String, Object>> facadeResponse = new FacadeResponse<>(true, "로그인 되었습니다.", tokenData);

        // 최종 응답 데이터 구성
        Map<String, Object> finalResponse = new LinkedHashMap<>();
        finalResponse.put("success", facadeResponse.isSuccess());
        finalResponse.put("message", facadeResponse.getMessage());
        finalResponse.put("data", facadeResponse.getData());
        finalResponse.put("user", new LoginUserResponse(user.getId(), user.getEmail(), user.getNickname())); // ✅ user는 따로 추가

        return ResponseEntity.ok(finalResponse);
    }
}
