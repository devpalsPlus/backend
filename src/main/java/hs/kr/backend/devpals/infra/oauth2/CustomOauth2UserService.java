package hs.kr.backend.devpals.infra.oauth2;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // request에 provider 저장
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            request.setAttribute("provider", provider);
        }

        String email = extractEmail(provider, oAuth2User);
        String name = extractName(provider, oAuth2User);

        if (email == null) {
            throw new IllegalArgumentException("소셜 로그인 응답에서 email을 찾을 수 없습니다.");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new UserEntity(email, null, name, true)));

        // attributes에 email이 없는 경우 추가해줌 (DefaultOAuth2User 생성시 필요)
        Map<String, Object> attributesMap = new java.util.HashMap<>(oAuth2User.getAttributes());
        attributesMap.put("email", email);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributesMap,
                "email"
        );
    }

    public static String extractEmail(String provider, OAuth2User oAuth2User) {
        switch (provider) {
            case "google":
                return oAuth2User.getAttribute("email");
            case "kakao":
                Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
                return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
            case "naver":
                Map<String, Object> response = oAuth2User.getAttribute("response");
                return response != null ? (String) response.get("email") : null;
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    public static String extractName(String provider, OAuth2User oAuth2User) {
        switch (provider) {
            case "google":
                return oAuth2User.getAttribute("name");
            case "kakao":
                Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
                if (kakaoAccount == null) return null;
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                return profile != null ? (String) profile.get("nickname") : null;
            case "naver":
                Map<String, Object> response = oAuth2User.getAttribute("response");
                return response != null ? (String) response.get("name") : null;
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }
}
