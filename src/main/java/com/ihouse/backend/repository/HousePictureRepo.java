package com.ihouse.backend.repository;

import com.ihouse.backend.domain.HousePicture;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HousePictureRepo extends CrudRepository<HousePicture,Long> {

    List<HousePicture> findAllByHouseId(Long houseId);

    void deleteById(Long id);
}
