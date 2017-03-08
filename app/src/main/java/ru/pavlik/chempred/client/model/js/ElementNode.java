package ru.pavlik.chempred.client.model.js;

import com.github.gwtd3.api.layout.Force;

public class ElementNode extends Force.Node<ElementNode> {

    protected ElementNode() {
    }

    public static native ElementNode create() /*-{
        return {}
    }-*/;

    public static native ElementNode create(String atom, int size) /*-{
        return {
            atom: atom,
            size: size
        }
    }-*/;

    public static native ElementNode create(double x, double y) /*-{
        return {
            x: x,
            y: y
        }
    }-*/;

    public static native ElementNode create(double x, double y, ElementNode element) /*-{
        return {
            x: x,
            y: y,
            atom: element.atom,
            size: element.size,
            valence: element.valence
        }
    }-*/;

    public static native ElementNode create(String atom, int size, double x, double y) /*-{
        return {
            atom: atom,
            size: size,
            x: x,
            y: y
        }
    }-*/;

    public final native void setAtom(String atom) /*-{
        this.atom = atom;
    }-*/;

    public final native void setSize(int size) /*-{
        this.size = size;
    }-*/;

    public final native void setValence(int valence) /*-{
        this.valence = valence;
    }-*/;

    public final native String setLocation(double x, double y) /*-{
        this.x = x;
        this.y = y;
    }-*/;

    public final native String getAtom() /*-{
        return this.atom;
    }-*/;

    public final native int getSize() /*-{
        return this.size;
    }-*/;

    public final native int getValence() /*-{
        return this.valence;
    }-*/;
}
