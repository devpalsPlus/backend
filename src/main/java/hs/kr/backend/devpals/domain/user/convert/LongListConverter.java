package hs.kr.backend.devpals.domain.user.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Converter
public class LongListConverter implements AttributeConverter<List<Long>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new ArrayList<>(); // 빈 값일 경우 빈 리스트 반환
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
    }
}
