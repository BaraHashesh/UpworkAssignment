package com.ticketingsystem.seat;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class IdListConverter implements AttributeConverter<List<Long>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<Long> stringList) {
        return stringList != null ? stringList.stream().map(Object::toString).collect(Collectors.joining(SPLIT_CHAR)) : "";
    }

    @Override
    public List<Long> convertToEntityAttribute(String string) {
        return string != null && !string.isEmpty() ? Arrays.stream(string.split(SPLIT_CHAR)).map(Long::parseLong).collect(Collectors.toList()) : new ArrayList<>();
    }
}
