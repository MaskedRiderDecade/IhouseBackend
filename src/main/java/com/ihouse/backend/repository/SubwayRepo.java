package com.ihouse.backend.repository;

import com.ihouse.backend.domain.Subway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubwayRepo extends JpaRepository<Subway,Long> {

    List<Subway>findAllByCityEnName(String cityEnName);

    Optional<Subway> findById(Long id);

}
