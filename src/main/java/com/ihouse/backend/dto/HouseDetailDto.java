package com.ihouse.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class HouseDetailDto implements Serializable {

    private static final long serialVersionUID = 8918735582286008182L;

    private String description;

    private String layoutDesc;

    private String traffic;

    private String roundService;

    private int rentWay;

    private String detailAddress;

    private Long subwayLineId;

    private Long subwayStationId;

    private String subwayLineName;

    private String subwayStationName;

    @Override
    public String toString() {
        return "HouseDetailDto{" +
                "description='" + description + '\'' +
                ", layoutDesc='" + layoutDesc + '\'' +
                ", traffic='" + traffic + '\'' +
                ", roundService='" + roundService + '\'' +
                ", rentWay=" + rentWay +
                ", detailAddress='" + detailAddress + '\'' +
                ", subwayLineId=" + subwayLineId +
                ", subwayStationId=" + subwayStationId +
                ", subwayLineName='" + subwayLineName + '\'' +
                ", subwayStationName='" + subwayStationName + '\'' +
                '}';
    }
}
