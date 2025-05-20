package hs.kr.backend.devpals.infra.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import hs.kr.backend.devpals.domain.auth.dto.TokenResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
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

        String provider = (String) request.getAttribute("provider");
        if (provider == null) {
            provider = oAuth2User.getAttributes().containsKey("kakao_account") ? "kakao" :
                    oAuth2User.getAttributes().containsKey("response") ? "naver" :
                            oAuth2User.getAttributes().containsKey("login") ? "github" : "google";
        }

        String email = CustomOauth2UserService.extractEmail(provider, oAuth2User);
        if (email == null) {
            throw new CustomException(ErrorException.USER_NOT_FOUND);
        }

        if ("github-auth".equals(provider)) {
            String githubUrl = oAuth2User.getAttribute("html_url");

            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

            user.updateGithub(githubUrl);
            userRepository.save(user);

            response.sendRedirect("https://dev.devpals.site/user/profile?auth=github");
            return;
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        String accessToken = jwtTokenProvider.generateToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(14 * 24 * 60 * 60)
                .build();
        response.setHeader("Set-Cookie", refreshCookie.toString());

        String redirectUri = "http://localhost:5173/login/oauth2/code?accessToken=" + accessToken;
        response.sendRedirect(redirectUri);
    }
}
