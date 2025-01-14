package lol.magmaclient.themes.impl;

import lol.magmaclient.Magma;
import lol.magmaclient.themes.Theme;

import java.awt.*;

public class Gradient extends Theme {
    public Gradient()
    {
        super("Gradient");
    }

    @Override
    public Color getSecondary()
    {
        final float location = (float)((Math.cos((System.currentTimeMillis() * Magma.clickGui.shiftSpeed.getValue()) / 1000.0) + 1.0) * 0.5);
        if (!Magma.clickGui.hsb.isEnabled()) {
            return new Color((int)(Magma.clickGui.redShift1.getValue() + (Magma.clickGui.redShift2.getValue() - Magma.clickGui.redShift1.getValue()) * location), (int)(Magma.clickGui.greenShift1.getValue() + (Magma.clickGui.greenShift2.getValue() - Magma.clickGui.greenShift1.getValue()) * location), (int)(Magma.clickGui.blueShift1.getValue() + (Magma.clickGui.blueShift2.getValue() - Magma.clickGui.blueShift1.getValue()) * location));
        }
        final float[] c1 = Color.RGBtoHSB((int) Magma.clickGui.redShift1.getValue(), (int) Magma.clickGui.greenShift1.getValue(), (int) Magma.clickGui.blueShift1.getValue(), null);
        final float[] c2 = Color.RGBtoHSB((int) Magma.clickGui.redShift2.getValue(), (int) Magma.clickGui.greenShift2.getValue(), (int) Magma.clickGui.blueShift2.getValue(), null);
        return Color.getHSBColor(c1[0] + (c2[0] - c1[0]) * location, c1[1] + (c2[1] - c1[1]) * location, c1[2] + (c2[2] - c1[2]) * location);
    }

    @Override
    public Color getSecondary(int index)
    {
        final float location = (float)((Math.cos((index * 100 + System.currentTimeMillis() * Magma.clickGui.shiftSpeed.getValue()) / 1000.0) + 1.0) * 0.5);
        if (!Magma.clickGui.hsb.isEnabled()) {
            return new Color((int)(Magma.clickGui.redShift1.getValue() + (Magma.clickGui.redShift2.getValue() - Magma.clickGui.redShift1.getValue()) * location), (int)(Magma.clickGui.greenShift1.getValue() + (Magma.clickGui.greenShift2.getValue() - Magma.clickGui.greenShift1.getValue()) * location), (int)(Magma.clickGui.blueShift1.getValue() + (Magma.clickGui.blueShift2.getValue() - Magma.clickGui.blueShift1.getValue()) * location));
        }
        final float[] c1 = Color.RGBtoHSB((int) Magma.clickGui.redShift1.getValue(), (int) Magma.clickGui.greenShift1.getValue(), (int) Magma.clickGui.blueShift1.getValue(), null);
        final float[] c2 = Color.RGBtoHSB((int) Magma.clickGui.redShift2.getValue(), (int) Magma.clickGui.greenShift2.getValue(), (int) Magma.clickGui.blueShift2.getValue(), null);
        return Color.getHSBColor(c1[0] + (c2[0] - c1[0]) * location, c1[1] + (c2[1] - c1[1]) * location, c1[2] + (c2[2] - c1[2]) * location);
    }
}
