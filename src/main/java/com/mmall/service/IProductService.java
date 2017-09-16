package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;

public interface IProductService {

    ServiceResponse saveOrUpdateProduct(Product product);

    ServiceResponse<String> setSaleStatus(Integer productId, Integer status);
}
