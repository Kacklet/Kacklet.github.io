package lol.magmaclient.ui.hud.impl;

import lol.magmaclient.Magma;
import lol.magmaclient.utils.font.Fonts;
import lol.magmaclient.utils.render.RenderUtils;
import lol.magmaclient.utils.render.shader.BlurUtils;
import lol.magmaclient.modules.misc.BuildGuesser;
import lol.magmaclient.ui.hud.DraggableComponent;
import lol.magmaclient.ui.hud.HudVec;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.Color;
import java.util.ArrayList;

public class GuesserHud extends DraggableComponent {
    public static final GuesserHud guesserHud;

    public GuesserHud() {
        this.setPosition(Magma.buildGuesser.x.getValue(), Magma.buildGuesser.y.getValue()); // Imposta la posizione del componente
    }

    static {
        guesserHud = new GuesserHud();
    }

    public HudVec drawScreen(ArrayList<String> matchingWords) {
        super.drawScreen();

        final String selected = Magma.buildGuesser.blurStrength.getSelected();

        int blur = 0;
        switch (selected) {
            case "Low": {
                blur = 7;
                break;
            }
            case "High": {
                blur = 15;
                break;
            }
        }

        double x;
        double y;
        final int cornerRadius = 5;

        switch (BuildGuesser.defaultPosition.getSelected()) {
            default:
                x = 2; // Top left, x = 2
                y = (double) new ScaledResolution(Magma.mc).getScaledHeight() / 2 - (double) (5 * 10) / 2; // Posizione Y al centro dello schermo, tenendo conto del numero di righe

                this.setPosition(x,y);
                break;
            case "Custom":
                x = this.getX();
                y = this.getY();
                break;
        }

        if(matchingWords != null) {
            final int maxWords = Math.min((int) Magma.buildGuesser.displayedGuesses.getValue(), matchingWords.size()); // Prendi al massimo 10 parole

            final String title = "(Build Guesser) Guesses (" + matchingWords.size() + "):";

            double maxLineWidth = 0;
            maxLineWidth = Math.max(maxLineWidth, Fonts.getPrimary().getStringWidth(title));
            for (int i = 0; i < maxWords; i++) {
                String word = matchingWords.get(i);
                double lineWidth = Fonts.getPrimary().getStringWidth(word);
                maxLineWidth = Math.max(maxLineWidth, lineWidth);
            }
            final double width = maxLineWidth + 20;

            final int lineHeight = 12;
            final int numLines = Math.min(matchingWords.size(), (int) Magma.buildGuesser.displayedGuesses.getValue());
            final int height = lineHeight * (numLines+1) + 10;

            this.setSize(width, height);

            BlurUtils.renderBlurredBackground(blur, (float) new ScaledResolution(Magma.mc).getScaledWidth(), (float) new ScaledResolution(Magma.mc).getScaledHeight(), (float) x, (float) y, (float) width, (float) height);
            drawBorderedRoundedRect((float) x, (float) y, (float) width, (float) height, cornerRadius, 2.5f);

            final double textY = y + 7.5;

            Fonts.getPrimary().drawSmoothCenteredStringWithShadow(title, x + width / 2, textY, Magma.clickGui.getColor().getRGB());
            for (int i = 0; i < maxWords; i++) {
                String word = matchingWords.get(i);
                Fonts.getPrimary().drawSmoothCenteredStringWithShadow(word, x + width / 2, textY + 10 + i * lineHeight, Color.white.getRGB());
            }
        } else {
            final String title = "(Build Guesser) Guesses (0):";

            double maxLineWidth = Fonts.getPrimary().getStringWidth(title);
            final double width = maxLineWidth + 22;
            final int height = 15 * 2 + 10;

            this.setSize(width, height);

            BlurUtils.renderBlurredBackground(blur, (float) new ScaledResolution(Magma.mc).getScaledWidth(), (float) new ScaledResolution(Magma.mc).getScaledHeight(), (float) x, (float) y, (float) width, (float) height);
            drawBorderedRoundedRect((float) x, (float) y, (float) width, (float) height, cornerRadius, 2.5f);

            final double textY = y + 7.5;

            Fonts.getPrimary().drawSmoothCenteredStringWithShadow(title, x + width / 2, textY, Magma.clickGui.getColor().getRGB());
            Fonts.getPrimary().drawSmoothCenteredStringWithShadow("No guesses", x + width / 2, textY + 15, Color.white.getRGB());
        }

        Magma.buildGuesser.x.set(this.x);
        Magma.buildGuesser.y.set(this.y);

        return new HudVec(x + width, y + height);
    }

    private void drawBorderedRoundedRect(final float x, final float y, final float width, final float height, final float radius, final float linewidth) {
        RenderUtils.drawRoundedRect(x, y, x + width, y + height, radius, new Color(21, 21, 21, 50).getRGB());
        if (this.isHovered() && Magma.mc.currentScreen instanceof GuiChat) {
            RenderUtils.drawOutlinedRoundedRect(x, y, width, height, radius, linewidth, Color.white.getRGB());
        }
        else {
            RenderUtils.drawGradientOutlinedRoundedRect(x, y, width, height, radius, linewidth, Magma.themeManager.getSecondaryColor().getRGB(), Magma.themeManager.getSecondaryColor(3).getRGB(), Magma.themeManager.getSecondaryColor(6).getRGB(), Magma.themeManager.getSecondaryColor(9).getRGB());
        }
    }

}
