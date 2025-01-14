package lol.magmaclient.modules.player;

import lol.magmaclient.Magma;
import lol.magmaclient.events.PacketSentEvent;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.BooleanSetting;
import lol.magmaclient.settings.NumberSetting;
import lol.magmaclient.utils.MovementUtils;
import lol.magmaclient.utils.render.RenderUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FreeCam extends Module
{
    private EntityOtherPlayerMP playerEntity;
    public NumberSetting speed;
    public BooleanSetting tracer;

    public FreeCam() {
        super("FreeCam", Category.PLAYER);
        this.speed = new NumberSetting("Speed", 3.0, 0.1, 5.0, 0.1);
        this.tracer = new BooleanSetting("Show tracer", false);
        this.addSettings(this.speed, this.tracer);
    }

    @Override
    public void assign()
    {
        Magma.freeCam = this;
    }

    @Override
    public void onEnable() {
        if (Magma.mc.theWorld != null) {
            (this.playerEntity = new EntityOtherPlayerMP((World) Magma.mc.theWorld, Magma.mc.thePlayer.getGameProfile())).copyLocationAndAnglesFrom((Entity) Magma.mc.thePlayer);
            this.playerEntity.onGround = Magma.mc.thePlayer.onGround;
            Magma.mc.theWorld.addEntityToWorld(-2137, (Entity)this.playerEntity);
        }
    }

    @Override
    public void onDisable() {
        if (Magma.mc.thePlayer == null || Magma.mc.theWorld == null || this.playerEntity == null) {
            return;
        }
        Magma.mc.thePlayer.noClip = false;
        Magma.mc.thePlayer.setPosition(this.playerEntity.posX, this.playerEntity.posY, this.playerEntity.posZ);
        Magma.mc.theWorld.removeEntityFromWorld(-2137);
        this.playerEntity = null;
        Magma.mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
    }

    @SubscribeEvent
    public void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
        if (this.isToggled()) {
            Magma.mc.thePlayer.noClip = true;
            Magma.mc.thePlayer.fallDistance = 0.0f;
            Magma.mc.thePlayer.onGround = false;
            Magma.mc.thePlayer.capabilities.isFlying = false;
            Magma.mc.thePlayer.motionY = 0.0;
            if (!MovementUtils.isMoving()) {
                Magma.mc.thePlayer.motionZ = 0.0;
                Magma.mc.thePlayer.motionX = 0.0;
            }
            final double speed = this.speed.getValue() * 0.1;
            Magma.mc.thePlayer.jumpMovementFactor = (float)speed;
            if (Magma.mc.gameSettings.keyBindJump.isKeyDown()) {
                final EntityPlayerSP thePlayer = Magma.mc.thePlayer;
                thePlayer.motionY += speed * 3.0;
            }
            if (Magma.mc.gameSettings.keyBindSneak.isKeyDown()) {
                final EntityPlayerSP thePlayer2 = Magma.mc.thePlayer;
                thePlayer2.motionY -= speed * 3.0;
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(final RenderWorldLastEvent event) {
        if (this.isToggled() && this.playerEntity != null && this.tracer.isEnabled()) {
            RenderUtils.tracerLine((Entity)this.playerEntity, event.partialTicks, 1.0f, Magma.clickGui.getColor());
        }
    }

    @SubscribeEvent
    public void onWorldChange(final WorldEvent.Load event) {
        if (this.isToggled()) {
            this.toggle();
        }
    }

    @SubscribeEvent
    public void onPacket(final PacketSentEvent event) {
        if (this.isToggled() && (event.packet instanceof C03PacketPlayer || event.packet instanceof C09PacketHeldItemChange || event.packet instanceof C08PacketPlayerBlockPlacement || event.packet instanceof C0BPacketEntityAction)) {
            event.setCanceled(true);
        }
    }
}