package com.ihouse.backend.repository;

import com.ihouse.backend.domain.SubwayStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubwayStationRepo extends JpaRepository<SubwayStation,Long> {

    List<SubwayStation> findAllBySubwayId(Long subwayId);

    Optional<SubwayStation> findById(Long subwayStationId);

}
