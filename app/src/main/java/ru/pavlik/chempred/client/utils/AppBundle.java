package ru.pavlik.chempred.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface AppBundle extends ClientBundle {

    AppBundle INSTANCE = GWT.create(AppBundle.class);

    @Source("images/periodic_table.png")
    ImageResource periodicTable();

    @Source("images/single_line.png")
    ImageResource singleLine();

    @Source("images/double_line.png")
    ImageResource doubleLine();

    @Source("images/triple_line.png")
    ImageResource tripleLine();

    @Source("images/single_up_line.png")
    ImageResource singleUpLineLine();

    @Source("images/single_down_line.png")
    ImageResource singleDownLineLine();

    @Source("images/clear.png")
    ImageResource clear();

    @Source("css/element.css")
    @CssResource.NotStrict
    CssResource elementsCss();

}
