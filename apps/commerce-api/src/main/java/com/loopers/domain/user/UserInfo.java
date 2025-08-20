package com.loopers.domain.user;

import java.time.LocalDate;

public record UserInfo(Long id, LoginId loginId, Email email, String username, BirthDate birthDate, Gender gender) {
    public static UserInfo from(final User user) {
        return new UserInfo(
                user.getId(),
                user.getLoginId(),
                user.getEmail(),
                user.getUsername(),
                user.getBirthDate(),
                user.getGender()
        );
    }

    public String getLoginId() {
        return loginId.getLoginId();
    }

    public String getEmail() {
        return email.getEmail();
    }

    public LocalDate getBirthDate() {
        return birthDate.getBirthDate();
    }

}
