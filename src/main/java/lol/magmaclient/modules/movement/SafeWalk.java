package lol.magmaclient.modules.movement;

import lol.magmaclient.Magma;
import lol.magmaclient.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.GameSettings;

public class SafeWalk extends Module
{
    public SafeWalk() {
        super("Safe Walk", 0, Category.MOVEMENT);
    }

    @Override
    public void assign()
    {
        Magma.safeWalk = this;
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(Magma.mc.gameSettings.keyBindSneak.getKeyCode(), GameSettings.isKeyDown(Magma.mc.gameSettings.keyBindSneak));
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (Magma.mc.thePlayer == null || Magma.mc.theWorld == null || !this.isToggled() || Magma.mc.currentScreen != null) {
            return;
        }
        final BlockPos BP = new BlockPos(Magma.mc.thePlayer.posX, Magma.mc.thePlayer.posY - 0.5, Magma.mc.thePlayer.posZ);
        if (Magma.mc.theWorld.getBlockState(BP).getBlock() == Blocks.air && Magma.mc.theWorld.getBlockState(BP.down()).getBlock() == Blocks.air && Magma.mc.thePlayer.onGround && Magma.mc.thePlayer.movementInput.moveForward < 0.1f) {
            KeyBinding.setKeyBindState(Magma.mc.gameSettings.keyBindSneak.getKeyCode(), true);
        }
        else {
            KeyBinding.setKeyBindState(Magma.mc.gameSettings.keyBindSneak.getKeyCode(), GameSettings.isKeyDown(Magma.mc.gameSettings.keyBindSneak));
        }
    }
}
