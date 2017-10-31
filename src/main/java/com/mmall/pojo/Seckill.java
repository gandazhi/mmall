package com.mmall.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class Seckill {
    private Long seckillId;

    private String name;

    private String mainImage;

    private String subImage;

    private String detail;

    private BigDecimal price;

    private BigDecimal seckillPrice;

    private Integer status;

    private Integer number;

    private Date createTime;

    private Date startTime;

    private Date endTime;

    public Seckill(Long seckillId, String name, String mainImage, String subImage,String detail,BigDecimal price, BigDecimal seckillPrice, Integer status, Integer number, Date createTime, Date startTime, Date endTime) {
        this.seckillId = seckillId;
        this.name = name;
        this.mainImage = mainImage;
        this.price = price;
        this.seckillPrice = seckillPrice;
        this.status = status;
        this.number = number;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subImage = subImage;
        this.detail = detail;
    }

    public Seckill() {
        super();
    }

    public Long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage == null ? null : mainImage.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(BigDecimal seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getSubImage() {
        return subImage;
    }

    public void setSubImage(String subImage) {
        this.subImage = subImage;
    }
}