package hs.kr.backend.devpals.infra.oauth2;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Oauth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        if ("github-auth".equals(provider)) {
            String githubUrl = (String) oAuth2User.getAttributes().get("html_url");
            if (githubUrl == null) {
                throw new CustomException(ErrorException.USER_NOT_FOUND);
            }
            response.sendRedirect("http://localhost:5173/oauth/github-success?githubUrl=" + githubUrl);
            return;
        }

        // 그 외 소셜 로그인은 기존 방식 (JWT 발급)
        String email = (String) oAuth2User.getAttributes().get("email");
        if (email == null) {
            throw new CustomException(ErrorException.USER_NOT_FOUND);
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        String accessToken = jwtTokenProvider.generateToken(user.getId());
        String redirectUrl = "http://localhost:5173/oauth-redirect?accessToken=" + accessToken;
        response.sendRedirect(redirectUrl);
    }
}