package com.mmall.util;

import java.lang.reflect.Field;

public class ObjUtil {

    public static boolean checkObjFieldIsNotNull(Object obj){
        try {
            for (Field f : obj.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(obj) != null) {
                    return true;
                }
            }
        }catch (IllegalAccessException e){

        }
        return false;
    }
}
