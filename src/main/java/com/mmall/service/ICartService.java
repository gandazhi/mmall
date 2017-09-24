package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.vo.CartVo;

public interface ICartService {

    ServiceResponse<CartVo> addCart(Integer userId, Integer productId, Integer count);
}
