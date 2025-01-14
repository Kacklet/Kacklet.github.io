package lol.magmaclient.mixins.player;

import lol.magmaclient.Magma;
import lol.magmaclient.events.MotionUpdateEvent;
import lol.magmaclient.events.MoveFlyingEvent;
import lol.magmaclient.events.PlayerUpdateEvent;
import lol.magmaclient.managers.CommandManager;
import lol.magmaclient.modules.combat.KillAura;
import lol.magmaclient.utils.MovementUtils;
import lol.magmaclient.utils.PlayerUtils;
import lol.magmaclient.utils.rotation.RotationUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = { EntityPlayerSP.class }, priority = 1)
public abstract class MixinEntityPlayerSP extends MixinPlayer {
    @Shadow
    @Final
    public NetHandlerPlayClient sendQueue;
    @Shadow
    private boolean serverSprintState;
    @Shadow
    private int positionUpdateTicks;
    @Shadow
    private boolean serverSneakState;
    @Shadow
    private double lastReportedPosX;
    @Shadow
    private double lastReportedPosY;
    @Shadow
    private double lastReportedPosZ;
    @Shadow
    private float lastReportedYaw;
    @Shadow
    private float lastReportedPitch;
    @Shadow
    public MovementInput movementInput;
    @Shadow
    public abstract boolean isSneaking();
    @Shadow
    protected abstract boolean isCurrentViewEntity();

    @Redirect(method = { "onLivingUpdate" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isUsingItem()Z"))
    public boolean isUsingItem(final EntityPlayerSP instance) {
        return !Magma.noSlow.isToggled() && (instance.isUsingItem());
    }

    @Inject(method = { "onLivingUpdate" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;onLivingUpdate()V") }, cancellable = true)
    public void onLivingUpdate(final CallbackInfo ci) {
        if (Magma.noSlow.isToggled() && getHeldItem() != null) {
            final EnumAction action = this.getHeldItem().getItem().getItemUseAction(this.getHeldItem());
            if (action == EnumAction.BLOCK) {
                final MovementInput movementInput = this.movementInput;
                movementInput.moveForward *= (float) Magma.noSlow.swordSlowdown.getValue();
                final MovementInput movementInput2 = this.movementInput;
                movementInput2.moveStrafe *= (float) Magma.noSlow.swordSlowdown.getValue();
            }
            else if (action == EnumAction.BOW) {
                final MovementInput movementInput3 = this.movementInput;
                movementInput3.moveForward *= (float) Magma.noSlow.bowSlowdown.getValue();
                final MovementInput movementInput4 = this.movementInput;
                movementInput4.moveStrafe *= (float) Magma.noSlow.bowSlowdown.getValue();
            }
            else if (action != EnumAction.NONE) {
                final MovementInput movementInput5 = this.movementInput;
                movementInput5.moveForward *= (float) Magma.noSlow.eatingSlowdown.getValue();
                final MovementInput movementInput6 = this.movementInput;
                movementInput6.moveStrafe *= (float) Magma.noSlow.eatingSlowdown.getValue();
            }
        }
        if (Magma.freeCam.isToggled()) {
            this.noClip = true;
        }
    }

    @Inject(method = { "sendChatMessage" }, at = { @At("HEAD") }, cancellable = true)
    public void onSenChatMessage(final String message, final CallbackInfo ci) {
        if (CommandManager.handle(message)) {
            ci.cancel();
        }
    }

    @Inject(method = { "onUpdate" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isRiding()Z") }, cancellable = true)
    private void onUpdate(final CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post((Event)new PlayerUpdateEvent())) {
            ci.cancel();
        }
    }

    @Override
    public void moveFlying(float strafe, float forward, float friction) {
        final MoveFlyingEvent event = new MoveFlyingEvent(forward, strafe, friction, this.rotationYaw);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return;
        }
        strafe = event.getStrafe();
        forward = event.getForward();
        friction = event.getFriction();
        float f = strafe * strafe + forward * forward;
        if (f >= 1.0E-4f) {
            f = MathHelper.sqrt_float(f);
            if (f < 1.0f) {
                f = 1.0f;
            }
            f = friction / f;
            strafe *= f;
            forward *= f;
            final float yaw = event.getYaw();
            final float f2 = MathHelper.sin(yaw * 3.1415927f / 180.0f);
            final float f3 = MathHelper.cos(yaw * 3.1415927f / 180.0f);
            this.motionX += strafe * f3 - forward * f2;
            this.motionZ += forward * f3 + strafe * f2;
        }
    }

    public void jump() {
        this.motionY = this.getJumpUpwardsMotion();
        if (this.isPotionActive(Potion.jump.id)) {
            this.motionY += (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f;
        }
        if (this.isSprinting() && MovementUtils.isMoving()) {
            final float f = ((Magma.killAura.isToggled() && Magma.killAura.target != null && Magma.killAura.movementFix.isEnabled()) ? RotationUtils.getRotations(KillAura.target).getYaw() : this.rotationYaw) * 0.017453292f;
            this.motionX -= MathHelper.sin(f) * 0.2f;
            this.motionZ += MathHelper.cos(f) * 0.2f;
        }
        this.isAirBorne = true;
        ForgeHooks.onLivingJump((EntityLivingBase)(Object)this);
        this.triggerAchievement(StatList.jumpStat);
        if (this.isSprinting()) {
            this.addExhaustion(0.8f);
        }
        else {
            this.addExhaustion(0.2f);
        }
    }

    /**
     * @author not me
     * @reason D:
     */
    @Overwrite
    public void onUpdateWalkingPlayer() {
        final MotionUpdateEvent event = new MotionUpdateEvent.Pre(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround, this.isSprinting(), this.isSneaking());
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return;
        }
        final boolean flag = event.sprinting;
        if (flag != this.serverSprintState) {
            if (flag) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(Magma.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            }
            else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(Magma.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
            this.serverSprintState = flag;
        }
        final boolean flag2 = event.sneaking;
        if (flag2 != this.serverSneakState) {
            if (flag2) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(Magma.mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            }
            else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(Magma.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }
            this.serverSneakState = flag2;
        }
        if (this.isCurrentViewEntity()) {
            final double d0 = event.x - this.lastReportedPosX;
            final double d2 = event.y - this.lastReportedPosY;
            final double d3 = event.z - this.lastReportedPosZ;
            final double d4 = event.yaw - this.lastReportedYaw;
            final double d5 = event.pitch - this.lastReportedPitch;
            boolean flag3 = d0 * d0 + d2 * d2 + d3 * d3 > 9.0E-4 || this.positionUpdateTicks >= 20;
            final boolean flag4 = d4 != 0.0 || d5 != 0.0;
            if (this.ridingEntity == null) {
                if (flag3 && flag4) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(event.x, event.y, event.z, event.yaw, event.pitch, event.onGround));
                }
                else if (flag3) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(event.x, event.y, event.z, event.onGround));
                }
                else if (flag4) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(event.yaw, event.pitch, event.onGround));
                }
                else {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer(event.onGround));
                }
            }
            else {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0, this.motionZ, event.yaw, event.pitch, event.onGround));
                flag3 = false;
            }
            ++this.positionUpdateTicks;
            if (flag3) {
                this.lastReportedPosX = event.x;
                this.lastReportedPosY = event.y;
                this.lastReportedPosZ = event.z;
                this.positionUpdateTicks = 0;
            }
            PlayerUtils.lastGround = event.onGround;
            RotationUtils.lastLastReportedPitch = this.lastReportedPitch;
            if (flag4) {
                this.lastReportedYaw = event.yaw;
                this.lastReportedPitch = event.pitch;
            }
        }
        MinecraftForge.EVENT_BUS.post((Event)new MotionUpdateEvent.Post(event));
    }
}
