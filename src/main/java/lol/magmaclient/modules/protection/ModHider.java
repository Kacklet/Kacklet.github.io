package lol.magmaclient.modules.protection;

import lol.magmaclient.Magma;
import lol.magmaclient.modules.Module;

public class ModHider extends Module {
    public ModHider()
    {
        super("Mod Hider", Category.PROTECTIONS);
        setToggled(true);
    }

    @Override
    public void assign()
    {
        Magma.modHider = this;
    }
}
