package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;


public enum Gender {
    MALE, FEMALE;

    public static Gender from(final String gender) {
        try {
            return Gender.valueOf(gender.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "성별은 MALE, FEMALE 중 하나 입니다. gender=" + gender);
        }
    }
}
