package com.ihouse.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserVo {
    String username;

    String mobile;

    String name;

    String email;
}
