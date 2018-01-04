package com.bechtle.util;

import com.bechtle.model.Matchtype;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class MatchtypeConverter implements AttributeConverter<Matchtype, String> {

    @Override
    public String convertToDatabaseColumn(Matchtype attribute) {
        switch (attribute) {
            case DEATH_MATCH:
                return "D";
            case DEATH_MATCH_BO3:
                return "B";
            case REGULAR:
                return "R";
            default:
                throw new IllegalArgumentException("Unknown" + attribute);
        }
    }

    @Override
    public Matchtype convertToEntityAttribute(String dbData) {
        switch (dbData) {
            case "D":
                return Matchtype.DEATH_MATCH;
            case "B":
                return Matchtype.DEATH_MATCH_BO3;
            case "R":
                return Matchtype.REGULAR;
            default:
                throw new IllegalArgumentException("Unknown" + dbData);
        }
    }
}