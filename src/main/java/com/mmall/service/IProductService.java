package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {

    ServiceResponse saveOrUpdateProduct(Product product);

    ServiceResponse<String> setSaleStatus(Integer productId, Integer status);

    ServiceResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServiceResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServiceResponse<PageInfo> productSearch(String productName, Integer productId, int pageNum, int pageSize);

    ServiceResponse<ProductDetailVo> productDetail(Integer productId);

    ServiceResponse<PageInfo> searchProductList(String keywords, int pageNum, int pageSize, String orderBy);
}
