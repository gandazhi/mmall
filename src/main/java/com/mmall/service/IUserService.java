package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;

public interface IUserService {

    ServiceResponse<User> login(String username, String password);

    ServiceResponse<String> register(User user);

    ServiceResponse<String> checkValid(String str, String type);

    ServiceResponse selectQuestion(String username);

    ServiceResponse<String> checkAnswer(String username, String question, String password);

    ServiceResponse<String> forgetRestPassword(String username, String newPassword, String forgetToken);

    ServiceResponse<String> resetPassword(String oldPassword, String newPassword, User user);

    ServiceResponse<User> updateInformation(User user);

    ServiceResponse<User> getInformation(Integer userId);

    ServiceResponse checkAdminRole(User user);

    ServiceResponse userViewHistory(Integer userId, Integer productId);

}
