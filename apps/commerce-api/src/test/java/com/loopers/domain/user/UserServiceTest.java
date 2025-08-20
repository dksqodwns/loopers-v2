package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.loopers.domain.user.UserCommand.Register;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @DisplayName("유저를 생성 할 때,")
    @Nested
    class Create {

        @DisplayName("이미 존재하는 loginId일 경우, 409 Conflict를 반환한다.")
        @Test
        void return409Conflict_whenExistsLoginId() {
            // given
            given(userRepository.findByEmail(any(Email.class))).willReturn(Optional.empty());
            given(userRepository.findByLoginId(any(LoginId.class))).willReturn(Optional.of(new User()));

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> userService.register(new Register("test", "test@test.com", "테스터", "1998-01-08", "MALE")));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }


        @DisplayName("이미 존재하는 email일 경우, 409 Conflict를 반환한다.")
        @Test
        void return409Conflict_whenExistsEmail() {
            // given
            given(userRepository.findByEmail(any(Email.class))).willReturn(Optional.of(new User()));

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> userService.register(new Register("test", "test@test.com", "테스터", "1998-01-08", "MALE")));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }
    }

    @DisplayName("유저 정보를 조회 할 때,")
    @Nested
    class Get {

        @DisplayName("존재하지 않는 유저일 경우, 404 Not Found를 반환한다.")
        @Test
        void return404NotFound_whenNotExistsUser() {
            // given
            given(userRepository.findByid(any(Long.class))).willReturn(Optional.empty());

            // when
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> userService.getUser(777L));

            // then
            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
