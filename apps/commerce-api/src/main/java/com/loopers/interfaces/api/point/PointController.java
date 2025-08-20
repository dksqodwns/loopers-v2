package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.application.point.PointResult;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointDto.V1.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointController implements PointV1ApiSpec {
    private final PointFacade pointFacade;

    @Override
    @PostMapping("/charge")
    public ApiResponse<PointResponse> charge(@RequestHeader("X-USER-ID") Long userId, @RequestBody PointDto.V1.ChargeRequest request) {
        PointResult pointResult = pointFacade.charge(request.toCriteira(userId));
        PointResponse response = PointResponse.from(pointResult);
        return ApiResponse.success(response);
    }

    @Override
    @GetMapping
    public ApiResponse<PointDto.V1.PointResponse> getPoint(@RequestHeader("X-USER-ID") Long userId) {
        PointResult pointResult = pointFacade.getPoint(userId);
        PointResponse response = PointResponse.from(pointResult);
        return ApiResponse.success(response);
    }
}
