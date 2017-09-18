package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/product")
public class ProductMangeController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    /**
     * 更新或新建产品
     * 传product.id的是更新产品
     * 传product.id的是新建产品
     *
     * @param session 根据session判断用户是否登录，判断登录用户是否是管理员
     * @param product 传入需要更新的产品
     * @return
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServiceResponse productSave(HttpSession session, Product product) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录，请登录");
        }
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //登录的用户是管理员，继续产品更新或创建操作
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return ServiceResponse.createByErrorMessage("登录用户无权限进行此操作");
        }
    }

    /**
     * 更新产品销售状态
     *
     * @param session   根据session判断用户是否登录，判断用户是否是管理员
     * @param productId 待更新产品状态的产品Id
     * @param status    更新产品后的销售状态
     * @return
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServiceResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录，请登录");
        }
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //登录用户是管理员，继续更新产品销售状态操作
            return iProductService.setSaleStatus(productId, status);
        } else {
            return ServiceResponse.createByErrorMessage("用户用户无权限进行次操作");
        }
    }

    /**
     * 获取产品详情信息
     *
     * @param session   根据session来判断用户是否登录，是否是管理员
     * @param productId 传入要获取详情的产品id
     * @return
     */
    @RequestMapping(value = "get_detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<ProductDetailVo> getDetail(HttpSession session, Integer productId) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录，请登录");
        }
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //登录用户是管理员
            return iProductService.manageProductDetail(productId);
        } else {
            return ServiceResponse.createByErrorMessage("用户没有权限进行此操作");
        }
    }
}
