package hs.kr.backend.devpals.global.jwt.jwtfilter;

import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import hs.kr.backend.devpals.global.exception.CustomException;
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
        if (token != null) {
            try {
                jwtValidator.validateJwtToken(token);
                Authentication auth = jwtValidator.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (CustomException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"message\": \"Access Token이 만료되었습니다.\", \"code\": \"TOKEN_EXPIRED\"}");
                response.getWriter().flush();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }


    /**
     * 요청 헤더에서 JWT 토큰을 추출하는 메서드
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(HEADER_PREFIX)) {
            return bearerToken.substring(HEADER_PREFIX.length());
        }
        return null;
    }
}

