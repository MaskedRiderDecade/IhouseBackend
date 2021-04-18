package com.ihouse.backend.dto;

public class QiNiuResponseRetDto {

    public String key;

    public String hash;

    public String bucket;

    public int width;

    public int height;

    @Override
    public String toString() {
        return "QiNiuPutRet{" +
                "key='" + key + '\'' +
                ", hash='" + hash + '\'' +
                ", bucket='" + bucket + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
