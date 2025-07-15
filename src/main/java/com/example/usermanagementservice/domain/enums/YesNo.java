package com.example.usermanagementservice.domain.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;

@Getter
@RequiredArgsConstructor
public enum YesNo {
    YES("Yes", true),
    NO("No", false),
    @JsonEnumDefaultValue
    NA("N/A", null);

    private final String description;
    @JsonValue
    private final Boolean value;

    /**
     * Gets the deserialised YesNo value of a boolean.
     *
     * @param value the boolean to deserialise.
     * @return the YesNo value.
     */
    @JsonCreator
    public static YesNo jsonCreator(Boolean value) {
        if (isNull(value)) {
            return NA;
        }
        return TRUE.equals(value) ? YES : NO;
    }
}
