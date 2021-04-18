package com.ihouse.backend.service;

import lombok.Data;

import java.util.List;

//通用分页
@Data
public class ServiceMultiResult<T> {

    private int total;

    private List<T> result;

    public ServiceMultiResult(int total, List<T> result) {
        this.total = total;
        this.result = result;
    }

    public int getResultSize(){
        if(this.result==null){
            return 0;
        }
        return this.result.size();
    }
}
