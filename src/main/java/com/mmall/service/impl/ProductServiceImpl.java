package com.mmall.service.impl;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServiceResponse saveOrUpdateProduct(Product product) {
        /**
         * 先判断前端传回来的product是否是空
         * 根据前端传回来的id做判断，如果id存在，则是更新已有的产品，如果id不存在则是创建了一个新的产品
         */
        if (product != null) {
            //将上传的第一张图片作为该产品的主图片
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }
            if (product.getId() == null) {
                //创建一个新的产品
                int resultCount = productMapper.insert(product);
                if (resultCount > 0) {
                    return ServiceResponse.createBySuccessMesage("新建产品成功");
                }
                return ServiceResponse.createByErrorMessage("新建产品失败");
            } else if (product.getId() != null) {
                //更新一个产品
                int resultCount = productMapper.updateByPrimaryKeySelective(product);
                if (resultCount > 0) {
                    return ServiceResponse.createBySuccessMesage("更新产品成功");
                }
                return ServiceResponse.createByErrorMessage("更新产品失败");
            }
        }
        return ServiceResponse.createByErrorMessage("参数错误");
    }

    @Override
    public ServiceResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //判断是否有传入的产品id
        int resultCount = productMapper.selectProductById(productId);
        if (resultCount > 0) {
            //有传入的产品id
            Product product = new Product();
            product.setId(productId);
            product.setStatus(status);

            resultCount = productMapper.updateByPrimaryKeySelective(product);
            if (resultCount > 0) {
                return ServiceResponse.createBySuccessMesage("更新产品状态成功");
            }
            return ServiceResponse.createByErrorMessage("更新产品状态失败");
        } else {
            //没有传入的产品id
            return ServiceResponse.createByErrorMessage("没有找到传入productId的产品");
        }
    }

    @Override
    public ServiceResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        //先判断传来的productId是否为空
        if (productId == null){
            return ServiceResponse.createByErrorMessage("参数不能为空");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServiceResponse.createByErrorMessage("没有找到传入productId的产品");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServiceResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(product.getName());
        productDetailVo.setSubTitle(product.getSubtitle());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImage(product.getSubImages());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setPrice(product.getPrice());

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null){
            productDetailVo.setParentCategoryId(0);
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix", "http://img.happymmall.com/"));
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }
}
