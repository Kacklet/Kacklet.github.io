package lol.magmaclient.mixins;

import lol.magmaclient.Magma;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(FMLHandshakeMessage.ModList.class)
public class MixinModList {

    @Shadow(remap = false) private Map<String, String> modTags;

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At("RETURN"), cancellable = true, remap = false)
    public void constructor(List<ModContainer> containerList, CallbackInfo ci)
    {
        if(Magma.modHider == null || Magma.modHider.isToggled()) {
            modTags.clear();

            for (ModContainer mod : containerList)
            {
                if (mod.getModId().equals(Magma.MOD_ID) && !Magma.mc.isIntegratedServerRunning())
                    continue;

                modTags.put(mod.getModId(), mod.getVersion());
            }
        }
    }
}
