package lol.magmaclient.ui.components;

import lol.magmaclient.Magma;
import lol.magmaclient.settings.RunnableSetting;
import lol.magmaclient.ui.ModernClickGui;
import lol.magmaclient.utils.font.Fonts;
import lol.magmaclient.utils.render.RenderUtils;

import java.awt.*;

public class CompRunnableSetting extends Comp {
    public RunnableSetting runnableSetting;
    public CompRunnableSetting(int x, int y, RunnableSetting runnableSetting)
    {
        this.x = x;
        this.y = y;
        this.runnableSetting = runnableSetting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, double scrollY)
    {
        RenderUtils.drawBorderedRoundedRect((float) (ModernClickGui.getX() + x), (float) (ModernClickGui.getY() + y), (float) (ModernClickGui.getWidth() - x - 5), 15, 5, 1, Magma.themeManager.getPrimaryColor().getRGB(), Magma.themeManager.getSecondaryColor().getRGB());

        Fonts.getPrimary().drawCenteredString(runnableSetting.name, (float) (ModernClickGui.getX() + x + (ModernClickGui.getWidth() - x)/2), (float) (ModernClickGui.getY() + y + 3), Color.WHITE.getRGB());
    }
}
