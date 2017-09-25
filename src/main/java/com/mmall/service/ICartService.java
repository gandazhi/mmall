package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.vo.CartVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ICartService {

    ServiceResponse<CartVo> addCart(Integer userId, Integer productId, Integer count);

    ServiceResponse<CartVo> getCart(Integer userId);

    ServiceResponse<CartVo> addCart(HttpServletRequest request, HttpServletResponse response, Integer productId, Integer count);
}
