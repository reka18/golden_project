package com.golden.project.services;

import com.golden.project.enums.ModeEnum;
import com.golden.project.pojos.BusinessResponse;
import com.golden.project.pojos.NeighborhoodResponse;
import com.golden.project.pojos.PointResponse;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;

public interface QueryService
{
    @Async
    CompletableFuture<NeighborhoodResponse> fetchNeighborhoodsWithBusinessLocationRecords();

    @Async
    CompletableFuture<BusinessResponse> fetchTopHundredOldestRunningBusinessesByNeighborhood(
        String neighborhood, ModeEnum mode, Integer count);

    @Async
    CompletableFuture<PointResponse> fetchGeographicCenterOfNeighborhood(String neighborhood);
}
