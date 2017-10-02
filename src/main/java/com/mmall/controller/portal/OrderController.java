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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckedV2){
                return ServiceResponse.createByErrorMessage("非法请求，验证失败");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调异常", e);
            e.printStackTrace();
        }
        // TODO 验证各种数据

        ServiceResponse response = iOrderService.aliCallBack(requestParams);
        if (response.isSuccess()){
            return Const.AlipayCallback.RESPONE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONE_FAILED;
    }

    @RequestMapping(value = "queryOrderPayStatus.do")
    @ResponseBody
    public ServiceResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNum){
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServiceResponse response = iOrderService.queryOrderPayStatus(user.getId(), orderNum);
        if (response.isSuccess()){
            return ServiceResponse.createBySuccess(true);
        }
        return ServiceResponse.createBySuccess(false);
    }
}
