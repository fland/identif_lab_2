package ua.pp.fland.labs.identif.lab2.gui.utils;

import java.awt.*;

/**
 * @author Maxim Bondarenko
 * @version 1.0 8/31/11
 */

public enum StandardBordersSizes {

    MAIN_BORDER(new Insets(12, 12, 12, 12));

    private final Insets value;

    private StandardBordersSizes(Insets value) {
        this.value = value;
    }

    public Insets getValue() {
        return value;
    }
}
