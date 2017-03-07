package ru.pavlik.chempred.client.model.js;

import com.google.gwt.core.client.JavaScriptObject;

public class PointerEvent extends JavaScriptObject {

    protected PointerEvent() {
    }

    public final native void setDefaultPrevented(boolean defaultPrevented) /*-{
        this.defaultPrevented = defaultPrevented;
    }-*/;

    public final native boolean getDefaultPrevented()/*-{
        return this.defaultPrevented;
    }-*/;
}
