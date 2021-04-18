package com.ihouse.backend.form;

import lombok.Data;

@Data
public class PhotoForm {

    private String path;

    private int width;

    private int height;

    @Override
    public String toString() {
        return "PhotoForm{" +
                "path='" + path + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
