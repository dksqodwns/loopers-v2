package com.loopers.infrastructure.user;

import com.loopers.domain.user.Email;
import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByid(Long id) {
        return this.userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByLoginId(LoginId loginId) {
        return this.userJpaRepository.findByLoginId(loginId);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return this.userJpaRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return this.userJpaRepository.save(user);
    }
}
