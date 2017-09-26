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

    /**
     * @param request   读取cookie，用户在未登录的情况下，把购物车信息添加到cookie中   未完成
     * @param response  存cookie，用户未登录的情况下，把购物车信息添加到cookie中    未完成
     * @param session   判断用户是否登录
     * @param productId 添加或者删除的商品id
     * @param count     添加或删除的数量
     * @return
     */
    @RequestMapping(value = "add_cart.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> addCart(HttpServletRequest request, HttpServletResponse response, HttpSession session, Integer productId, Integer count) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.addCart(user.getId(), productId, count);
    }

    /**
     * 获取当前购物车中的信息
     *
     * @param session 判读用户是否登录
     * @return
     */
    @RequestMapping(value = "get_cart.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> getCart(HttpSession session) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.getCart(user.getId());
    }

    /**
     * 删除购物车中的商品
     *
     * @param session    判断用户登录信息
     * @param productIds 删除的商品id
     * @return
     */
    @RequestMapping(value = "delete_cart.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> deleteCart(HttpSession session, String productIds) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProductIds(user.getId(), productIds);
    }

    /**
     * 全选购物车中的所有商品
     *
     * @param session 判断用户登录信息
     * @return
     */
    @RequestMapping(value = "checkedAll.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> checkedAll(HttpSession session) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.checkedOrUnCheckedProduct(user.getId(), null, Const.CartChecked.CHECK); // productId为null则是全选
    }

    /**
     * 全不选购物车中的所有商品
     *
     * @param session 判断用户登录信息
     * @return
     */
    @RequestMapping(value = "unCheckedAll.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> uncheckedAll(HttpSession session) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.checkedOrUnCheckedProduct(user.getId(), null, Const.CartChecked.UN_CHECK); // productId为null则是全选
    }

    /**
     * 选中单个购物车中的商品
     *
     * @param session   判断用户是否登录
     * @param productId 要选中的商品id
     * @return
     */
    @RequestMapping(value = "checkedProduct.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> checkedProduct(HttpSession session, Integer productId) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.checkedOrUnCheckedProduct(user.getId(), productId, Const.CartChecked.CHECK);
    }

    /**
     * 取消选中购物中的单个商品
     *
     * @param session   判断用户登录信息
     * @param productId 需要取消的商品Id
     * @return
     */
    @RequestMapping(value = "uncheckedProduct.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<CartVo> uncheckedProduct(HttpSession session, Integer productId) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.checkedOrUnCheckedProduct(user.getId(), productId, Const.CartChecked.UN_CHECK);
    }

    /**
     * 获取购物车中的数量，未登录的时候显示数量为0
     *
     * @param session 判断用户是否登录
     * @return
     */
    @RequestMapping(value = "getCartProductCount.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<Integer> getCartProductCount(HttpSession session) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
