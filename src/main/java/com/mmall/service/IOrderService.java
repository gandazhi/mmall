package com.mmall.service;

import com.mmall.common.ServiceResponse;

public interface IOrderService {

    ServiceResponse pay(Long orderNum, Integer userId, String path);
}
