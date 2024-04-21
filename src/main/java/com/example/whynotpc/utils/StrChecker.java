package com.example.whynotpc.utils;

/**
 * Utility class for checking strings.
 */
public class StrChecker {
    /**
     * Checks if a string is null or blank.
     *
     * @param str The string to check
     * @return {@code true} if the string is null or blank, {@code false} otherwise
     */
    public static boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }
}

