package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.MethodTypeEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
public class MethodTypeResponse {
    private final Long id;
    private final String name;
    private final LocalDateTime createAt = LocalDateTime.now();

    @Builder
    public MethodTypeResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static MethodTypeResponse fromEntity(MethodTypeEntity methodType){
        return MethodTypeResponse.builder()
                .id(methodType.getId())
                .name(methodType.getName())
                .build();
    }
}
