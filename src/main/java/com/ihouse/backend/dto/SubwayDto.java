package com.ihouse.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubwayDto {

    private Long id;

    private String name;

    private String cityEnName;

}
