package com.ihouse.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class HousePictureDto implements Serializable {

    private static final long serialVersionUID = 8918735582286008182L;

    private Long id;

    @JsonProperty(value = "house_id")
    private Long houseId;

    private String path;

    @JsonProperty(value = "cdn_prefix")
    private String cdnPrefix;

    private int width;

    private int height;

    @Override
    public String toString() {
        return "HousePictureDto{" +
                "id=" + id +
                ", houseId=" + houseId +
                ", path='" + path + '\'' +
                ", cdnPrefix='" + cdnPrefix + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
