package hs.kr.backend.devpals.infra.oauth2;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 요청에 provider 저장
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            request.setAttribute("provider", provider);
        }

        String email = extractEmail(provider, oAuth2User, userRequest);
        String name = extractName(provider, oAuth2User);

        if (email == null) {
            throw new IllegalArgumentException("소셜 로그인 응답에서 email을 찾을 수 없습니다.");
        }

        if (!"github-auth".equals(provider)) {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseGet(() -> new UserEntity(email, "SOCIAL_LOGIN_USER", name, true));

            if ("github".equals(provider)) {
                String githubUrl = oAuth2User.getAttribute("html_url");
                user.updateGithub(githubUrl);
            }

            try {
                userRepository.save(user);
            } catch (DataIntegrityViolationException e) {
                throw new CustomException(ErrorException.DUPLICATE_NICKNAME);
            }
        }

        Map<String, Object> attributesMap = new HashMap<>(oAuth2User.getAttributes());
        attributesMap.put("email", email); // email 보장

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributesMap,
                "email"
        );
    }

    public static String extractEmail(String provider, OAuth2User oAuth2User, OAuth2UserRequest userRequest) {
        switch (provider) {
            case "google":
                return oAuth2User.getAttribute("email");
            case "kakao":
                Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
                return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
            case "naver":
                Map<String, Object> response = oAuth2User.getAttribute("response");
                return response != null ? (String) response.get("email") : null;
            case "github":
            case "github-auth":
                String email = oAuth2User.getAttribute("email");
                if (email == null) {
                    email = fetchPrimaryEmailFromGithub(userRequest);
                }
                return email;
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
            case "github":
            case "github-auth":
                return oAuth2User.getAttribute("name");
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    private static String fetchPrimaryEmailFromGithub(OAuth2UserRequest userRequest) {
        String token = userRequest.getAccessToken().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = new RestTemplate()
                .exchange(
                        "https://api.github.com/user/emails",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

        List<Map<String, Object>> emailList = response.getBody();
        log.info("🔍 [GitHub Email API 응답] 시작 ---------");
        if (emailList != null) {
            for (Map<String, Object> emailInfo : emailList) {
                log.info("📧 emailInfo: {}", emailInfo);
            }
        } else {
            log.warn("⚠️ GitHub email 응답이 null 입니다.");
        }

        String primaryEmail = emailList.stream()
                .filter(e -> Boolean.TRUE.equals(e.get("primary")) && Boolean.TRUE.equals(e.get("verified")))
                .map(e -> (String) e.get("email"))
                .findFirst()
                .orElse(null);

        log.info("✅ 선택된 Primary Email: {}", primaryEmail);
        log.info("🔍 [GitHub Email API 응답] 끝 ---------");
        return primaryEmail;
    }
}
