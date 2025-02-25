package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.user.dto.UserResponse;
import hs.kr.backend.devpals.domain.user.service.UserService;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final JwtTokenValidator jwtTokenValidator;
    private final UserService userService;


    @GetMapping("/me")
    public UserResponse getUserProfile(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // "Bearer " 제거
        Integer userId = jwtTokenValidator.getUserIdFromToken(jwt); //  JWT에서 userId(PK) 추출
        return userService.getUserById(userId);
    }
}
