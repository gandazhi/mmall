package com.mmall.service;

import com.mmall.common.ServiceResponse;

import java.util.Map;

public interface IOrderService {

    ServiceResponse pay(Long orderNum, Integer userId, String path);

    ServiceResponse aliCallBack(Map<String, String> params);

    ServiceResponse queryOrderPayStatus(Integer userId, Long orderNum);

    ServiceResponse createOrder(Integer userId, Integer shippingId);
}
