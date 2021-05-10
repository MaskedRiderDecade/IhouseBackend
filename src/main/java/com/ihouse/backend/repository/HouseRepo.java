package com.ihouse.backend.repository;

import com.ihouse.backend.domain.House;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Date;
import java.util.List;

public interface HouseRepo extends CrudRepository<House,Long>,JpaSpecificationExecutor<House> {

     Page<House> findByAdminIdAndStatusIsNotOrderByCreateTimeDesc(Long adminId,int status, Pageable pageable);

     Page<House> findByAdminIdAndCityEnNameAndStatusIsNotOrderByCreateTimeDesc(Long adminId, String cityEnName, int status, Pageable pageable);

     Page<House> findByStatusAndCityEnNameOrderByLastUpdateTimeDesc(int status, String cityEnName, Pageable pageable);

     Page<House> findByStatusAndCityEnNameOrderByPriceAsc(int status, String cityEnName, Pageable pageable);

     Page<House> findByStatusAndCityEnNameOrderByAreaDesc(int status, String cityEnName, Pageable pageable);

     Page<House> findByStatusAndCityEnNameAndRegionEnNameOrderByLastUpdateTimeDesc(int status, String cityEnName, String regionEnName, Pageable pageable);

     Page<House> findByStatusAndCityEnNameAndRegionEnNameOrderByPriceAsc(int status, String cityEnName, String regionEnName, Pageable pageable);

     Page<House> findByStatusAndCityEnNameAndRegionEnNameOrderByAreaDesc(int status, String cityEnName, String regionEnName, Pageable pageable);

    List<House> findAllByIdIn(List<Long>ids);
}
