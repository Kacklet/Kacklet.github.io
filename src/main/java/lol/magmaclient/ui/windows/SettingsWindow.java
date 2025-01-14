package lol.magmaclient.ui.windows;

import lol.magmaclient.Magma;
import lol.magmaclient.settings.Setting;
import lol.magmaclient.ui.ModernClickGui;
import lol.magmaclient.ui.components.Comp;
import lol.magmaclient.utils.MouseUtils;
import lol.magmaclient.utils.StencilUtils;

import java.util.ArrayList;
import java.util.List;

public class SettingsWindow extends Window {
    public List<Setting> settingList = new ArrayList<>();

    public SettingsWindow() {
        super("Settings");
        for(Setting setting : Magma.clickGui.settings) {
            setting.category = "Gui";
            this.settingList.add(setting);
        }
        this.settingList.addAll(Magma.clientSettings.settings);
    }

    @Override
    public void initGui() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (Comp comp : ModuleWindow.updateComps(this.settingList))
        {
            comp.drawScreen(mouseX, mouseY, (double)partialTicks);
        }

        MouseUtils.Scroll scroll = MouseUtils.scroll();

         if (scroll != null)
        {
            switch (scroll)
            {
                case DOWN: {
                    if ((ModuleWindow.scrollYsettings > (ModernClickGui.getHeight()) - ModuleWindow.settingsHeight))
                        ModuleWindow.scrollYsettings -= 10;
                    break;
                }
                case UP: {
                    ModuleWindow.scrollYsettings += 10;
                    if (ModuleWindow.scrollYsettings >= 0)
                    {
                        ModuleWindow.scrollYsettings = 0;
                    }
                    if (ModuleWindow.settingsHeight < (ModernClickGui.getHeight() - 25))
                        ModuleWindow.scrollYsettings = 0.0;
                }
            }
        }
        ModuleWindow.scrollAnimation.setAnimation(ModuleWindow.scrollY, 16.0);
        ModuleWindow.settingsAnimation.setAnimation(ModuleWindow.scrollYsettings, 16);
        StencilUtils.disableStencilBuffer();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (Comp comp : ModuleWindow.updateComps(this.settingList))
        {
            comp.mouseClicked(mouseX, mouseY, mouseButton);
        }

        Magma.themeManager.setTheme(Magma.clickGui.colorMode.getSelected());
        Magma.configManager.saveConfig();
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (Comp comp : ModuleWindow.updateComps(this.settingList))
        {
            comp.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (ModuleWindow.selectedString != null)
        {
            ModuleWindow.selectedString = null;
        }

        for (Comp comp : ModuleWindow.updateComps(this.settingList))
        {
            comp.keyTyped(typedChar, keyCode);
        }
    }
}
