package com.ihouse.backend.util;

import com.ihouse.backend.domain.House;
import com.ihouse.backend.domain.HouseDetail;
import com.ihouse.backend.domain.HousePicture;
import com.ihouse.backend.domain.HouseTag;
import com.ihouse.backend.dto.HouseDetailDto;
import com.ihouse.backend.dto.HouseDto;
import com.ihouse.backend.dto.HousePictureDto;
import com.ihouse.backend.service.search.HouseIndexTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

public class ConvertUtil {

    private static String cdnPrefix="121.4.81.114/img/";

    public static HouseDto convertHouseDto(House house,HouseDetailDto houseDetailDto,List<HousePictureDto>housePictureDtos,List<String>tags){
        HouseDto houseDto= HouseDto.builder()
                .id(house.getId())
                .houseDetail(houseDetailDto)
                .pictures(housePictureDtos)
                .title(house.getTitle())
                .price(house.getPrice())
                .area(house.getArea())
                .direction(house.getDirection())
                .room(house.getRoom())
                .parlour(house.getParlour())
                .bathroom(house.getBathroom())
                .floor(house.getFloor())
                .adminId(house.getAdminId())
                .district(house.getDistrict())
                .totalFloor(house.getTotalFloor())
                .watchTimes(house.getWatchTimes())
                .buildYear(house.getBuildYear())
                .status(house.getStatus())
                .createTime(house.getCreateTime())
                .lastUpdateTime(house.getLastUpdateTime())
                .cityEnName(house.getCityEnName())
                .regionEnName(house.getRegionEnName())
                .street(house.getStreet())
                .cover(cdnPrefix+house.getCover())
                .distanceToSubway(house.getDistanceToSubway())
                .build();
        if(tags!=null&&!tags.isEmpty()){
            houseDto.setTags(tags);
        }
        return houseDto;
    }

    public static HouseDto convertHouseDto(House house,Long adminId){
        HouseDto houseDto= HouseDto.builder()
                .id(house.getId())
                .adminId(adminId)
                .title(house.getTitle())
                .price(house.getPrice())
                .area(house.getArea())
                .direction(house.getDirection())
                .room(house.getRoom())
                .parlour(house.getParlour())
                .bathroom(house.getBathroom())
                .floor(house.getFloor())
                .adminId(house.getAdminId())
                .district(house.getDistrict())
                .totalFloor(house.getTotalFloor())
                .watchTimes(house.getWatchTimes())
                .buildYear(house.getBuildYear())
                .status(house.getStatus())
                .createTime(house.getCreateTime())
                .lastUpdateTime(house.getLastUpdateTime())
                .cityEnName(house.getCityEnName())
                .regionEnName(house.getRegionEnName())
                .street(house.getStreet())
                .cover(cdnPrefix+house.getCover())
                .distanceToSubway(house.getDistanceToSubway())
                .build();
        return houseDto;
    }

    public static HouseDto convertHouseDto(House house){
        HouseDto houseDto= HouseDto.builder()
                .id(house.getId())
                .adminId(house.getAdminId())
                .title(house.getTitle())
                .price(house.getPrice())
                .area(house.getArea())
                .direction(house.getDirection())
                .room(house.getRoom())
                .parlour(house.getParlour())
                .bathroom(house.getBathroom())
                .floor(house.getFloor())
                .adminId(house.getAdminId())
                .district(house.getDistrict())
                .totalFloor(house.getTotalFloor())
                .watchTimes(house.getWatchTimes())
                .buildYear(house.getBuildYear())
                .status(house.getStatus())
                .createTime(house.getCreateTime())
                .lastUpdateTime(house.getLastUpdateTime())
                .cityEnName(house.getCityEnName())
                .regionEnName(house.getRegionEnName())
                .street(house.getStreet())
                .cover(cdnPrefix+house.getCover())
                .distanceToSubway(house.getDistanceToSubway())
                .build();
        return houseDto;
    }

    public static HouseDetailDto convertHouseDetailDto(HouseDetail houseDetail){
        return HouseDetailDto.builder()
                .description(houseDetail.getDescription())
                .layoutDesc(houseDetail.getLayoutDesc())
                .traffic(houseDetail.getTraffic())
                .roundService(houseDetail.getRoundService())
                .rentWay(houseDetail.getRentWay())
                .detailAddress(houseDetail.getDetailAddress())
                .subwayLineId(houseDetail.getSubwayLineId())
                .subwayLineName(houseDetail.getSubwayLineName())
                .subwayStationId(houseDetail.getSubwayStationId())
                .subwayStationName(houseDetail.getSubwayStationName())
                .build();
    }

    public static List<HousePictureDto> convertHousePictureDto(List<HousePicture> housePictures){
        return housePictures.stream().map(housePicture -> {
            return HousePictureDto.builder()
                    .id(housePicture.getId())
                    .houseId(housePicture.getHouseId())
                    .path(housePicture.getPath())
                    .cdnPrefix(housePicture.getCdnPrefix())
                    .width(housePicture.getWidth())
                    .height(housePicture.getHeight()).build();
        }).collect(Collectors.toList());
    }

    public static List<String>convertHouseTagStr(List<HouseTag>tags){
        return tags.stream().map(tag->{
            return tag.getName();
        }).collect(Collectors.toList());
    }

    public static HouseIndexTemplate convertHouseIndexTemplate(House house,HouseDetail houseDetail){
        return HouseIndexTemplate.builder()
                .houseId(house.getId())
                .title(house.getTitle())
                .price(house.getPrice())
                .area(house.getArea())
                .createTime(house.getCreateTime())
                .lastUpdateTime(house.getLastUpdateTime())
                .cityEnName(house.getCityEnName())
                .regionEnName(house.getRegionEnName())
                .direction(house.getDirection())
                .distanceToSubway(house.getDistanceToSubway())
                .street(house.getStreet())
                .district(house.getDistrict())
                .description(houseDetail.getDescription())
                .layoutDesc(houseDetail.getLayoutDesc())
                .traffic(houseDetail.getTraffic())
                .roundService(houseDetail.getRoundService())
                .rentWay(houseDetail.getRentWay())
                .subwayLineName(houseDetail.getSubwayLineName())
                .subwayStationName(houseDetail.getSubwayStationName())
                .build();

    }

}
