package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.vo.OrderVo;

import java.util.List;
import java.util.Map;

public interface IOrderService {

    ServiceResponse pay(Long orderNum, Integer userId, String path);

    ServiceResponse aliCallBack(Map<String, String> params);

    ServiceResponse queryOrderPayStatus(Integer userId, Long orderNum);

    ServiceResponse createOrder(Integer userId, Integer shippingId);

    ServiceResponse<String> cancelOrder(Integer userId, Long orderNum);

    ServiceResponse getCartOrderCartProduct(Integer userId);

    ServiceResponse<OrderVo> orderDetail(Integer userId, Long orderNum);

    ServiceResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    ServiceResponse<PageInfo> manageOrderList(int pageNum, int pageSize);

    ServiceResponse<OrderVo> manageOrderDetail(Long orderNum);

    ServiceResponse<PageInfo> searchOrder(String keywords, int pageNum, int pageSize);

    ServiceResponse<String> manageSendGoods(Long orderNum);
}
