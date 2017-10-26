package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.dao.UserHistoryMapper;
import com.mmall.pojo.Product;
import com.mmall.pojo.UserHistory;
import com.mmall.service.IRecommendService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductListVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iRecommendService")
public class RecommendServiceImpl implements IRecommendService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserHistoryMapper userHistoryMapper;


    @Override
    public ServiceResponse productRecommend(Integer userId, Integer categoryId, Integer num) {
        if (num == null){
            return ServiceResponse.createByErrorMessage("num不能为空");
        }
        if (userId == 0) {
            //userId为0，则是用户没有登录，按照类别下的销量来推荐
            return this.salesOrder(categoryId, num);
        } else {
            //用户登录，按照用户的浏览记录和搜索关键字进行推荐
            List<UserHistory> userHistoryList = userHistoryMapper.selectUserViewHistoryByUserIdCategoryId(userId, num, categoryId);
            if (CollectionUtils.isEmpty(userHistoryList)) {
                //该用户在这个分类下没有记录，按分类下的销量来排
                return this.salesOrder(categoryId, num);
            } else {
                //分类下有记录，按分类下的浏览量来排
                List<Product> productList = Lists.newArrayList();
                //从userHistory中获取productId，组装productList
                for (UserHistory userHistory : userHistoryList) {
                    Product product = productMapper.selectByPrimaryKey(userHistory.getProductId());
                    productList.add(product);
                }
                List<ProductListVo> productListVoList = this.assembleProductListVo(productList);
                return ServiceResponse.createBySuccess(productListVoList);
            }
        }
    }

    //按照分类下的销量来排序
    private ServiceResponse salesOrder(Integer categoryId, Integer num) {
        List<Product> productList = productMapper.selectIdByCategoryId(categoryId, num); // order by sales_volume desc
        if (CollectionUtils.isEmpty(productList)) {
            return ServiceResponse.createByErrorMessage("没有找到与" + categoryId + "相关的商品");
        }
        //组装productListVo
        List<ProductListVo> productListVoList = this.assembleProductListVo(productList);
        return ServiceResponse.createBySuccess(productListVoList);
    }

    //组装ProductListVo
    private List<ProductListVo> assembleProductListVo(List<Product> productList) {
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = new ProductListVo();
            productListVo.setId(productItem.getId());
            productListVo.setCategoryId(productItem.getCategoryId());
            productListVo.setName(productItem.getName());
            productListVo.setSubTitle(productItem.getSubtitle());
            productListVo.setMainImage(productItem.getMainImage());
            productListVo.setPrice(productItem.getPrice());
            productListVo.setStatus(productItem.getStatus());
            productListVo.setImageHost(PropertiesUtil.getProperties("qiniu.url", "http://owioow1ef.bkt.clouddn.com/"));
            productListVoList.add(productListVo);
        }
        return productListVoList;

    }

}
