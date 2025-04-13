package hs.kr.backend.devpals.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CareerDto {
    private String name;
    private String role;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate periodStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate periodEnd;

    // JSON 문자열을 List<CareerResponse>로 변환하는 메서드
    public static List<CareerDto> fromJson(String careerJson) {
        if (careerJson == null || careerJson.isEmpty()) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Map<String, Object>> careerList = objectMapper.readValue(careerJson, List.class);
            return careerList.stream()
                    .map(CareerDto::fromMap)
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorException.FAIL_JSONPROCESSING);
        }
    }

    // Map -> CareerResponse 변환 메서드
    public static CareerDto fromMap(Map<String, Object> careerMap) {
        return new CareerDto(
                (String) careerMap.get("name"),
                (String) careerMap.get("role"),
                careerMap.get("periodStart") != null ? LocalDate.parse((String) careerMap.get("periodStart")) : null,
                careerMap.get("periodEnd") != null ? LocalDate.parse((String) careerMap.get("periodEnd")) : null
        );
    }
}


