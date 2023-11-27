package com.aes.erp.config;

public class Constants {
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final String SECRET_KEY = "secret";
    public static final long TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 111;
    public static final String BD_TIMEZONE = "Asia/Dhaka";
    public static final String BD_DATE_FORMAT = "dd-MM-yyyy";
    public static final String BD_DATETIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

    public static final String ROLE_SYS_ADMIN = "SYS_ADMIN";
    public static final String ROLE_LEAD_ENGR = "LEAD_ENGR";
    public static final String ROLE_SOP_ENGR = "SOP_ENGR";
    public static final String ROLE_DB_ADMIN = "DB_ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ENGINEER = "ENGINEER";
    public static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String MYSQL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

}