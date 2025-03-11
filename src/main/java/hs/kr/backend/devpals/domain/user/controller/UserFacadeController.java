package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping
@Tag(name = "Skill,Position API", description = "스킬, 포지션을 가져오는 API")
public class UserFacadeController {

    private final UserFacade userFacade;

    @GetMapping("/position-tag")
    @Operation(summary = "모든 포지션 조회", description = "저장된 모든 포지션 데이터를 조회합니다.")
    public ResponseEntity<ApiResponse<List<PositionTagEntity>>> getPositionTag(){
        return userFacade.getPositionTag();
    }

    @GetMapping("/skill-tag")
    @Operation(summary = "모든 스킬 조회", description = "저장된 모든 스킬 데이터를 조회합니다.")
    public ResponseEntity<ApiResponse<List<SkillTagEntity>>> getSkillTag(){
        return userFacade.getSkillTags();
    }
}
