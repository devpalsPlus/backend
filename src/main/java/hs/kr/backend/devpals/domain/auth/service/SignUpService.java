package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.user.dto.SingUpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SignUpService {

    public ResponseEntity<Map<String, Object>> signUp(SingUpRequest request) {
        return null;
    }
}
