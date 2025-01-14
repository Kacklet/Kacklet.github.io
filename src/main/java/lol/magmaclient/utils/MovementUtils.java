package lol.magmaclient.utils;

import lol.magmaclient.Magma;
import lol.magmaclient.events.MoveEvent;
import lol.magmaclient.modules.combat.KillAura;
import lol.magmaclient.utils.rotation.RotationUtils;
import net.minecraft.entity.Entity;

public class MovementUtils
{
    public static MilliTimer strafeTimer;

    public static float getSpeed() {
        return (float)Math.sqrt(Magma.mc.thePlayer.motionX * Magma.mc.thePlayer.motionX + Magma.mc.thePlayer.motionZ * Magma.mc.thePlayer.motionZ);
    }

    public static float getSpeed(final double x, final double z) {
        return (float)Math.sqrt(x * x + z * z);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static boolean isMoving() {
        return Magma.mc.thePlayer.moveForward != 0.0f || Magma.mc.thePlayer.moveStrafing != 0.0f;
    }

    public static boolean hasMotion() {
        return Magma.mc.thePlayer.motionX != 0.0 && Magma.mc.thePlayer.motionZ != 0.0 && Magma.mc.thePlayer.motionY != 0.0;
    }

    public static boolean isOnGround(final double height) {
        return !Magma.mc.theWorld.getCollidingBoundingBoxes((Entity) Magma.mc.thePlayer, Magma.mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
    }

    public static void strafe(final double speed) {
        if (!isMoving()) {
            return;
        }
        final double yaw = getDirection();
        Magma.mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        Magma.mc.thePlayer.motionZ = Math.cos(yaw) * speed;
        MovementUtils.strafeTimer.reset();
    }

    public static void bhop(double s) {
        double forward = Magma.mc.thePlayer.movementInput.moveForward;
        double strafe = Magma.mc.thePlayer.movementInput.moveStrafe;
        float yaw = Magma.mc.thePlayer.rotationYaw;

        if ((forward == 0.0D) && (strafe == 0.0D)) {
            Magma.mc.thePlayer.motionX = 0.0D;
            Magma.mc.thePlayer.motionZ = 0.0D;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D)
                    yaw += (float) (forward > 0.0D ? -45 : 45);
                else if (strafe < 0.0D)
                    yaw += (float) (forward > 0.0D ? 45 : -45);

                strafe = 0.0D;
                if (forward > 0.0D)
                    forward = 1.0D;
                else if (forward < 0.0D)
                    forward = -1.0D;
            }

            double rad = Math.toRadians(yaw + 90.0F);
            double sin = Math.sin(rad);
            double cos = Math.cos(rad);
            Magma.mc.thePlayer.motionX = (forward * s * cos) + (strafe * s * sin);
            Magma.mc.thePlayer.motionZ = (forward * s * sin) - (strafe * s * cos);
        }

    }

    public static void strafe(final float speed, final float yaw) {
        if (!isMoving() || !MovementUtils.strafeTimer.hasTimePassed(150L)) {
            return;
        }
        Magma.mc.thePlayer.motionX = -Math.sin(Math.toRadians(yaw)) * speed;
        Magma.mc.thePlayer.motionZ = Math.cos(Math.toRadians(yaw)) * speed;
        MovementUtils.strafeTimer.reset();
    }

    public static void forward(final double length) {
        final double yaw = Math.toRadians(Magma.mc.thePlayer.rotationYaw);
        Magma.mc.thePlayer.setPosition(Magma.mc.thePlayer.posX + -Math.sin(yaw) * length, Magma.mc.thePlayer.posY, Magma.mc.thePlayer.posZ + Math.cos(yaw) * length);
    }

    public static double getDirection() {
        return Math.toRadians(getYaw());
    }

    public static void setMotion(final MoveEvent em, final double speed) {
        double forward = Magma.mc.thePlayer.movementInput.moveForward;
        double strafe = Magma.mc.thePlayer.movementInput.moveStrafe;
        float yaw = ((KillAura.target != null && Magma.killAura.movementFix.isEnabled())) ? RotationUtils.getRotations(KillAura.target).getYaw() : Magma.mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            Magma.mc.thePlayer.motionX = 0.0;
            Magma.mc.thePlayer.motionZ = 0.0;
            if (em != null) {
                em.setX(0.0);
                em.setZ(0.0);
            }
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            Magma.mc.thePlayer.motionX = forward * speed * cos + strafe * speed * sin;
            Magma.mc.thePlayer.motionZ = forward * speed * sin - strafe * speed * cos;
            if (em != null) {
                em.setX(Magma.mc.thePlayer.motionX);
                em.setZ(Magma.mc.thePlayer.motionZ);
            }
        }
    }

    public static float getYaw() {
        float yaw = Magma.mc.thePlayer.rotationYaw;
        if (Magma.mc.thePlayer.moveForward < 0.0f) {
            yaw += 180.0f;
        }
        float forward = 1.0f;
        if (Magma.mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        }
        else if (Magma.mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (Magma.mc.thePlayer.moveStrafing > 0.0f) {
            yaw -= 90.0f * forward;
        }
        if (Magma.mc.thePlayer.moveStrafing < 0.0f) {
            yaw += 90.0f * forward;
        }
        return yaw;
    }

    static {
        MovementUtils.strafeTimer = new MilliTimer();
    }
}
