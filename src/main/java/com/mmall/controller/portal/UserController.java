package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.service.impl.UserServiceImpl;
import com.mmall.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.security.provider.MD5;

import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @param session  登录成功后，将登录信息存入session中
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody //自动将返回值序列化成json
    public ServiceResponse<User> login(String username, String password, HttpSession session) {
        //service--->mybatis--->dao
        ServiceResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 用户退出登录
     * 删除session中的Const.CURRENT_USER信息
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySuccess();
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 验证用户名或邮箱是否已经被使用了
     *
     * @param str  用户名或邮箱的值
     * @param type 指定传入的值是用户名还是邮箱
     * @return
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取登录的用户信息
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> getUserInfo(HttpSession session) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user != null) {
            return ServiceResponse.createBySuccess(user);
        }
        return ServiceResponse.createByErrorMessage("用户未登录，不能获取用户信息");
    }

    /**
     * 获取用户忘记密码重置密码问题
     *
     * @param username 用户名
     * @return
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    /**
     * 验证用户提交的问题答案是否与设置的密码一致
     *
     * @param username 用户名
     * @param question 设置的找回密码问题
     * @param answer   设置的找回密码问题的答案
     * @return
     */
    @RequestMapping(value = "for_get_checkAnswer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 没有登录的情况下，使用验证用户设置的找回密码问题来重置密码
     *
     * @param username    用户名
     * @param newPassword 新密码
     * @param forgetToken 验证找回密码问题通过后的token
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {
        return iUserService.forgetRestPassword(username, newPassword, forgetToken);
    }

    /**
     * 已经登录的情况下，验证旧密码，修改新密码
     *
     * @param session     通过session获取当前登录的用户信息
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> resetPassword(HttpSession session, String oldPassword, String newPassword) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(oldPassword, newPassword, user);
    }

    /**
     * 用户在登录的情况下，更新用户信息
     *
     * @param session 通过session获取当前登录的用户信息
     * @param user    更新后的user
     * @return
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> updateInformation(HttpSession session, User user) {
        User currentUser = ((User) session.getAttribute(Const.CURRENT_USER));
        if (currentUser == null) {
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServiceResponse response = iUserService.updateInformation(user);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 获取当前登录的用户信息
     *
     * @param session 通过session获取当前登录的用户信息
     * @return
     */
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> getInformation(HttpSession session) {
        User currentUser = ((User) session.getAttribute(Const.CURRENT_USER));
        if (currentUser == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iUserService.getInformation(currentUser.getId());
    }

    /**
     *
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "userViewHistory.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse userViewHistory(HttpSession session, Integer productId){
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iUserService.userViewHistory(user.getId(), productId);
    }
}
