package ru.pavlik.chempred.client.utils;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

public class EffectUtils {

    private EffectUtils() {
    }

    public static void fadeIn(final Widget widget, final int duration) {
        fade(widget, 0, 1, duration);
    }

    public static void fadeOut(final Widget widget, final int duration) {
        fade(widget, 1, 0, duration);
    }

    private static void fade(final Widget element, final float startOpacity, final float endOpacity, int totalTimeMillis) {
        final int numberOfSteps = 30;
        int stepLengthMillis = totalTimeMillis / numberOfSteps;

        final float deltaOpacity = (endOpacity - startOpacity) / numberOfSteps;

        Timer timer = new Timer() {
            private int stepCount = 0;

            @Override
            public void run() {
                double opacity = startOpacity + (stepCount * deltaOpacity);
                element.getElement().getStyle().setOpacity(opacity);

                stepCount++;
                if (stepCount == numberOfSteps) {
                    element.getElement().getStyle().setOpacity(endOpacity);
                    element.setVisible(endOpacity == 1);
                    this.cancel();
                }
            }
        };

        timer.scheduleRepeating(stepLengthMillis);
    }
}