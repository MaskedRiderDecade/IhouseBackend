package com.ihouse.backend.form;

import lombok.Data;

@Data
public class MapSearch {

    private String cityEnName;

    /**
     * 地图缩放级别
     */
    private int level = 12;
    private String orderBy = "lastUpdateTime";
    private String orderDirection = "desc";
    /**
     * 左上角
     */
    private Double leftLongitude;
    private Double leftLatitude;

    /**
     * 右下角
     */
    private Double rightLongitude;
    private Double rightLatitude;

    private int start = 0;
    private int size = 5;

    public int getStart() {
        return Math.max(start, 0);
    }

    public int getSize() {
        return Math.min(size, 100);
    }

}
