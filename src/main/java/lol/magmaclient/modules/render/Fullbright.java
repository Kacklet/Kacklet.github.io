package lol.magmaclient.modules.render;

import lol.magmaclient.Magma;
import lol.magmaclient.modules.Module;

public class Fullbright extends Module {

    private float originalGamma;

    public Fullbright() {
        super("Fullbright", Category.RENDER);
    }

    @Override
    public void assign()
    {
        Magma.fullbright = this;
    }

    @Override
    public void onEnable() {
        originalGamma = Magma.mc.gameSettings.gammaSetting;
        Magma.mc.gameSettings.gammaSetting = 100;
    }

    @Override
    public void onDisable() {
        Magma.mc.gameSettings.gammaSetting = originalGamma > 10 ? 1 : originalGamma;
        if(Magma.clientSettings.debug.isEnabled()) {
            Magma.sendMessageWithPrefix("" + Magma.mc.gameSettings.gammaSetting);
        }
    }
}