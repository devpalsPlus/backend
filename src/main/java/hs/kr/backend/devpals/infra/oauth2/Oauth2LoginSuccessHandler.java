// Oauth2LoginSuccessHandler.java
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
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Oauth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final OauthUserService oauthUserService; // 주입

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        String provider = (String) request.getAttribute("provider");

        if (email == null) {
            throw new CustomException(ErrorException.USER_NOT_FOUND);
        }

        if ("github-auth".equals(provider)) {
            String githubUrl = oAuth2User.getAttribute("html_url");

            // 트랜잭션 서비스 호출
            oauthUserService.updateGithubUrl(email, githubUrl);

            response.sendRedirect("http://localhost:5173/oauth/github-success?githubUrl=" + githubUrl);
            return;
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        String accessToken = jwtTokenProvider.generateToken(user.getId());

        String redirectUrl = "http://localhost:5173/oauth-redirect?accessToken=" + accessToken;
        response.sendRedirect(redirectUrl);
    }
}
