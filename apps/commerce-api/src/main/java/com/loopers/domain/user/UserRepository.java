package com.loopers.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByid(Long id);

    Optional<User> findByEmail(Email email);

    Optional<User> findByLoginId(LoginId loginId);

    User save(User user);

}
