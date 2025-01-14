package lol.magmaclient.modules.player;

import lol.magmaclient.Magma;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.BooleanSetting;
import lol.magmaclient.settings.NumberSetting;

public class Velocity extends Module
{
    public NumberSetting vModifier;
    public NumberSetting hModifier;
    public BooleanSetting skyblockKB;

    public Velocity() {
        super("Velocity", 0, Category.PLAYER);
        this.vModifier = new NumberSetting("Vertical", 0.0, -2.0, 2.0, 0.05);
        this.hModifier = new NumberSetting("Horizontal", 0.0, -2.0, 2.0, 0.05);
        this.skyblockKB = new BooleanSetting("Skyblock KnockBack", true);
        this.addSettings(this.hModifier, this.vModifier, this.skyblockKB);
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Magma.velocity = this;
    }
}
