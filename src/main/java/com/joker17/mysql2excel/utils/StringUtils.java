package com.joker17.mysql2excel.utils;

public class StringUtils {

    public static String convertString(Object object) {
        return object == null ? null : String.valueOf(object);
    }

    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }


}
