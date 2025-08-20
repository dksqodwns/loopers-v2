package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {
    @Embedded
    private LoginId loginId;

    @Embedded
    private Email email;

    private String username;

    @Embedded
    private BirthDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    public User(
            final LoginId loginId,
            final Email email,
            String username,
            final BirthDate birthDate,
            final Gender gender
    ) {
        if (gender == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "성별은 비어있을 수 없습니다.");
        }

        this.loginId = loginId;
        this.email = email;
        this.username = username;
        this.birthDate = birthDate;
        this.gender = gender;
    }
}



