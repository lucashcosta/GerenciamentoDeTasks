package com.devmasterteam.tasks.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUrlParameters {

    /**
     * Formato esperado
     * yyyy-MM-dd
     */
    public static String formatDate(Date value) {

        if (value == null)
            return "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(value);
    }

    /**
     * Formato esperado - String
     * true / false
     */
    public static String formatBoolean(Boolean value) {
        if (value)
            return "true";
        return "false";
    }

}
