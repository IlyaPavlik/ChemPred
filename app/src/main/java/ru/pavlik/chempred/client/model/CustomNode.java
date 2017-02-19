package ru.pavlik.chempred.client.model;

import com.github.gwtd3.api.layout.Force;

public class CustomNode extends Force.Node<CustomNode> {

    protected CustomNode() {
    }

    public static native CustomNode create() /*-{
        return {}
    }-*/;

    public static native CustomNode create(String atom, int size) /*-{
        return {
            atom: atom,
            size: size
        }
    }-*/;

    public static native CustomNode create(double x, double y) /*-{
        return {
            x: x,
            y: y
        }
    }-*/;

    public static native CustomNode create(String atom, int size, double x, double y) /*-{
        return {
            atom: atom,
            size: size,
            x: x,
            y: y
        }
    }-*/;

    public final native String getAtom() /*-{
        return this.atom;
    }-*/;

    public final native int getSize() /*-{
        return this.size;
    }-*/;
}
