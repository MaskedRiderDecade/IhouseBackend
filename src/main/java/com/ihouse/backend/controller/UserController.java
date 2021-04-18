package com.ihouse.backend.controller;

import com.ihouse.backend.service.user.UserUtil;
import com.ihouse.backend.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserUtil userUtil;

    @GetMapping("/getUser")
    public ResponseVo getUser(@RequestHeader(name = "Authorization") String authorization){
           return ResponseVo.success(userUtil.getUser(authorization));
    }
}
