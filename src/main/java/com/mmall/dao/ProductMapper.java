package com.mmall.dao;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.Product;
import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    int selectProductById(Integer productId);

    List<Product> selectList();

    List<Product> selectByNameAndProductId(@Param(value = "productName") String productName,@Param(value = "productId") Integer productId);

    List<Product> selectByKeywords(String keywords);

    List<Product> selectUserList();

    int updateSalesVolumeByProductId(@Param(value = "productId")Integer productId, @Param(value = "quantity")Integer quantity);

    List<Product> selectIdByCategoryId(@Param(value = "categoryId") Integer categoryId, @Param(value = "num") Integer num);
}