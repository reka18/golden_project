package com.golden.project.repositories;

import com.golden.project.repositories.entities.Business;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Integer>
{
    @Query(nativeQuery = true, value =
        "select b.name, street_address || ' San Francisco, CA', location_start\n" +
            "from businesses b\n" +
            "inner join neighborhoods n on n.id = b.neighborhood_id and n.name = b.neighborhood_name\n" +
            "where n.name = :neighborhood and location_end is null\n" +
            "order by location_start\n" +
            "limit :count")
    List<Object[]> findTopHundredOldestRunningBusinessesByNeighborhoodAsc(
        @Param("neighborhood") String neighborhood, @Param("count")  Integer count);


    @Query(nativeQuery = true, value =
        "select b.name, street_address || ' San Francisco, CA', location_start\n" +
            "from businesses b\n" +
            "         inner join neighborhoods n on n.id = b.neighborhood_id and n.name = b.neighborhood_name\n" +
            "where n.name = :neighborhood and location_end is null\n" +
            "order by location_start DESC\n" +
            "limit :count")
    List<Object[]> findTopHundredOldestRunningBusinessesByNeighborhoodDesc(
        @Param("neighborhood") String neighborhood, @Param("count")  Integer count);


    @Query(nativeQuery = true, value =
        "select text(((float8(maxx) - float8(minx)) / 2) + float8(minx)) || ','\n" +
            "        || text(((float8(maxy) - float8(miny)) / 2) + float8(miny)) as center\n" +
            "from (select max(split_part(coordinates, ',', 1)) as maxx, min(split_part(coordinates, ',', 1)) as minx,\n" +
            "          max(split_part(coordinates, ',', 2)) as maxy, min(split_part(coordinates, ',', 2)) as miny\n" +
            "      from businesses b\n" +
            "               inner join neighborhoods n on n.id = b.neighborhood_id and n.name = b.neighborhood_name\n" +
            "      where n.name = :neighborhood) p1;")
    String findByNeighborhoodName(@Param("neighborhood") String neighborhood);
}
