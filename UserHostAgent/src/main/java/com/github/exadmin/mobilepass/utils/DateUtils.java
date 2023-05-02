package com.github.exadmin.mobilepass.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static String getCurrentTime() {
        return dtf.format(LocalDateTime.now());
    }

}
