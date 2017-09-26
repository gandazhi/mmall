package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServiceResponse add(Integer userId, Shipping shipping) {
        if (shipping == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        shipping.setUserId(userId);
        int resultCount = shippingMapper.insert(shipping);
        if (resultCount > 0) {
            Map resultMap = Maps.newHashMap();
            resultMap.put("shipping", shipping.getId());
            return ServiceResponse.createBySuccess("新建收货地址成功", resultMap);
        }
        return ServiceResponse.createByErrorMessage("新建收货地址失败");
    }


    @Override
    public ServiceResponse<String> delete(Integer userId, Integer shippingId) {
        if (shippingId == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int resultCount = shippingMapper.deleteByUserIdShippingId(userId, shippingId);
        if (resultCount > 0){
            return ServiceResponse.createBySuccessMesage("删除收货地址成功");
        }
        return ServiceResponse.createByErrorMessage("删除收货地址失败");
    }

    @Override
    public ServiceResponse<String> update(Integer userId, Shipping shipping) {
        if (shipping == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        shipping.setUserId(userId); //防止传来的shipping是模拟的数据
        int resultCount = shippingMapper.updateByUserIdShippingId(shipping);
        if (resultCount > 0){
            return ServiceResponse.createBySuccessMesage("更新收货地址成功");
        }
        return ServiceResponse.createByErrorMessage("更新收货地址失败");
    }

    @Override
    public ServiceResponse<Shipping> getShipping(Integer userId, Integer shippingId) {
        if (shippingId == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping shipping = shippingMapper.selectByUserIdShippingId(userId, shippingId);
        if (shipping != null){
            return ServiceResponse.createBySuccess("获取收货地址成功", shipping);
        }
        return ServiceResponse.createByErrorMessage("查询收货失败");
    }

    @Override
    public ServiceResponse<PageInfo> getAllShipping(Integer userId, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null || pageNum == 0 || pageSize == 0){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(shippingList)){
            return ServiceResponse.createBySuccessMesage("你还没有收货地址，请添加");
        }
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServiceResponse.createBySuccess(pageInfo);
    }
}
