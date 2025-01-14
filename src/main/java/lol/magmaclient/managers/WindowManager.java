package lol.magmaclient.managers;

import lol.magmaclient.modules.Module;
import lol.magmaclient.ui.windows.HomeWindow;
import lol.magmaclient.ui.windows.ModuleWindow;
import lol.magmaclient.ui.windows.SettingsWindow;
import lol.magmaclient.ui.windows.Window;

import java.util.ArrayList;
import java.util.List;

public class WindowManager {
    public List<Window> windows = new ArrayList<Window>();

    public WindowManager() {
        this.windows.add(new HomeWindow());
        for (Module.Category category : Module.Category.values()) {
            if (category == Module.Category.SETTINGS) continue;
            this.windows.add(new ModuleWindow(category));
        }

        this.windows.add(new SettingsWindow());
    }

    public Window getDefaultWindow() {
        return this.windows.get(0);
    }
}
