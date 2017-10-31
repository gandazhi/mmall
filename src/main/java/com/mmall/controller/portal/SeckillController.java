package com.mmall.controller.portal;

import com.mmall.common.ServiceResponse;
import com.mmall.service.ISeckillService;
import com.mmall.vo.SeckillVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/seckill/")
public class SeckillController {

    @Autowired
    private ISeckillService iSeckillService;

    /**
     * 秒杀列表
     * @param pageNum 分页页数
     * @param pageSize 分页每页显示的个数
     * @return
     */
    @RequestMapping(value = "getSeckillList.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<List<SeckillVo>> getSeckillList(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return iSeckillService.getSeckillList(pageNum, pageSize);
    }
}
