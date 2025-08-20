package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.user.UserCommand.Register;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
public class UserServiceIntegrationTest {

    @MockitoSpyBean
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;


    @AfterEach
    void cleanUp() {
        this.databaseCleanUp.truncateAllTables();
    }

    @DisplayName("유저를 생성 할 때,")
    @Nested
    class Create {
        @DisplayName("이미 존재하는 LoginId면, 409 Conflict를 반환 한다.")
        @Test
        void return409Conflict_whenExistsLoginId() {
            User user = new User(new LoginId("test"), new Email("test@test.com"), "테스터", new BirthDate("1998-01-08"), Gender.from("MALE"));
            userJpaRepository.save(user);

            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> userService.register(new Register("test", "test2@test.com", "테스터", "1998-01-08", "MALE")));

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }

        @DisplayName("이미 존재하는 Email이면, 409 Conflict를 반환 한다.")
        @Test
        void return409Conflict_whenExistsEmail() {

            User user = new User(new LoginId("test"), new Email("test@test.com"), "테스터", new BirthDate("1998-01-08"), Gender.from("MALE"));
            userJpaRepository.save(user);

            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> userService.register(new Register("test2", "test@test.com", "테스터", "1998-01-08", "MALE")));

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }

        @DisplayName("새로운 LoginId, Email일 경우, 유저를 생성한다.")
        @Test
        void createUser() {
            userService.register(new Register("test2", "test@test.com", "테스터", "1998-01-08", "MALE"));

            verify(userJpaRepository, times(1)).save(any());
        }
    }


    @DisplayName("유저 정보를 조회 할 때,")
    @Nested
    class Get {
        @DisplayName("존재하지 않는 id일 경우, 404 Not Found를 반환한다.")
        @Test
        void returnNotFound_whenNotExistsUesr() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> userService.getUser(777L));

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("존재하지 유저일 경우, 유저 정보를 반환한다.")
        @Test
        void returnUser_whenUserExists() {

            User user = new User(new LoginId("test"), new Email("test@test.com"), "테스터", new BirthDate("1998-01-08"), Gender.from("MALE"));

            User savedUser = userJpaRepository.save(user);

            Assertions.assertAll(
                    () -> assertThat(userService.getUser(1L)).isNotNull(),
                    () -> assertThat(userService.getUser(1L).id()).isEqualTo(savedUser.getId()),
                    () -> assertThat(userService.getUser(1L).email()).isEqualTo(savedUser.getEmail()),
                    () -> assertThat(userService.getUser(1L).username()).isEqualTo(savedUser.getUsername())
            );


        }
    }
}
