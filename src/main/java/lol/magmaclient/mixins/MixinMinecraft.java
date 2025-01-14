package lol.magmaclient.mixins;

import lol.magmaclient.Magma;
import lol.magmaclient.mixins.player.PlayerSPAccessor;
import lol.magmaclient.modules.combat.KillAura;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow private int rightClickDelayTimer;
    @Shadow private Entity renderViewEntity;

    @Inject(method = "startGame", at = @At("TAIL"), cancellable = false)
    public void startGame(CallbackInfo ci)
    {
        Magma.mc = Minecraft.getMinecraft();
    }

    @Inject(method = { "runTick" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V") })
    public void keyPresses(final CallbackInfo ci) {
        final int k = (Keyboard.getEventKey() == 0) ? (Keyboard.getEventCharacter() + '\u0100') : Keyboard.getEventKey();
        final char aChar = Keyboard.getEventCharacter();
        if (Keyboard.getEventKeyState()) {
            if (Magma.mc.currentScreen == null) {
                Magma.handleKey(k);
            }
        }
    }

    @Inject(method = { "rightClickMouse" }, at = { @At("RETURN") }, cancellable = true)
    public void onRightClickPost(final CallbackInfo callbackInfo) {
        if (Magma.fastPlace != null && Magma.fastPlace.isToggled()) {
            this.rightClickDelayTimer = (int) Magma.fastPlace.placeDelay.getValue();
        }
    }

    @Inject(method = { "sendClickBlockToController" }, at = { @At("RETURN") })
    public void sendClickBlock(final CallbackInfo callbackInfo) {
        final boolean click = Magma.mc.currentScreen == null && Magma.mc.gameSettings.keyBindAttack.isKeyDown() && Magma.mc.inGameHasFocus;
        if (Magma.fastBreak != null && Magma.fastBreak.isToggled() && click && Magma.mc.objectMouseOver != null && Magma.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            for (int i = 0; i < Magma.fastBreak.maxBlocks.getValue(); ++i) {
                final BlockPos prevBlockPos = Magma.mc.objectMouseOver.getBlockPos();
                Magma.mc.objectMouseOver = this.renderViewEntity.rayTrace((double) Magma.mc.playerController.getBlockReachDistance(), 1.0f);
                final BlockPos blockpos = Magma.mc.objectMouseOver.getBlockPos();
                if (Magma.mc.objectMouseOver == null || blockpos == null || Magma.mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || blockpos == prevBlockPos || Magma.mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() == Material.air) {
                    break;
                }
                Magma.mc.thePlayer.swingItem();
                Magma.mc.playerController.clickBlock(blockpos, Magma.mc.objectMouseOver.sideHit);
            }
        }
    }

    @Inject(method = { "getRenderViewEntity" }, at = { @At("HEAD") })
    public void getRenderViewEntity(final CallbackInfoReturnable<Entity> cir) {
        if (!Magma.killAura.isToggled() || this.renderViewEntity == null || this.renderViewEntity != Magma.mc.thePlayer) {
            return;
        }
        if (KillAura.target != null) {
            ((EntityLivingBase)this.renderViewEntity).rotationYawHead = ((PlayerSPAccessor)this.renderViewEntity).getLastReportedYaw();
            ((EntityLivingBase)this.renderViewEntity).renderYawOffset = ((PlayerSPAccessor)this.renderViewEntity).getLastReportedYaw();
        }
    }
}
