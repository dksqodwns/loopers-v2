package com.loopers.application.user;

import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserFacade {
    private final UserService userService;
    private final PointService pointService;

    public UserResult register(UserCriteria.Register criteria) {
        final UserInfo userInfo = this.userService.register(criteria.toCommand());
        pointService.create(new PointCommand.Create(userInfo.id(), 0L));

        return UserResult.from(userInfo);
    }

    public UserResult getUser(Long userId) {
        final UserInfo userInfo = this.userService.getUser(userId);
        return UserResult.from(userInfo);
    }

}
