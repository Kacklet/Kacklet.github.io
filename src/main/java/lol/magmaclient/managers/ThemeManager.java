package lol.magmaclient.managers;

import lol.magmaclient.Magma;
import lol.magmaclient.themes.Theme;
import lol.magmaclient.themes.impl.Gradient;
import lol.magmaclient.themes.impl.Katoz;
import lol.magmaclient.themes.impl.Rainbow;

import java.awt.*;
import java.util.ArrayList;

public class ThemeManager {
    public ArrayList<Theme> themes = new ArrayList<>();
    public Theme activeTheme;

    public void setTheme(Theme theme) {
        this.activeTheme = theme;
    }

    public void setTheme(String theme)
    {
        for (Theme theme1 : themes)
        {
            if (theme1.name.equals(theme))
                activeTheme = theme1;
        }
    }

    public boolean is(String name)
    {
        return name.equals(activeTheme.name);
    }

    public ThemeManager()
    {
        themes.add(new Theme("Vape", new Color(50, 50, 50), new Color(120, 55, 150)));
        themes.add(new Theme("Mint", new Color(5, 135, 65), new Color(158, 227, 191)));
        themes.add(new Theme("Devil", new Color(210, 39, 48), new Color(79, 13, 26)));
        themes.add(new Gradient());
        themes.add(new Rainbow());
        themes.add(activeTheme = new Katoz());

        String nameSelected;
        try {
            nameSelected = Magma.clickGui.colorMode.getSelected();
        } catch (Exception e) {
            nameSelected = "Vape";
        }

        for (Theme theme : themes)
        {
            if (theme.name.equals(nameSelected))
                activeTheme = theme;
        }
    }

    public Theme getTheme() {
        return this.activeTheme;
    }

    public Color getPrimaryColor() {
        return this.activeTheme.getPrimary();
    }

    public Color getSecondaryColor() {
        return this.activeTheme.getSecondary();
    }
    public Color getSecondaryColor(int index) {
        return this.activeTheme.getSecondary(index);
    }
}
