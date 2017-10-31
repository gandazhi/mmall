package com.mmall.pojo;

public class SeckillWithBLOBs extends Seckill {
    private String subImages;

    private String detail;


    public SeckillWithBLOBs() {
        super();
    }

    public String getSubImages() {
        return subImages;
    }

    public void setSubImages(String subImages) {
        this.subImages = subImages == null ? null : subImages.trim();
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }
}