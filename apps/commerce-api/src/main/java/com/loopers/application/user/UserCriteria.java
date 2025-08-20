package com.loopers.application.user;

import com.loopers.domain.user.UserCommand;

public class UserCriteria {

    public record Register(String loginId, String email, String username, String birthDate, String gender) {
        public UserCommand.Register toCommand() {
            return new UserCommand.Register(loginId, email, username, birthDate, gender);
        }
    }
}
