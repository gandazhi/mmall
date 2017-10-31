package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.SeckillMapper;
import com.mmall.pojo.Seckill;
import com.mmall.service.ISeckillService;
import com.mmall.vo.seckillVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service("iSeckillService")
public class SeckillServiceImpl implements ISeckillService{

    @Autowired
    private SeckillMapper seckillMapper;

    @Override
    public ServiceResponse getSeckillList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Seckill> seckillList = seckillMapper.selectAllSeckill();
        if (CollectionUtils.isEmpty(seckillList)){
            return ServiceResponse.createByErrorMessage("在数据库中没有找到秒杀数据....");
        }
        //组装seckillVoList
        List<seckillVo> seckillVoList = assembleSeckillVo(seckillList);

        PageInfo pageResult = new PageInfo(seckillList);
        pageResult.setList(seckillVoList);
        return ServiceResponse.createBySuccess(pageResult);
    }

    private List<seckillVo> assembleSeckillVo(List<Seckill> seckillList){
        List<seckillVo> seckillVoList = Lists.newArrayList();
        for (Seckill seckillItem : seckillList){
            seckillVo seckill = new seckillVo();
            seckill.setName(seckillItem.getName());
            seckill.setNumber(seckillItem.getNumber());
            seckillVoList.add(seckill);
        }
        return seckillVoList;
    }
}
