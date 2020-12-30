package com.golden.project.controllers;

import com.golden.project.enums.ModeEnum;
import com.golden.project.pojos.BusinessResponse;
import com.golden.project.pojos.NeighborhoodResponse;
import com.golden.project.pojos.PointResponse;
import com.golden.project.services.QueryService;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueryController
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final QueryService queryService;

    @Autowired
    public QueryController(QueryService queryService)
    {
        this.queryService = queryService;
    }

    @RequestMapping(
        method = RequestMethod.GET,
        path = "/neighborhoods",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public NeighborhoodResponse getBusinessLocations() throws ExecutionException, InterruptedException
    {
        log.info("Requesting all neighborhoods with business locations");
        return queryService.fetchNeighborhoodsWithBusinessLocationRecords().get();
    }

    @RequestMapping(
        method = RequestMethod.GET,
        path = "/top",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public BusinessResponse getTopHundredOldest(
        @RequestParam("neighborhood") String neighborhood,
        @RequestParam("mode") String mode,
        @RequestParam("count") Integer count) throws ExecutionException, InterruptedException
    {
        log.info("Requesting top {} oldest businesses in {}", count, neighborhood);
        ModeEnum modeEnum = ModeEnum.getInstanceByName(mode);
        return queryService.fetchTopHundredOldestRunningBusinessesByNeighborhood(neighborhood, modeEnum, count).get();
    }

    @RequestMapping(
        method = RequestMethod.GET,
        path = "/neighborhood-geographic-center",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public PointResponse getNeighborhoodGeographicCenter(
        @RequestParam("neighborhood") String neighborhood) throws ExecutionException, InterruptedException
    {
        log.info("Requesting geographic center of {}", neighborhood);
        return queryService.fetchGeographicCenterOfNeighborhood(neighborhood).get();
    }
}
