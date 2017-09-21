package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
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
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    //后台获取商品详情
    @Override
    public ServiceResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        //先判断传来的productId是否为空
        if (productId == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServiceResponse.createByErrorMessage("没有找到传入productId的产品");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServiceResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
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
        if (category == null) {
            productDetailVo.setParentCategoryId(0);
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix", "http://img.happymmall.com/"));
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    @Override
    public ServiceResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        /**
         *  1.startPage--start
         *  2.填充自己的sql
         *  3.pageHelper-收尾
         */
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServiceResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setSubTitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setImageHost(PropertiesUtil.getProperties("qiniu.url", "http://owioow1ef.bkt.clouddn.com/"));

        return productListVo;
    }

    @Override
    public ServiceResponse<PageInfo> productSearch(String productName, Integer productId, int pageNum, int pageSize) {
        //判断productName和productId是否都为空
        if (StringUtils.isBlank(productName) && productId == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //pageHelper第一步,startPage
        PageHelper.startPage(pageNum, pageSize);

        //pageHelper第二步，开始填充自己的sql
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        //判断通过关键字productList是否是空的
        if (productList.isEmpty()) {
            return ServiceResponse.createByErrorMessage("没有搜到与关键字相关的商品");
        }

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        //pageHelper第三步，收尾
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);

        return ServiceResponse.createBySuccess(pageResult);
    }


    //前台用户-获取商品详情
    @Override
    public ServiceResponse<ProductDetailVo> productDetail(Integer productId) {
        if (productId == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServiceResponse.createByErrorMessage("没有找到传入productId的产品");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServiceResponse.createByErrorMessage("传入productId的商品不是在售状态");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServiceResponse.createBySuccess(productDetailVo);
    }

    //用户通过关键字搜索
    public ServiceResponse<PageInfo> searchProductList(String keywords, int pageNum, int pageSize, String orderBy) {
        if (StringUtils.isBlank(keywords)) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //拼接搜索关键字
        keywords = "%" + keywords + "%";
        PageHelper.startPage(pageNum, pageSize);
        //排序处理
        if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
            String[] orderByArray = orderBy.split("_");
            PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
        }else {
            return ServiceResponse.createByErrorMessage("orderBy参数错误");
        }

        List<Product> productList = productMapper.selectByKeywords(keywords);
        if (productList.isEmpty()){
            return ServiceResponse.createByErrorMessage("没有搜索到与关键字相关的商品");
        }

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);

        return ServiceResponse.createBySuccess(pageResult);
    }

    //用户获取商品列表
    @Override
    public ServiceResponse<PageInfo> getUserProductList(int pageNum, int pageSize, String orderBy) {
        PageHelper.startPage(pageNum, pageSize);

        if (StringUtils.isNotBlank(orderBy)){
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }

        List<Product> productList = productMapper.selectUserList();

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServiceResponse.createBySuccess(pageResult);
    }
}
