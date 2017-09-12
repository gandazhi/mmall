package com.mmall.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpressionUtil {

    //验证邮箱是否符合email格式
    public static Boolean isEmail(String email){
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(check);
        Matcher matcher = pattern.matcher(email);
        boolean isMatched = matcher.matches();
        return isMatched;
    }

    //验证手机号是否合法
    public static Boolean isPhone(String phone){
        String check = "^((13[0-9])|(15[^4,\\D])|(18[0,4-9]))\\d{8}$";
        Pattern pattern = Pattern.compile(check);
        Matcher matcher = pattern.matcher(phone);
        boolean isMatched = matcher.matches();
        return isMatched;
    }
}
