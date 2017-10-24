package com.mmall.service;

import com.mmall.common.ServiceResponse;

public interface IRecommendService {

    ServiceResponse productRecommend(Integer userId, Integer categoryId, Integer num);
}
