package hs.kr.backend.devpals.infra.oauth2;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.principal.CustomUserDetails;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class Oauth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final OauthUserService oauthUserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        if ("github-auth".equals(provider)) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String githubUrl = oAuth2User.getAttribute("html_url");

            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserEntity user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

            user.updateGithub(githubUrl);
            userRepository.save(user);

            response.sendRedirect("http://localhost:5173/oauth/github-success?githubUrl=" + githubUrl);
            return;
        }

        // 일반 소셜 로그인 처리
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null || email.isBlank()) {
            throw new CustomException(ErrorException.USER_NOT_FOUND);
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        String accessToken = jwtTokenProvider.generateToken(user.getId());
        String redirectUrl = "http://localhost:5173/oauth-redirect?accessToken=" + accessToken;
        response.sendRedirect(redirectUrl);
    }
}
