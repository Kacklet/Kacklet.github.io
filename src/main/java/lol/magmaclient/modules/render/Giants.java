package lol.magmaclient.modules.render;

import lol.magmaclient.Magma;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.BooleanSetting;
import lol.magmaclient.settings.NumberSetting;

public class Giants extends Module
{
    public NumberSetting scale;
    public BooleanSetting mobs;
    public BooleanSetting players;
    public BooleanSetting armorStands;

    public Giants() {
        super("Giants", Category.RENDER);
        this.scale = new NumberSetting("Scale", 2.0, 0.1, 5.0, 0.1);
        this.mobs = new BooleanSetting("Mobs", false);
        this.players = new BooleanSetting("Players", true);
        this.armorStands = new BooleanSetting("ArmorStands", false);
        this.addSettings(this.scale, this.players, this.mobs, this.armorStands);
    }

    @Override
    public void assign()
    {
        Magma.giants = this;
    }
}
