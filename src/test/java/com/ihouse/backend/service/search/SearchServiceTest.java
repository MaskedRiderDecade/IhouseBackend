package com.ihouse.backend.service.search;

import com.ihouse.backend.BackendApplicationTests;
import com.ihouse.backend.form.RentSearch;
import com.ihouse.backend.service.ServiceMultiResult;
import com.ihouse.backend.service.house.HouseService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchServiceTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private HouseService houseService;

    @Test
    public void index() {
        boolean success=searchService.index(24L);
//        searchService.index(24L);
//        searchService.index(18L);
//        searchService.index(19L);
//        searchService.index(20L);
        Assert.assertTrue(success);
    }

    @Test
    public void remove(){
        searchService.remove(37L);
        searchService.remove(38L);
        searchService.remove(39L);


    }

    @Test
    public void testQuery(){
        RentSearch rentSearch=RentSearch.builder()
                .keywords("皇家")
//                .priceBlock("5000-10000")
                .direction(2)
                .cityEnName("bj")
                .page(1)
                .size(10)
                .orderBy("price")
                .orderDirection("desc")
                .build();
        ServiceMultiResult res=houseService.query(rentSearch);
        Integer i=1;
    }
}