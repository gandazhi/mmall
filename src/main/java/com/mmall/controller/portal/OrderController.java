package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.security.krb5.Config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    /**
     * 支付宝支付接口
     *
     * @param session  通过session判断用户是否登录
     * @param orderNum 待支付的订单号
     * @param request  通过HttpServletRequest取得生成支付二维码的位置
     * @return
     */
    @RequestMapping(value = "pay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse pay(HttpSession session, Long orderNum, HttpServletRequest request) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNum, user.getId(), path);
    }

    /**
     * 支付宝支付的回调接口
     *
     * @param request 通过HttpServletRequest取出相应信息做请求验证
     * @return
     */
    @RequestMapping(value = "callback.do")
    @ResponseBody
    protected Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for (Iterator iterator = requestParams.keySet().iterator(); iterator.hasNext(); ) {
            String name = ((String) iterator.next());
            String[] values = ((String[]) requestParams.get(name));
            String valueStr = "";

            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        logger.info("支付宝回调,sign{},trade_status{},参数{}", params.get("sign"), params.get("trade_status"), params.toString());

        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckedV2) {
                logger.error("非法请求，验证失败");
                return ServiceResponse.createByErrorMessage("非法请求，验证失败");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调异常", e);
            e.printStackTrace();
        }
        logger.info("回调之前.....");
        ServiceResponse response = iOrderService.aliCallBack(params);
        logger.info("回调之后.....");
        if (response.isSuccess()) {
            return Const.AlipayCallback.RESPONE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONE_FAILED;
    }

    /**
     * 查询订单支付状态
     *
     * @param session  通过session判断用户是否登录
     * @param orderNum 查询支付状态的订单号
     * @return
     */
    @RequestMapping(value = "queryOrderPayStatus.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNum) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServiceResponse response = iOrderService.queryOrderPayStatus(user.getId(), orderNum);
        if (response.isSuccess()) {
            return ServiceResponse.createBySuccess(true);
        }
        return ServiceResponse.createBySuccess(false);
    }

    /**
     * 创建订单(对购物车中选中的商品下一个订单，对于没有添加商品到购物车中时，直接购买，前端调用的时候先调用添加该商品到购物车中的接口再调用这个接口，后续优化)
     *
     * @param session    通过session判断用户是否登录
     * @param shippingId 登录用户的收货地址id
     * @return
     */
    @RequestMapping(value = "create.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse create(HttpSession session, Integer shippingId) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(), shippingId);
    }

    /**
     * 取消订单
     *
     * @param session  通过session判断用户是否登录
     * @param orderNum 待取消的订单号
     * @return
     */
    @RequestMapping(value = "cancel.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse cancel(HttpSession session, Long orderNum) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancelOrder(user.getId(), orderNum);
    }

    @RequestMapping(value = "getOrderCartProduct.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse getOrderCartProduct(HttpSession session) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getCartOrderCartProduct(user.getId());
    }

    /**
     * 前台个人中心里获取某个订单的详情
     *
     * @param session  判断用户是否登录
     * @param orderNum 待查看订单详情的订单号
     * @return
     */
    @RequestMapping(value = "orderDetail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse orderDetail(HttpSession session, Long orderNum) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.orderDetail(user.getId(), orderNum);
    }

    /**
     * 用户前台获取所有的订单列表
     *
     * @param session  判断用户是否登录
     * @param pageNum  分页，显示的当前页数
     * @param pageSize 分页，每页显示的数量
     * @return
     */
    @RequestMapping(value = "getOrderList.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse getOrderList(HttpSession session,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }

}
