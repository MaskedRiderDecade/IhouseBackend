package com.ihouse.backend.repository;

import com.ihouse.backend.domain.HouseTag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HouseTagRepo extends CrudRepository<HouseTag,Long> {
    List<HouseTag> findAllByHouseId(Long houseId);

    HouseTag findByNameAndHouseId(String name,Long houseId);

    void deleteById(Long id);

    List<HouseTag> findAllByHouseIdIn(List<Long>houseIds);
}
