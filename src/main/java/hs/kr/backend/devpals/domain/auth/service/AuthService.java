package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.auth.dto.LoginRequest;
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

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<Map<String, Object>> login(LoginRequest request) {
        // 이메일로 유저 조회
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorException.INVALID_PASSWORD);
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user.getId());

        // FacadeResponse 사용 (여기서는 data에 token만 전달)
        FacadeResponse<String> facadeResponse = new FacadeResponse<>(true, "로그인 되었습니다.", token);

        // 최종 응답 Map 구성
        Map<String, Object> finalResponse = new LinkedHashMap<>();
        finalResponse.put("success", facadeResponse.isSuccess());
        finalResponse.put("message", facadeResponse.getMessage());
        finalResponse.put("data", Map.of("accessToken", token));
        finalResponse.put("user", new LoginUserResponse(user.getId(), user.getEmail(), user.getNickname()));

        return ResponseEntity.ok(finalResponse);
    }
}
