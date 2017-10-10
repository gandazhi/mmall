package com.mmall.common;


import com.google.common.collect.Sets;

import java.util.Set;

public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";

    public interface ProductListOrderBy {
        Set<String> ORDERBY_ASC_DESC = Sets.newHashSet("price-asc", "price-desc", "create_time-asc", "create_time-desc");
    }

    public interface CartChecked {
        int CHECK = 1; //1是购物车中勾选状态
        int UN_CHECK = 0; //0是购物车中未勾选状态
    }

    public interface LIMT {
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
        String LIMIT_NUM_DELETE = "LIMIT_NUM_DELETE";
    }

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

    public enum ProductStatusEnum {
        ON_SALE(1, "在售");

        private int code;
        private String status;

        ProductStatusEnum(int code, String status) {
            this.code = code;
            this.status = status;
        }

        public int getCode() {
            return code;
        }

        public String getStatus() {
            return status;
        }
    }

    public enum OrderStatusEnum {
        CANCELED(0, "已取消"),
        NO_PAY(10, "未支付"),
        PAID(20, "已付款"),
        SHIPPED(40, "已发货"),
        ORDER_SUCCESS(50, "订单完成"),
        ORDER_CLOSE(60, "订单取消");

        OrderStatusEnum(int code, String status) {
            this.code = code;
            this.status = status;
        }

        private int code;
        private String status;

        public int getCode() {
            return code;
        }

        public String getStatus() {
            return status;
        }

        public static OrderStatusEnum codeOf(int code){
            for (OrderStatusEnum orderStatusEnum : values()){
                if (orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public interface AlipayCallback {
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_PAY";
        String TRADE_STATUS_SUCCESS = "TRADE_SUCCESS";

        String RESPONE_SUCCESS = "success";
        String RESPONE_FAILED = "failed";
    }

    public enum PayPlatformEnum {
        ALIPAY(1, "支付宝");

        private int code;
        private String value;

        PayPlatformEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PaymentTypeEnum {
        ONLINE_PAY(1, "在线支付");
        private int code;
        private String value;

        PaymentTypeEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public static PaymentTypeEnum codeOf(int code){
            for (PaymentTypeEnum paymentTypeEnum : values()){
                if (paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

}
