package com.ihouse.backend.controller.house;

import com.ihouse.backend.domain.SupportAddress;
import com.ihouse.backend.domain.User;
import com.ihouse.backend.dto.HouseDto;
import com.ihouse.backend.dto.SubwayDto;
import com.ihouse.backend.dto.SubwayStationDto;
import com.ihouse.backend.dto.SupportAddressDto;
import com.ihouse.backend.enums.ResponseEnum;
import com.ihouse.backend.exception.ParamsNotValidException;
import com.ihouse.backend.form.RentSearch;
import com.ihouse.backend.service.ServiceMultiResult;
import com.ihouse.backend.service.ServiceResult;
import com.ihouse.backend.service.house.AddressService;
import com.ihouse.backend.service.house.HouseService;
import com.ihouse.backend.service.user.UserUtil;
import com.ihouse.backend.vo.HouseVo;
import com.ihouse.backend.vo.ResponseVo;
import com.ihouse.backend.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
public class HouseController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private UserUtil userUtil;

    @GetMapping("address/support/cities")
    public ResponseVo getSupportCities(){
        ServiceMultiResult<SupportAddressDto> result = addressService.findAllCities();
        if (result.getResultSize() == 0) {
            return ResponseVo.error(ResponseEnum.DB_NOT_FOUND);
        }
        return ResponseVo.success(result.getResult());
    }

    @GetMapping("address/support/regions")
    public ResponseVo getSupportRegions(@RequestParam(name = "city_name") String cityEnName){
        ServiceMultiResult<SupportAddressDto> addressResult = addressService.findAllRegionsByCityName(cityEnName);
        if (addressResult.getResultSize()==0) {
            return ResponseVo.error(ResponseEnum.DB_NOT_FOUND);
        }
        return ResponseVo.success(addressResult.getResult());
    }

    @GetMapping("address/support/subway/line")
    public ResponseVo getSupportSubwayLine(@RequestParam(name = "city_name") String cityEnName){
        ServiceMultiResult<SubwayDto> subwayResult = addressService.findAllSubwayByCity(cityEnName);
        if (subwayResult.getResultSize()==0) {

            return ResponseVo.error(ResponseEnum.DB_NOT_FOUND);

        }
        return ResponseVo.success(subwayResult.getResult());
    }

    @GetMapping("address/support/subway/station")
    public ResponseVo getSupportSubwayStation(@RequestParam(name = "subway_id") Long subwayId){
        ServiceMultiResult<SubwayStationDto> stationResult = addressService.findAllStationBySubway(subwayId);
        if(stationResult.getResultSize()==0){
            return ResponseVo.error(ResponseEnum.DB_NOT_FOUND);
        }
        return ResponseVo.success(stationResult.getResult());
    }

    @PostMapping("rent/house")
    public ResponseVo showRentHouse(@Valid @RequestBody RentSearch rentSearch, BindingResult bindingResult){
        if(rentSearch.getRegionEnName()==null){
            rentSearch.setRegionEnName("*");
        }
        if(rentSearch.getPage()==0||rentSearch.getSize()==0){
            rentSearch.setPage(1);
            rentSearch.setSize(5);
        }
        if(rentSearch.getOrderBy()==null){
            rentSearch.setOrderBy("lastUpdateTime");
        }
        if(rentSearch.getOrderDirection()==null){
            rentSearch.setOrderDirection("desc");
        }

        ServiceMultiResult<HouseDto>result=houseService.query(rentSearch);

        return ResponseVo.success(result.getResult());
    }

    @GetMapping("rent/house/show")
    public ResponseVo show(@RequestHeader(name = "Authorization") String authorization,@RequestParam("id")Long houseId){
        if(houseId==null||houseId<=0){
            throw new ParamsNotValidException();
        }
        ServiceResult<HouseDto> result=houseService.findCompleteOne(houseId);
        User user=userUtil.getUserById(result.getResult().getAdminId());
        if(!result.isSuccess()||user==null){
            return ResponseVo.error();
        }
        Map<SupportAddress.Level, SupportAddressDto> cityAndRegion =
                addressService.findCityAndRegion(result.getResult().getCityEnName(), result.getResult().getRegionEnName());
        SupportAddressDto city=cityAndRegion.get(SupportAddress.Level.CITY);
        SupportAddressDto region=cityAndRegion.get(SupportAddress.Level.REGION);
        UserVo userVo=UserVo.builder()
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .build();
        HouseVo houseVo=HouseVo.builder()
                .userVo(userVo)
                .houseDto(result.getResult())
                .city(city)
                .region(region)
                .build();

        return ResponseVo.success(houseVo);
    }

}
