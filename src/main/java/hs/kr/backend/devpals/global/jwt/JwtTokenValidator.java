package hs.kr.backend.devpals.global.jwt;

import hs.kr.backend.devpals.domain.user.principal.CustomUserDetailsService;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtTokenValidator {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);

    @Value("${jwt.secret}")
    private String secretKey;

    private final CustomUserDetailsService customUserDetailsService;

    public JwtTokenValidator(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * JWT 서명을 위한 키 생성
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT 토큰에서 Claims(데이터) 추출
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * JWT 토큰에서 userId(PK) 추출
     */
    public Integer getUserIdFromToken(String token) {
        return Integer.parseInt(extractClaims(token).getSubject()); //  subject에서 userId(PK) 가져옴
    }

    /**
     * Access Token 검증
     */
    public boolean validateJwtToken(String jwtToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwtToken);

            Date expiration = claimsJws.getBody().getExpiration();
            System.out.println("토큰 만료 시간 (서버 기준): " + expiration);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("JWT 토큰이 만료되었습니다: {}", e.getMessage());
            throw new CustomException(ErrorException.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            logger.error("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
            throw new CustomException(ErrorException.FORBIDDEN);
        } catch (JwtException e) {
            logger.error("유효하지 않은 JWT 토큰입니다: {}", e.getMessage());
            throw new CustomException(ErrorException.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            logger.error("JWT 클레임 문자열이 비어 있습니다: {}", e.getMessage());
            throw new CustomException(ErrorException.INVALID_PASSWORD);
        }
    }

    /**
     * Refresh Token 검증
     */
    public boolean validateRefreshToken(String refreshToken) {
        try {
            // 받은 Refresh Token을 로그로 출력
            System.out.println("Received Refresh Token: " + refreshToken);

            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // Refresh Token도 동일한 키 사용
                    .build()
                    .parseClaimsJws(refreshToken);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("JWT 토큰이 만료되었습니다: {}", e.getMessage());
            throw new CustomException(ErrorException.TOKEN_EXPIRED);
        }
    }

    /**
     * JWT 토큰에서 사용자 인증 정보 반환
     */
    public Authentication getAuthentication(String token) {
        Claims claims = extractClaims(token);
        return getAuthenticationByClaims(claims);
    }

    /**
     * 클레임에서 사용자 정보 추출 후 Authentication 반환
     */
    public Authentication getAuthenticationByClaims(Claims claims) {
        // 디버깅 로그 추가
        System.out.println("JWT Claims: " + claims); // claims 내용 출력

        Object userIdObj = claims.get("userId");
        if (userIdObj == null) {
            throw new CustomException(ErrorException.USER_ID_NOT_FOUND); // memberId 없으면 예외 발생
        }

        Long memberId = Long.parseLong(userIdObj.toString());

        // ✅ memberId로 유저 정보 조회
        UserDetails userDetails = customUserDetailsService.findById(memberId);
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
