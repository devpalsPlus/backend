package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping
public class UserFacadeController {

    private final UserFacade userFacade;

    @GetMapping("/position-tag")
    public ResponseEntity<ApiResponse<List<PositionTagEntity>>> getPositionTag(){
        return userFacade.getPositionTag();
    }
}
