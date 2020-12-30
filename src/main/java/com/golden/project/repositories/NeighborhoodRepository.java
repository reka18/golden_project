package com.golden.project.repositories;

import com.golden.project.repositories.entities.Neighborhood;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NeighborhoodRepository extends JpaRepository<Neighborhood, Integer>
{
    @Query(value = "SELECT name FROM neighborhoods;", nativeQuery = true)
    List<String> findAllNeighborhoods();
}
