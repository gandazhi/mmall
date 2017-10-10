package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    /**
     * 后台获取订单列表
     *
     * @param session  判断用户是否登录，是否是管理员
     * @param pageNum  分页当前显示的页数
     * @param pageSize 分页每页显示的个数
     * @return
     */
    @RequestMapping(value = "orderList.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse mangeOrderList(HttpSession session,
                                          @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //登录的用户是管理员
            return iOrderService.manageOrderList(pageNum, pageSize);
        } else {
            return ServiceResponse.createByErrorMessage("当前用户没有权限调用此接口");
        }
    }

    /**
     * 后台查看某个订单详情
     *
     * @param session  判断用户是否登录，是否是管理员
     * @param orderNum 待查看订单详情的订单号
     * @return
     */
    @RequestMapping(value = "orderDetail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse manageOrderDetail(HttpSession session, Long orderNum) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //是管理员用户登录
            return iOrderService.manageOrderDetail(orderNum);
        } else {
            return ServiceResponse.createByErrorMessage("当前用户没有权限调用此接口");
        }
    }

    /**
     * 通过订单号为关键字搜索订单
     *
     * @param session  判断用户是否登录，是否是管理员
     * @param keywords 搜索的关键字
     * @param pageNum  分页当前显示的页数
     * @param pageSize 分页每页显示的个数
     * @return
     */
    @RequestMapping(value = "searchOrder.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse searchOrder(HttpSession session, String keywords,
                                       @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            return iOrderService.searchOrder(keywords, pageNum, pageSize);
        } else {
            return ServiceResponse.createByErrorMessage("当前用户没有权限调用此接口");
        }
    }

    /**
     * 更改订单状态为已发货
     *
     * @param session  判断用户是否登录，是否是管理员
     * @param orderNum 待更改状态的订单号
     * @return
     */
    @RequestMapping(value = "sendGoods.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse sendGoods(HttpSession session, Long orderNum) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            return iOrderService.manageSendGoods(orderNum);
        } else {
            return ServiceResponse.createByErrorMessage("当前用户没有权限调用此接口");
        }
    }
}
