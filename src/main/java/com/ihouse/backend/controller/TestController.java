package com.ihouse.backend.controller;

import com.ihouse.backend.exception.TestException;
import com.ihouse.backend.vo.ResponseVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class TestController {

    @GetMapping("/orders")
    public ResponseVo create(@Valid @RequestParam String test){
           throw new TestException();
    }
}
