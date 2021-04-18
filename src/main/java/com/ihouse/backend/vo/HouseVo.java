package com.ihouse.backend.vo;

import com.ihouse.backend.dto.HouseDto;
import com.ihouse.backend.dto.SupportAddressDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HouseVo {

    HouseDto houseDto;

    UserVo userVo;

    SupportAddressDto city;

    SupportAddressDto region;

}
