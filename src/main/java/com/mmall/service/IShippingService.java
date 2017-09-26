package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Shipping;

public interface IShippingService {

    ServiceResponse add(Integer userId, Shipping shipping);

    ServiceResponse<String> delete(Integer userId, Integer shippingId);

    ServiceResponse<String> update(Integer userId, Shipping shipping);

    ServiceResponse<Shipping> getShipping(Integer userId, Integer shippingId);

    ServiceResponse<PageInfo> getAllShipping(Integer userId, Integer pageNum, Integer pageSize);
}
