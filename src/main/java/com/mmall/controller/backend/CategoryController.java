package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/mange/category")
public class CategoryController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse categoryAdd(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        //先判断用户是否已经登录
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录，请登录");
        }
        //校验是否是管理员
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //是管理员，继续增加分类操作
            return iCategoryService.addCategory(categoryName, parentId);
        }
        return ServiceResponse.createByErrorMessage("不是管理员，不能增加分类");
    }

    @RequestMapping(value = "update_category_name.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse updateCategoryName(HttpSession session, String categoryName, Integer categoryId){
        //先判断用户登录状态
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录，请登录");
        }
        //校验登录用户是否是管理员
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()){
            //登录用户是管理员，继续更新分类名的操作
            return iCategoryService.updateCategoryName(categoryName, categoryId);
        }
        return ServiceResponse.createByErrorMessage("不是管理员，不能更新分类名");
    }
}
