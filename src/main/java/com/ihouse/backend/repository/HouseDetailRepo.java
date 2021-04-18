package com.ihouse.backend.repository;

import com.ihouse.backend.domain.HouseDetail;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HouseDetailRepo extends CrudRepository<HouseDetail,Long> {

    HouseDetail save(HouseDetail houseDetail);

    HouseDetail findByHouseId(Long houseId);

    List<HouseDetail> findAllByHouseIdIn(List<Long>houseIds);

}
