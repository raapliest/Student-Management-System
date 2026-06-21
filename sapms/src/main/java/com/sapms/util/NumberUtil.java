package com.sapms.util;

public class NumberUtil {
    public static String formatNumber(int number, int length) {
        String value = String.valueOf(number);
        while (value.length() < length) value = "0" + value;
        return value;
    }

    public static String nextSequence(String currentSeq, int length) {
        return formatNumber(Integer.parseInt(currentSeq) + 1, length);
    }
}