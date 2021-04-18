package com.ihouse.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class HouseSubscribeDto implements Serializable {

    private static final long serialVersionUID = 8918735582286008182L;

    private Long id;

    private Long houseId;

    private Long userId;

    private Long adminId;

    // 预约状态 1-加入待看清单 2-已预约看房时间 3-看房完成
    private int status;

    private Date createTime;

    private Date lastUpdateTime;

    private Date orderTime;

    private String telephone;

    private String desc;

    @Override
    public String toString() {
        return "HouseSubscribeDto{" +
                "id=" + id +
                ", houseId=" + houseId +
                ", userId=" + userId +
                ", adminId=" + adminId +
                ", status=" + status +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", orderTime=" + orderTime +
                ", telephone='" + telephone + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
