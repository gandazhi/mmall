package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping(value = "add_cart.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> addCart(HttpServletRequest request, HttpServletResponse response, HttpSession session, Integer productId, Integer count) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.addCart(user.getId(), productId, count);
    }

    @RequestMapping(value = "get_cart.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> getCart(HttpSession session) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.getCart(user.getId());
    }

    @RequestMapping(value = "delete_cart.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> deleteCart(HttpSession session, String productIds) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProductIds(user.getId(), productIds);
    }

    @RequestMapping(value = "checkedAll.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> checkedAll(HttpSession session) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.checkedOrUnCheckedProduct(user.getId(), null, Const.CartChecked.CHECK); // productId为null则是全选
    }

    @RequestMapping(value = "unCheckedAll.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> uncheckedAll(HttpSession session) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.checkedOrUnCheckedProduct(user.getId(), null, Const.CartChecked.UN_CHECK); // productId为null则是全选
    }

    @RequestMapping(value = "checkedProduct.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> checkedProduct(HttpSession session, Integer productId) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.checkedOrUnCheckedProduct(user.getId(), productId, Const.CartChecked.CHECK);
    }

    @RequestMapping(value = "uncheckedProduct.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> uncheckedProduct(HttpSession session, Integer productId) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.checkedOrUnCheckedProduct(user.getId(), productId, Const.CartChecked.UN_CHECK);
    }

    @RequestMapping(value = "getCartProductCount.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<Integer> getCartProductCount(HttpSession session){
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null){
            return ServiceResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
