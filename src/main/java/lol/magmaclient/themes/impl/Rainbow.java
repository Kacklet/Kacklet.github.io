package lol.magmaclient.themes.impl;

import lol.magmaclient.Magma;
import lol.magmaclient.themes.Theme;

import java.awt.*;

public class Rainbow extends Theme {
    public Rainbow()
    {
        super("Rainbow");
    }

    @Override
    public Color getSecondary()
    {
        return Color.getHSBColor((float) ((System.currentTimeMillis() * Magma.clickGui.rgbSpeed.getValue()) / 5000.0 % 1.0), 0.8f, 1.0f);
    }

    @Override
    public Color getSecondary(int index)
    {
        return Color.getHSBColor((float) ((index * 100 + System.currentTimeMillis() * Magma.clickGui.rgbSpeed.getValue()) / 5000.0 % 1.0), 0.8f, 1.0f);
    }
}
