package lol.magmaclient.modules.player;

import lol.magmaclient.Magma;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.NumberSetting;

public class FastPlace extends Module {
    public NumberSetting placeDelay;
    public FastPlace()
    {
        super("Fast Place", Category.PLAYER);
        this.addSettings(this.placeDelay = new NumberSetting("Place delay", 2.0, 0.0, 4.0, 1.0));
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Magma.fastPlace = this;
    }
}