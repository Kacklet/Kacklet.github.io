package lol.magmaclient.modules.player;

import lol.magmaclient.Magma;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.NumberSetting;

public class FastBreak extends Module
{
    public NumberSetting mineSpeed;
    public NumberSetting maxBlocks;

    public FastBreak() {
        super("Fast Break", 0, Category.PLAYER);
        this.mineSpeed = new NumberSetting("Mining speed", 1.4, 1.0, 1.6, 0.1);
        this.maxBlocks = new NumberSetting("Additional blocks", 0.0, 0.0, 4.0, 1.0);
        this.addSettings(this.maxBlocks, this.mineSpeed);
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Magma.fastBreak = this;
    }
}