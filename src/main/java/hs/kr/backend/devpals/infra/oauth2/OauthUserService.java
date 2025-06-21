package hs.kr.backend.devpals.infra.oauth2;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthUserService {

    private final UserRepository userRepository;

    @Transactional
    public void updateGithubUrl(String email, String githubUrl) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
        user.updateGithub(githubUrl);
        userRepository.save(user);
    }
}