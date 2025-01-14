package lol.magmaclient.modules.movement;

import lol.magmaclient.Magma;
import lol.magmaclient.events.MotionUpdateEvent;
import lol.magmaclient.events.PacketReceivedEvent;
import lol.magmaclient.events.PacketSentEvent;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.ModeSetting;
import lol.magmaclient.settings.NumberSetting;
import lol.magmaclient.utils.MilliTimer;
import lol.magmaclient.utils.PacketUtils;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S30PacketWindowItems;

public class NoSlow extends Module
{
    public NumberSetting eatingSlowdown;
    public NumberSetting swordSlowdown;
    public NumberSetting bowSlowdown;
    public ModeSetting mode;
    private final MilliTimer blockDelay;

    public NoSlow() {
        super("No Slow", 0, Category.MOVEMENT);
        this.eatingSlowdown = new NumberSetting("Eating slow", 1.0, 0.2, 1.0, 0.1);
        this.swordSlowdown = new NumberSetting("Sword slow", 1.0, 0.2, 1.0, 0.1);
        this.bowSlowdown = new NumberSetting("Bow slow", 1.0, 0.2, 1.0, 0.1);
        this.mode = new ModeSetting("Mode", "Vanilla", new String[] { "Hypixel", "Vanilla" });
        this.blockDelay = new MilliTimer();
        this.addSettings(this.mode, this.swordSlowdown, this.bowSlowdown, this.eatingSlowdown);
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Magma.noSlow = this;
    }

    @SubscribeEvent
    public void onPacket(final PacketReceivedEvent event) {
        if (event.packet instanceof S30PacketWindowItems && Magma.mc.thePlayer != null && this.isToggled() && this.mode.is("Hypixel") && Magma.mc.thePlayer.isUsingItem() && Magma.mc.thePlayer.getItemInUse().getItem() instanceof ItemSword) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void unUpdate(final MotionUpdateEvent.Post event) {
        if (this.isToggled() && Magma.mc.thePlayer.isUsingItem() && this.mode.is("Hypixel")) {
            if (this.blockDelay.hasTimePassed(250L) && Magma.mc.thePlayer.getItemInUse().getItem() instanceof ItemSword) {
                Magma.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C08PacketPlayerBlockPlacement(Magma.mc.thePlayer.getHeldItem()));
                Magma.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction((Entity) Magma.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                Magma.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction((Entity) Magma.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                this.blockDelay.reset();
            }
            PacketUtils.sendPacketNoEvent((Packet<?>)new C09PacketHeldItemChange(Magma.mc.thePlayer.inventory.currentItem));
        }
    }

    @SubscribeEvent
    public void onPacket(final PacketSentEvent event) {
        if (this.isToggled() && this.mode.is("Hypixel") && event.packet instanceof C08PacketPlayerBlockPlacement && ((C08PacketPlayerBlockPlacement)event.packet).getStack() != null && ((C08PacketPlayerBlockPlacement)event.packet).getStack().getItem() instanceof ItemSword) {
            this.blockDelay.reset();
        }
    }
}
