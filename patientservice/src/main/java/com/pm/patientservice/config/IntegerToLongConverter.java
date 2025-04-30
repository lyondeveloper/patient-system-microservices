package com.pm.patientservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class IntegerToLongConverter implements Converter<Integer, Long> {
    @Override
    public Long convert(Integer source) {
        return source != null ? source.longValue() : null;
    }
}
