package ru.pavlik.chempred.client.utils;

public class Utils {

    public static native void console(Object text)
/*-{
    console.log(text);
}-*/;
}
