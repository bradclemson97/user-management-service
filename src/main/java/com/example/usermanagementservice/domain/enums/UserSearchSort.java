package com.example.usermanagementservice.domain.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Enum for sort criteria to be used when searching for users.
 */
@Getter
@RequiredArgsConstructor
public enum UserSearchSort {
    @JsonEnumDefaultValue
    NAME(Sort.by("userDetails.lastName")
            .and(Sort.by("userDetails.firstName"))
            .and(Sort.by("userDetails.middleName"))),
    EMAIL(Sort.by("userDetails.primaryEmail"));

    private final Sort sort;

    /**
     * returns the sort with a given direction.
     *
     * @param direction the direction to sort by.
     * @return the sort with the given direction
     */
    public Sort getSort(Sort.Direction direction) {
        if (DESC == direction) {
            return sort.reverse();
        }
        return sort;
    }

}
