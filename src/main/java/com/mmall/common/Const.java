package com.mmall.common;


public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";

    //七牛云相关区域的信息
    public interface Zone {
        int EAST_CHINA = 1;//七牛云华东 Zone.zone0()
        int NORTH_CHINA = 2;//七牛云华北 Zone.zone1()
        int SOUTH_CHINA = 3;//七牛云华南 Zone.zone2()
        int NORTH_AMERICA = 4;//七牛云北美 Zone.zoneNa0()
    }

    public interface Role {
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员用户
    }

}
