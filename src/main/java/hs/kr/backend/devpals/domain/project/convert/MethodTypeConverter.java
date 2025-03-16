package hs.kr.backend.devpals.domain.project.convert;

import hs.kr.backend.devpals.global.common.enums.MethodType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MethodTypeConverter implements AttributeConverter<MethodType, String> {

    @Override
    public String convertToDatabaseColumn(MethodType methodType) {
        return methodType != null ? methodType.getValue() : null;
    }

    @Override
    public MethodType convertToEntityAttribute(String value) {
        return MethodType.from(value);
    }
}