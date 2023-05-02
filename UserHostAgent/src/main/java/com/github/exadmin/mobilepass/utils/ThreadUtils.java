package com.github.exadmin.mobilepass.utils;

public class ThreadUtils {
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            // ok to suppress exception here
        }
    }
}
