package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.user.dto.CareerDto;
import lombok.Getter;

import java.util.List;

@Getter
public class ProjectApplyRequest {
    private String email;
    private String phoneNumber;
    private String message;
    private List<CareerDto> career;
}
