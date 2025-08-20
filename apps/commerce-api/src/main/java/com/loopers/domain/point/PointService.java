package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PointService {
    private final PointRepository pointRepository;

    @Transactional
    public PointInfo create(final PointCommand.Create command) {
        final Point point = new Point(command.userId(), new Amount(command.amount()));
        if (pointRepository.existsByUserId(point.getUserId())) {
            throw new CoreException(ErrorType.CONFLICT, "유저의 포인트가 이미 존재합니다.");
        }
        Point savedPoint = pointRepository.save(point);

        return PointInfo.from(savedPoint);
    }

    @Transactional(readOnly = true)
    public PointInfo getPoint(final Long userId) {
        return pointRepository.findByUserId(userId)
                .map(PointInfo::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "회원의 포인트가 존재하지 않습니다."));
    }

    @Transactional
    public PointInfo charge(final PointCommand.Charge command) {
        Point point = pointRepository.findByUserIdForUpdate(command.userId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당하는 포인트를 찾을 수 없습니다. userId: " + command.userId()));
        point.charge(command.amount());
        return PointInfo.from(point);
    }

    @Transactional
    public PointInfo use(final PointCommand.Use command) {
        Point point = pointRepository.findByUserIdForUpdate(command.userId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "유저가 존재하지 않습니다."));
        point.use(command.amount());
        return PointInfo.from(point);
    }
}
