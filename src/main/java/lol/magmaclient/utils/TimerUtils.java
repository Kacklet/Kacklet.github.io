package lol.magmaclient.utils;

import lol.magmaclient.Magma;
import lol.magmaclient.mixins.MinecraftAccessor;
import net.minecraft.util.Timer;

public class TimerUtils
{
    public static void setSpeed(final float speed) {
        getTimer().timerSpeed = speed;
    }

    public static void resetSpeed() {
        setSpeed(1.0f);
    }

    public static Timer getTimer() {
        return ((MinecraftAccessor) Magma.mc).getTimer();
    }
}
