package com.romy.platform.common.utils;

import org.apache.commons.lang3.StringUtils;


public class StringUtil {

    public static boolean isEmpty(String value) {
        return StringUtils.isEmpty(value);
    }

    public static boolean isNotEmpty(String value) {
        return StringUtils.isNotEmpty(value);
    }

    public static boolean equals(String fixValue, String compValue) {
        if (isEmpty(fixValue)) return false;

        return fixValue.equals(compValue);
    }

}
