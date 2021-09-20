package com.joker17.mysql2excel.utils;

import java.util.Locale;

public class StringUtils {

    private StringUtils() {
    }

    public static String convertString(Object object) {
        return object == null ? null : String.valueOf(object);
    }

    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static String trimToEmpty(String text) {
        return text == null ? "" : text.trim();
    }

    public static String trimToNull(String text) {
        if (text == null) {
            return null;
        }
        String result = text.trim();
        return result.isEmpty() ? null : result;
    }

    public static String toUpperCase(String text) {
        return text == null ? null : text.toUpperCase(Locale.ROOT);
    }

    public static String toLowerCase(String text) {
        return text == null ? null : text.toLowerCase(Locale.ROOT);
    }

    public static boolean equals(CharSequence left, CharSequence right) {
        if (left == right) {
            return true;
        } else {
            if (left != null && right != null) {
                return left.toString().equals(right.toString());
            } else {
                return false;
            }
        }
    }

    public static String removeStart(String text, String remove) {
        if (!isEmpty(text) && !isEmpty(remove)) {
            return text.startsWith(remove) ? text.substring(remove.length()) : text;
        } else {
            return text;
        }
    }

}
