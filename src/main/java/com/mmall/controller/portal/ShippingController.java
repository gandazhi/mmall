package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    /**
     * 新建收货地址接口
     *
     * @param session  判断用户是否登录
     * @param shipping 用户收货地址Bean类，id，updateTime和updateTime可以不传，其他都要传
     * @return
     */
    @RequestMapping(value = "add.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse add(HttpSession session, Shipping shipping) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.add(user.getId(), shipping);
    }

    /**
     * 删除收货地址
     *
     * @param session    判断用户是否登录
     * @param shippingId 待删除的用户收货地址id
     * @return
     */
    @RequestMapping(value = "delete.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> delete(HttpSession session, Integer shippingId) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.delete(user.getId(), shippingId);
    }

    /**
     * 更新收货地址
     *
     * @param session  判断用户是否登录
     * @param shipping 更新的收货地址Bean类
     * @return
     */
    @RequestMapping(value = "update.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> update(HttpSession session, Shipping shipping) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.update(user.getId(), shipping);
    }

    /**
     * 获取单个收货地址
     *
     * @param session    判断用户是否登录
     * @param shippingId 获取的收货地址Id
     * @return
     */
    @RequestMapping(value = "getShipping.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<Shipping> getShipping(HttpSession session, Integer shippingId) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.getShipping(user.getId(), shippingId);
    }

    /**
     * 获取全部收货地址
     *
     * @param session  判断用户是否登录
     * @param pageNum  分页的当前页数
     * @param pageSize 每页显示的个数
     * @return
     */
    @RequestMapping(value = "getAllShipping.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<PageInfo> getAllShipping(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.getAllShipping(user.getId(), pageNum, pageSize);

    }
}
