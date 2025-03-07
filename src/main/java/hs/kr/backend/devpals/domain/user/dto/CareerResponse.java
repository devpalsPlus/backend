package hs.kr.backend.devpals.domain.user.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class CareerResponse {
    private String name;
    private String role;
    private String periodStart;
    private String periodEnd;

    // JSON 문자열을 List<CareerResponse>로 변환하는 메서드
    public static List<CareerResponse> fromJson(String careerJson) {
        if (careerJson == null || careerJson.isEmpty()) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Map<String, Object>> careerList = objectMapper.readValue(careerJson, List.class);
            return careerList.stream()
                    .map(CareerResponse::fromMap)
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorException.FAIL_JSONPROCESSING);
        }
    }

    // Map -> CareerResponse 변환 메서드
    public static CareerResponse fromMap(Map<String, Object> careerMap) {
        return new CareerResponse(
                (String) careerMap.get("name"),
                (String) careerMap.get("role"),
                (String) careerMap.get("periodStart"),
                (String) careerMap.get("periodEnd")
        );
    }
}


