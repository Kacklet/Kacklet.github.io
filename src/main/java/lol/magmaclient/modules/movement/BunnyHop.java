package lol.magmaclient.modules.movement;

import lol.magmaclient.Magma;
import lol.magmaclient.events.MotionUpdateEvent;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.BooleanSetting;
import lol.magmaclient.settings.NumberSetting;
import lol.magmaclient.utils.MovementUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BunnyHop extends Module {
    public NumberSetting speed;
    public BooleanSetting fastFall;

    public BunnyHop() {
        super("Bunny Hop", Category.MOVEMENT);
        this.speed = new NumberSetting("Speed", 2, 1, 10, 0.2);
        this.fastFall = new BooleanSetting("Fast Fall", false);
        this.addSettings(this.speed, this.fastFall);
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Magma.bunnyHop = this;
    }

    @SubscribeEvent
    public void onMoveInput(MotionUpdateEvent e) {
        if(!this.isToggled()) return;

        if (MovementUtils.isMoving() && !Magma.mc.thePlayer.isInWater()) {
            if (Magma.mc.thePlayer.onGround) {
                Magma.mc.thePlayer.jump();
            }

            if (fastFall.isEnabled()) {
                if (Magma.mc.thePlayer.fallDistance < 2 && Magma.mc.thePlayer.fallDistance > 0) {
                    Magma.mc.thePlayer.motionY *= 1.5;
                }
            }

            Magma.mc.thePlayer.setSprinting(true);
            double spd = 0.01D * speed.getValue();
            double m = (float)(Math.sqrt(Magma.mc.thePlayer.motionX * Magma.mc.thePlayer.motionX + Magma.mc.thePlayer.motionZ * Magma.mc.thePlayer.motionZ) + spd);
            MovementUtils.bhop(m);
        }
    }
}