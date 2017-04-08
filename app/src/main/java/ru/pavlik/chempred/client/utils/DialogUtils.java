package ru.pavlik.chempred.client.utils;

import org.gwtbootstrap3.extras.bootbox.client.Bootbox;
import org.gwtbootstrap3.extras.bootbox.client.options.DialogOptions;

public class DialogUtils {

    private DialogUtils() {
    }

    public static void showErrorDialog(String title, String message) {
        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(title);
        Bootbox.dialog(options);
    }

}
