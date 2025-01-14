package lol.magmaclient.mixins.render;

import lol.magmaclient.Magma;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.entity.passive.EntityVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ RenderVillager.class })
public class MixinRenderVillager
{
    @Inject(method = { "preRenderCallback(Lnet/minecraft/entity/passive/EntityVillager;F)V" }, at = { @At("HEAD") }, cancellable = true)
    private <T extends EntityVillager> void onPreRenderCallback(final T entitylivingbaseIn, final float partialTickTime, final CallbackInfo ci) {
        if (Magma.giants != null && Magma.giants.isToggled() && Magma.giants.mobs.isEnabled()) {
            GlStateManager.scale(Magma.giants.scale.getValue(), Magma.giants.scale.getValue(), Magma.giants.scale.getValue());
        }
    }
}
