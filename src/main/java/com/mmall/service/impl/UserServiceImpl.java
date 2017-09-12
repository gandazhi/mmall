package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.mmall.util.RegularExpressionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public ServiceResponse<User> login(String username, String password) {

        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServiceResponse.createByErrorMessage("用户名不存在");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServiceResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess("登录成功", user);
    }

    public ServiceResponse<String> register(User user) {
        ServiceResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return ServiceResponse.createByErrorMessage("用户名已经存在");
        }
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return ServiceResponse.createByErrorMessage("邮箱已经存在");
        }
        //验证邮箱账号是否合法
        if (!RegularExpressionUtil.isEmail(user.getEmail())) {
            return ServiceResponse.createByErrorMessage("邮箱账号不合法");
        }
        validResponse = this.checkValid(user.getPhone(), Const.PHONE);
        if (!validResponse.isSuccess()) {
            return ServiceResponse.createByErrorMessage("手机号已经存在");
        }
        //验证手机账号是否合法
        if (!RegularExpressionUtil.isPhone(user.getPhone())) {
            return ServiceResponse.createByErrorMessage("手机号不合法");
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServiceResponse.createByErrorMessage("注册失败");
        }
        return ServiceResponse.createBySuccessMesage("注册成功");
    }

    @Override
    public ServiceResponse<String> checkValid(String str, String type) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(str)) { //判断str是否为空
            //开始校验用户名或邮箱是否存在User
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServiceResponse.createByErrorMessage("用户名已经存在");
                }
            } else if (Const.EMAIL.equals(type)) {
                if (!RegularExpressionUtil.isEmail(str)) {
                    return ServiceResponse.createByErrorMessage("邮箱不合法");
                }
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServiceResponse.createByErrorMessage("邮箱已经存在");
                }
            } else if (Const.PHONE.equals(type)) {
                if (!RegularExpressionUtil.isPhone(str)) {
                    return ServiceResponse.createByErrorMessage("手机号不合法");
                }
                int resultCount = userMapper.checkPhone(str);
                if (resultCount > 0) {
                    return ServiceResponse.createByErrorMessage("这个手机号已经被注册了");
                }
            } else {
                return ServiceResponse.createByErrorMessage("type错误");
            }
        } else {
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        return ServiceResponse.createBySuccessMesage("校验成功");
    }

    @Override
    public ServiceResponse selectQuestion(String username) {
        //判断username是不是为空
        if (StringUtils.isBlank(username)){
            return ServiceResponse.createByErrorMessage("username不能为空");
        }
        //先校验传来的username是否存在
        ServiceResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(question)) {
            return ServiceResponse.createBySuccess(question);
        }
        return ServiceResponse.createByErrorMessage("找回密码的问题是空的");
    }

    @Override
    public ServiceResponse<String> checkAnswer(String username, String question, String answer) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(question) || StringUtils.isBlank(answer)){
            return ServiceResponse.createByErrorMessage("参数不能为空");
        }
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            String forgetToken = UUID.randomUUID().toString(); //生成一个不可重复的字符串
            TokenCache.setKey("token_" + username, forgetToken);
            return ServiceResponse.createBySuccess(forgetToken);
        }
        return ServiceResponse.createByErrorMessage("找回密码的答案验证失败");
    }

    @Override
    public ServiceResponse<String> forgetRestPassword(String username, String newPassword, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServiceResponse.createByErrorMessage("token需要重新获取");
        }
        ServiceResponse checkUsername = this.checkValid(username, Const.USERNAME);
        if (checkUsername.isSuccess()) {
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServiceResponse.createByErrorMessage("token无效");
        }
        if (StringUtils.equals(token, forgetToken)) {
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int resultCount = userMapper.updatePassword(username, md5Password);
            if (resultCount > 0) {
                return ServiceResponse.createBySuccessMesage("修改密码成功");
            }
        } else {
            return ServiceResponse.createByErrorMessage("token错误,重新获取重置密码token");
        }
        return ServiceResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServiceResponse<String> resetPassword(String oldPassword, String newPassword, User user) {
        int resultCount = userMapper.checkPassword(user.getId(), MD5Util.MD5EncodeUtf8(oldPassword));
        if (resultCount == 0) {
            return ServiceResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount > 0) {
            return ServiceResponse.createBySuccessMesage("密码修改成功");
        }
        return ServiceResponse.createByErrorMessage("密码修改失败");
    }

    @Override
    public ServiceResponse<User> updateInformation(User user) {
        //先判断email是不是被其他用户占用了
        int resultCount = userMapper.checkEmailByUserId(user.getId(), user.getEmail());
        if (resultCount > 0) {
            return ServiceResponse.createByErrorMessage("该email已经被其他人注册了，请换个邮箱");
        }
        User updateUser = new User();
        updateUser.setUsername(user.getUsername());
        updateUser.setEmail(user.getEmail());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (resultCount > 0) {
            return ServiceResponse.createBySuccess("更新信息成功", updateUser);
        }
        return ServiceResponse.createByErrorMessage("更新信息失败");
    }

    @Override
    public ServiceResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess(user);
    }

    @Override
    public ServiceResponse checkAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServiceResponse.createBySuccess();
        }
        return ServiceResponse.createByError();
    }
}
