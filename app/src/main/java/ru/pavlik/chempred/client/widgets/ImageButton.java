package ru.pavlik.chempred.client.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import org.gwtbootstrap3.client.ui.Button;

public class ImageButton extends Button {

    private String text;
    private ImageResource imageResource;

    public ImageButton() {
        super();
    }

    public void setResource(ImageResource imageResource) {
        this.imageResource = imageResource;
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        Scheduler.get().scheduleDeferred(this::setupImage);
    }

    private void setupImage() {
        Image img = new Image(imageResource.getSafeUri());
        Element imgElement = img.getElement();
        String definedStyles = img.getElement().getAttribute("style");
        imgElement.setAttribute("style", definedStyles + "; vertical-align:top;");
        imgElement.getStyle().setWidth(100, Style.Unit.PCT);
        imgElement.getStyle().setHeight(100, Style.Unit.PCT);
        DOM.insertBefore(getElement(), img.getElement(), DOM.getFirstChild(getElement()));
    }
}
