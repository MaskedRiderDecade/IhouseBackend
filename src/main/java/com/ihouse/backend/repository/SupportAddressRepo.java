package com.ihouse.backend.repository;

import com.ihouse.backend.domain.SupportAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportAddressRepo extends JpaRepository<SupportAddress,Long> {

    //获取所有对应行政级别的信息
    List<SupportAddress> findAllByLevel(String level);

    List<SupportAddress> findAllByLevelAndBelongTo(String level,String belongTo);

    SupportAddress findByEnNameAndBelongTo(String enName,String belongTo);

    SupportAddress findByEnNameAndLevel(String enName,String level);

}
