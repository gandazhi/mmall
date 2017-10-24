package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
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

    @Override
    public ServiceResponse productRecommend(Integer userId, Integer categoryId, Integer num) {
        if (userId == 0) {
            //userId为0，则是用户没有登录，按照类别下的销量来推荐
            List<Product> productList = productMapper.selectIdByCategoryId(categoryId, num);
            if (CollectionUtils.isEmpty(productList)) {
                return ServiceResponse.createByErrorMessage("没有找到与" + categoryId + "相关的商品");
            }
            List<ProductListVo> productListVoList = Lists.newArrayList();
            //组装productListVo
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
            return ServiceResponse.createBySuccess(productListVoList);
        }else {
            //用户登录，按照用户的浏览记录和搜索关键字进行推荐

        }
        return null;
    }

}
