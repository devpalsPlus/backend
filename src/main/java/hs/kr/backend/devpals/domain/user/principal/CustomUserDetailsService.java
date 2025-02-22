package hs.kr.backend.devpals.domain.user.principal;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 이메일(Username) 기반 사용자 조회
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        return new CustomUserDetails(userEntity, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    /**
     * memberId 기반 사용자 조회
     */
    public UserDetails findById(Long Id) {
        UserEntity userEntity = userRepository.findById(Id)
                .orElseThrow(() -> new CustomException(ErrorException.USER_ID_NOT_FOUND));

        return new CustomUserDetails(userEntity, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
