package ru.pavlik.chempred.client.utils;

public class TextUtils {

    private TextUtils() {
    }

    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

}
