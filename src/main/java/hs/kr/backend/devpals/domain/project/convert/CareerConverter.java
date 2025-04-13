package hs.kr.backend.devpals.domain.project.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hs.kr.backend.devpals.domain.user.dto.CareerDto;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter
public class CareerConverter implements AttributeConverter<List<CareerDto>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CareerConverter() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String convertToDatabaseColumn(List<CareerDto> careerList) {
        if (careerList == null || careerList.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(careerList);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorException.FAIL_JSONPROCESSING);
        }
    }

    @Override
    public List<CareerDto> convertToEntityAttribute(String careerJson) {
        if (careerJson == null || careerJson.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(careerJson, new TypeReference<List<CareerDto>>() {});
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorException.FAIL_JSONPROCESSING);
        }
    }
}
