package com.looksmaxxing.mellow.Utility;

public class UserUtils {

    private static boolean isAdmin = false;

    public static boolean isAdmin() {
        return isAdmin;
    }
    public static void setAdmin(boolean isAdmin) {
        UserUtils.isAdmin = isAdmin;
    }}
