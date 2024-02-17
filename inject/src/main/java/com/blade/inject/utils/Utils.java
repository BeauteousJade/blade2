package com.blade.inject.utils;

public class Utils {

    @SuppressWarnings("unchecked")
    public static <T> T checkNoNull(Object t, String fileName, String key, boolean supportNull) {
        if (supportNull || t != null) {
            return (T) t;
        }
        throw new NullPointerException(fileName + " is null, key: " + key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPrimitive(T t, Object defaultValue, String fileName, String key, boolean userDefault) {
        if (t != null) {
            return t;
        }
        if (!userDefault) {
            throw new NullPointerException(fileName + " is null, key: " + key);
        }
        return (T) defaultValue;
    }
}
