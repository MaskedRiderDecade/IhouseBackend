package com.ihouse.backend.form;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Builder
@Data
public class RentSearch {

    @NotNull
    private String cityEnName;

    private String regionEnName;

    private String priceBlock;

    private String areaBlock;

    private int room;

    private int direction;

    private String keywords;

    private int rentWay = 0;

    private String orderBy;

    private String orderDirection;

    private int page = 1;

    private int size = 5;

    public int getSize() {
        if (this.size < 1) {
            return 5;
        } else if (this.size > 100) {
            return 100;
        } else {
            return this.size;
        }
    }

    public int getRentWay() {
        if (rentWay >0 && rentWay < 3) {
            return rentWay;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "RentSearch{" +
                "cityEnName='" + cityEnName + '\'' +
                ", regionEnName='" + regionEnName + '\'' +
                ", priceBlock='" + priceBlock + '\'' +
                ", areaBlock='" + areaBlock + '\'' +
                ", room=" + room +
                ", direction=" + direction +
                ", keywords='" + keywords + '\'' +
                ", rentWay=" + rentWay +
                ", orderBy='" + orderBy + '\'' +
                ", orderDirection='" + orderDirection + '\'' +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}
