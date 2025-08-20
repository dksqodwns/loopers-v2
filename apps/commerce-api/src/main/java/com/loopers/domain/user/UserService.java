package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserInfo register(final UserCommand.Register command) {
        User user = new User(
                new LoginId(command.loginId()),
                new Email(command.email()),
                command.username(),
                new BirthDate(command.birthDate()),
                Gender.from(command.gender())
        );
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new CoreException(ErrorType.CONFLICT, "이미 존재하는 이메일 입니다. email=" + command.email());
        }
        if (userRepository.findByLoginId(user.getLoginId()).isPresent()) {
            throw new CoreException(ErrorType.CONFLICT, "이미 존재하는 아이디 입니다. loginId=" + command.loginId());
        }

        return UserInfo.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserInfo getUser(final Long id) {
        return userRepository.findByid(id)
                .map(UserInfo::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당하는 유저를 찾을 수 없습니다. id=" + id));
    }

}
