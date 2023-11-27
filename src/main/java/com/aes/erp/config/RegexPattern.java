package com.aes.erp.config;

public class RegexPattern {
    public final static String MOBILE_NUMBER_PATTERN = "^(?:(\\+88)?01[13456798][0-9]{8}|)$";
    public final static String PHONE_NUMBER_PATTERN = "^(?:(\\+88)?0[0-9]{7,10}|)$";
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9.]*[@][a-zA-Z]+\\.(com|net|org)";
    public static final String BLOOD_GROUP_PATTERN = "^(A|B|AB|O)[+-]$";
    public static final String ALPHABET_ONLY = "^[a-zA-z]+$";
    public static final String DATE_PATTERN = "^\\d{2}-\\d{2}-\\d{4}";
    public static final String ALPHABET_WITH_SPACE = "^[a-zA-Z][a-zA-Z ]*$";
    public static final String ALPHABET_WITH_SPACE_BRACES = "^[a-zA-Z][a-zA-Z ()\\.]*$";
    public static final String YEAR_PATTERN = "[0-9]{4}";
    public static final String ONLY_DIGIT_PATTERN = "^[0-9]+$";
    public static final String ALPHANUMERIC_WITH_SPACE = "^[a-zA-Z0-9 ]*$";
}
