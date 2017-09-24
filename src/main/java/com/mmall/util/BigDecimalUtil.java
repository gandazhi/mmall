package com.mmall.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil {

    private BigDecimalUtil(){

    }

    public static BigDecimal add(double d1, double d2){
        BigDecimal v1 = new BigDecimal(Double.toString(d1));
        BigDecimal v2 = new BigDecimal(Double.toString(d2));
        return v1.add(v2);
    }

    public static BigDecimal sub(double d1, double d2){
        BigDecimal v1 = new BigDecimal(Double.toString(d1));
        BigDecimal v2 = new BigDecimal(Double.toString(d2));
        return v1.subtract(v2);
    }

    public static BigDecimal mul(double d1, double d2){
        BigDecimal v1 = new BigDecimal(Double.toString(d1));
        BigDecimal v2 = new BigDecimal(Double.toString(d2));
        return v1.multiply(v2);
    }

    public static BigDecimal div(double d1, double d2){
        BigDecimal v1 = new BigDecimal(Double.toString(d1));
        BigDecimal v2 = new BigDecimal(Double.toString(d2));
        return v1.divide(v2, 2, RoundingMode.HALF_UP);//除不尽的时候，保留2位小数，四舍五入
    }

}
