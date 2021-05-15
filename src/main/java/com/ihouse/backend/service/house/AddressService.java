package com.ihouse.backend.service.house;

import com.ihouse.backend.domain.Subway;
import com.ihouse.backend.domain.SubwayStation;
import com.ihouse.backend.domain.SupportAddress;
import com.ihouse.backend.dto.SubwayDto;
import com.ihouse.backend.dto.SubwayStationDto;
import com.ihouse.backend.dto.SupportAddressDto;
import com.ihouse.backend.exception.DataNotFoundException;
import com.ihouse.backend.exception.ParamsNotValidException;
import com.ihouse.backend.repository.SubwayRepo;
import com.ihouse.backend.repository.SubwayStationRepo;
import com.ihouse.backend.repository.SupportAddressRepo;
import com.ihouse.backend.service.ServiceMultiResult;
import com.ihouse.backend.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//地址服务
@Service
public class AddressService {

    @Autowired
    private SupportAddressRepo supportAddressRepo;

    @Autowired
    private SubwayRepo subwayRepo;

    @Autowired
    private SubwayStationRepo subwayStationRepo;

    public ServiceMultiResult<SupportAddressDto> findAllCities(){
        List<SupportAddress> dbResList=supportAddressRepo.findAllByLevel(SupportAddress.Level.CITY.getValue());
        List<SupportAddressDto> resList=dbResList.stream().map(dbRes-> SupportAddressDto.builder()
                .id(dbRes.getId())
                .level(dbRes.getLevel())
                .cnName(dbRes.getCnName())
                .enName(dbRes.getEnName())
                .belongTo(dbRes.getBelongTo())
                .baiduMapLatitude(dbRes.getBaiduMapLatitude())
                .baiduMapLongitude(dbRes.getBaiduMapLongitude())
                .build()).collect(Collectors.toList());

        return new ServiceMultiResult<>(resList.size(),resList);
    }

    public Map<SupportAddress.Level, SupportAddressDto> findCityAndRegion(String cityEnName, String regionEnName) {
        Map<SupportAddress.Level, SupportAddressDto> result = new HashMap<>();

        SupportAddress city = supportAddressRepo.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY
                .getValue());
        SupportAddress region = supportAddressRepo.findByEnNameAndBelongTo(regionEnName, city.getEnName());

        SupportAddressDto cityDto=SupportAddressDto.builder()
                .id(city.getId())
                .cnName(city.getCnName())
                .enName(city.getEnName())
                .baiduMapLatitude(city.getBaiduMapLatitude())
                .baiduMapLongitude(city.getBaiduMapLongitude())
                .belongTo(city.getBelongTo())
                .level(city.getLevel())
                .build();

        SupportAddressDto regionDto=SupportAddressDto.builder()
                .id(region.getId())
                .cnName(region.getCnName())
                .enName(region.getEnName())
                .baiduMapLatitude(region.getBaiduMapLatitude())
                .baiduMapLongitude(region.getBaiduMapLongitude())
                .belongTo(region.getBelongTo())
                .level(region.getLevel())
                .build();

        result.put(SupportAddress.Level.CITY, cityDto);
        result.put(SupportAddress.Level.REGION, regionDto);

        return result;
    }

    public ServiceMultiResult<SupportAddressDto> findAllRegionsByCityName(String cityName) {
        if(cityName==null||cityName.isEmpty()){
            return new ServiceMultiResult<>(0, null);
        }
        List<SupportAddress> regions = supportAddressRepo.findAllByLevelAndBelongTo(SupportAddress.Level.REGION
                .getValue(), cityName);
        List<SupportAddressDto>resList=regions.stream().map(region->SupportAddressDto.builder()
                .id(region.getId())
                .level(region.getLevel())
                .cnName(region.getCnName())
                .enName(region.getEnName())
                .belongTo(region.getBelongTo())
                .baiduMapLatitude(region.getBaiduMapLatitude())
                .baiduMapLongitude(region.getBaiduMapLongitude())
                .build()).collect(Collectors.toList());
        return new ServiceMultiResult<>(resList.size(),resList);
    }

    public ServiceMultiResult<SubwayDto> findAllSubwayByCity(String cityEnName) {
        if(cityEnName==null||cityEnName.isEmpty()){
            return new ServiceMultiResult<>(0, null);
        }
        List<Subway> subways = subwayRepo.findAllByCityEnName(cityEnName);
        List<SubwayDto>resList=subways.stream().map(subway -> SubwayDto.builder()
                .id(subway.getId())
                 .name(subway.getName())
                 .cityEnName(subway.getCityEnName())
                 .build()).collect(Collectors.toList());
        return new ServiceMultiResult<>(resList.size(),resList);
    }

    public ServiceMultiResult<SubwayStationDto> findAllStationBySubway(Long subwayId){
        if(subwayId==null){
            return new ServiceMultiResult<>(0, null);
        }
        List<SubwayStation>subwayStations=subwayStationRepo.findAllBySubwayId(subwayId);
        List<SubwayStationDto>resList=subwayStations.stream().map(subwayStation -> SubwayStationDto.builder()
                .id(subwayStation.getId())
                .subwayId(subwayStation.getSubwayId())
                .name(subwayStation.getName())
                .build()).collect(Collectors.toList());
        return new ServiceMultiResult<>(resList.size(),resList);
    }

    public ServiceResult<SupportAddressDto> findCity(String cityEnName) {
        if (cityEnName == null) {
            throw new ParamsNotValidException();
        }

        SupportAddress supportAddress = supportAddressRepo.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());
        if (supportAddress == null) {
            throw new DataNotFoundException();
        }

        SupportAddressDto addressDto = SupportAddressDto.builder()
                .cnName(supportAddress.getCnName())
                .enName(supportAddress.getEnName())
                .belongTo(supportAddress.getBelongTo())
                .level(supportAddress.getLevel())
                .baiduMapLatitude(supportAddress.getBaiduMapLatitude())
                .baiduMapLongitude(supportAddress.getBaiduMapLongitude())
                .id(supportAddress.getId())
                .build();
        return new ServiceResult(addressDto);
    }


}
