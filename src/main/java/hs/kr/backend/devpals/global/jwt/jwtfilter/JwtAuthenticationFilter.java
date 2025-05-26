package hs.kr.backend.devpals.global.jwt.jwtfilter;

import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_PREFIX = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";

    private final JwtTokenValidator jwtValidator;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token == null) {
            throw new CustomException(ErrorException.UNAUTHORIZED);
        }

        try {
            jwtValidator.validateJwtToken(token); // 내부에서 유효성 검증
            Authentication auth = jwtValidator.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (CustomException e) {
            throw e;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"/auth/oauth-login".equals(request.getServletPath());
    }

    /**
     * 요청 헤더 또는 쿠키에서 JWT 토큰을 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(HEADER_PREFIX)) {
            return bearerToken.substring(HEADER_PREFIX.length());
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
