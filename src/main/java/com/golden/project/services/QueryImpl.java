package com.golden.project.services;

import com.golden.project.enums.ModeEnum;
import com.golden.project.pojos.BusinessResponse;
import com.golden.project.pojos.NeighborhoodResponse;
import com.golden.project.pojos.PointResponse;
import com.golden.project.repositories.BusinessRepository;
import com.golden.project.repositories.NeighborhoodRepository;
import com.golden.project.services.QueryService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.geo.Point;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@EnableAsync
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Service
public class QueryImpl implements QueryService
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final NeighborhoodRepository neighborhoodRepository;
    private final BusinessRepository businessRepository;

    @Autowired
    public QueryImpl(NeighborhoodRepository neighborhoodRepository,
                     BusinessRepository businessRepository)
    {
        this.neighborhoodRepository = neighborhoodRepository;
        this.businessRepository = businessRepository;
    }

    @Override
    public CompletableFuture<NeighborhoodResponse> fetchNeighborhoodsWithBusinessLocationRecords()
    {
        log.info("Serving requested list of neighborhoods");
        CompletableFuture<NeighborhoodResponse> result = new CompletableFuture<>();
        List<String> neighborhoods = neighborhoodRepository.findAllNeighborhoods();
        NeighborhoodResponse response = NeighborhoodResponse.builder()
            .names(neighborhoods)
            .build();
        result.complete(response);
        return result;
    }

    @Override
    public CompletableFuture<BusinessResponse> fetchTopHundredOldestRunningBusinessesByNeighborhood(
        String neighborhood, ModeEnum mode)
    {
        List<Object[]> businesses;
        CompletableFuture<BusinessResponse> result = new CompletableFuture<>();
        switch (mode)
        {
            case OLDEST:
                businesses = businessRepository.findTopHundredOldestRunningBusinessesByNeighborhoodAsc(neighborhood);
                break;
            case NEWEST:
                businesses = businessRepository.findTopHundredOldestRunningBusinessesByNeighborhoodDesc(neighborhood);
                break;
            default:
                throw new IllegalStateException();
        }
        log.info("Serving requested list of top 100 {} operating businesses in {}", mode.getName(), neighborhood);
        Map<String, String> bizMap = new HashMap<>(businesses.size());
        businesses.forEach(
            b -> bizMap.put(String.valueOf(b[0]), String.valueOf(b[1]))
        );
        String message = String.format("These are the top 100 %s still operating businesses in %s", mode.getName(), neighborhood);
        BusinessResponse response = BusinessResponse.builder()
            .message(message)
            .businesses(bizMap)
            .build();
        result.complete(response);
        return result;
    }

    @Override
    public CompletableFuture<PointResponse> fetchGeographicCenterOfNeighborhood(String neighborhood)
    {
        String[] coordString = businessRepository.findByNeighborhoodName(neighborhood).split(",");
        double x = Double.parseDouble(coordString[0]);
        double y = Double.parseDouble(coordString[1]);
        Point centerPoint = new Point(x, y);
        CompletableFuture<PointResponse> result = new CompletableFuture<>();
        PointResponse response = PointResponse.builder()
            .message("This is the center point for " + neighborhood)
            .center(centerPoint)
            .build();
        result.complete(response);
        return result;
    }
}
