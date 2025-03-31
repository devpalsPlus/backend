package hs.kr.backend.devpals.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.project.entity.MethodTypeEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodTypeResponse {
    private  Long id;
    private  String name;
    private  LocalDateTime createAt;

    public static MethodTypeResponse fromEntity(MethodTypeEntity methodType){
        return MethodTypeResponse.builder()
                .id(methodType.getId())
                .name(methodType.getName())
                .createAt(methodType.getCreatedAt())
                .build();
    }
}
