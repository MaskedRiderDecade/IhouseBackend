package com.ihouse.backend.form;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class HouseSearch {

    //页码
    private int page;
    //页大小
    private int pageSize;

    private Integer status;

    private String city;
    private String title;
    private String direction;
    private String orderBy;

}
