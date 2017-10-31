package com.mmall.pojo;

public class SuccessKilledKey {
    private Long seckillId;

    private Long userPhone;

    public SuccessKilledKey(Long seckillId, Long userPhone) {
        this.seckillId = seckillId;
        this.userPhone = userPhone;
    }

    public SuccessKilledKey() {
        super();
    }

    public Long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    public Long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(Long userPhone) {
        this.userPhone = userPhone;
    }
}