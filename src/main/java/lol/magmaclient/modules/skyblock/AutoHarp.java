package lol.magmaclient.modules.skyblock;

import lol.magmaclient.Magma;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.NumberSetting;
import lol.magmaclient.utils.GuiUtils;
import lol.magmaclient.utils.Multithreading;
import lol.magmaclient.utils.font.Fonts;
import lol.magmaclient.utils.render.RenderUtils;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AutoHarp extends Module {
    public NumberSetting autoHarpDelay;
    // Global
    private boolean inHarp;
    private Slot slot;
    private long timestamp;
    private long startedSongTimestamp;
    private int updates;
    private final ArrayList<ItemStack> currentInventory = new ArrayList<>();
    private long lastContainerUpdate;
    private final Random rand = new Random(System.currentTimeMillis());

    public AutoHarp() {
        super("Auto Harp", Category.SKYBLOCK);
        this.autoHarpDelay = new NumberSetting("Click delay (Milliseconds)", 100, 0, 500, 1);
        this.addSetting(autoHarpDelay);
        setToggled(false);
    }

    @Override
    public void assign()
    {
        Magma.autoHarp = this;
    }

    public int getRandDelay()
    {
        return rand.nextInt(25);
    }

    @SubscribeEvent
    public final void onGuiOpen(GuiOpenEvent event) {
        inHarp = GuiUtils.getInventoryName(event.gui).startsWith("Harp -");
        updates = 1;
        currentInventory.clear();
    }

    @SubscribeEvent
    public void onBackgroundDraw(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!Magma.autoHarp.isToggled() || !inHarp) return;

        if (Magma.mc.thePlayer.openContainer.inventorySlots.size() != currentInventory.size()) {
            for (Slot slot : Magma.mc.thePlayer.openContainer.inventorySlots) {
                currentInventory.add(slot.getStack());
            }
            return;
        }

        boolean updated = false;

        if (System.currentTimeMillis() - lastContainerUpdate > 175) {
            for (int i = 0; i < Magma.mc.thePlayer.openContainer.inventorySlots.size(); i++) {
                ItemStack itemStack1 = Magma.mc.thePlayer.openContainer.inventorySlots.get(i).getStack();
                ItemStack itemStack2 = currentInventory.get(i);
                if (!ItemStack.areItemStacksEqual(itemStack1, itemStack2)) {
                    if (updates < 3) {
                        startedSongTimestamp = System.currentTimeMillis();
                    }

                    lastContainerUpdate = System.currentTimeMillis();
                    currentInventory.set(i, itemStack1);
                    updated = true;
                }
            }
        }

        if (updated) {
            updates++;
            for (int slotNumber = 0; slotNumber < currentInventory.size(); slotNumber++) {
                if (slotNumber > 26 && slotNumber < 36) {
                    ItemStack itemStack = currentInventory.get(slotNumber);
                    if (itemStack != null && itemStack.getItem() instanceof ItemBlock && ((ItemBlock) itemStack.getItem()).getBlock() == Blocks.wool) {
                        int finalSlotNumber = slotNumber;
                        Multithreading.schedule(() -> {
                            slot = Magma.mc.thePlayer.openContainer.inventorySlots.get(finalSlotNumber);
                            timestamp = System.currentTimeMillis();
                            if(Magma.clientSettings.debug.isEnabled()) {
                                Magma.sendMessageWithPrefix("(&cAutoHarp&f) Clicked Slot " + slot.slotNumber+9 + " (&c" + (timestamp - startedSongTimestamp) +"&f)");
                            }
                            Magma.mc.playerController.windowClick(Magma.mc.thePlayer.openContainer.windowId,finalSlotNumber + 9,2,3, Magma.mc.thePlayer);
                        }, (long)autoHarpDelay.getValue()+getRandDelay(), TimeUnit.MILLISECONDS);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!Magma.autoHarp.isToggled() || !inHarp) return;

        RenderUtils.setupRender(true);
        Fonts.getSecondary().drawSmoothString("ore", Fonts.getSecondary().drawSmoothString("K", 5.0, 5.0f, Color.white.darker().getRGB()) + 1.0f, 5.0f, Magma.themeManager.getSecondaryColor(0).getRGB());
        if(Magma.clientSettings.debug.isEnabled()) {
            Magma.mc.fontRendererObj.drawStringWithShadow("Song Speed: " + (System.currentTimeMillis() - startedSongTimestamp) / updates + "ms",5,15,Color.LIGHT_GRAY.getRGB());
            Magma.mc.fontRendererObj.drawStringWithShadow("Gui Updates: " + updates,5,25,Color.LIGHT_GRAY.getRGB());
            Magma.mc.fontRendererObj.drawStringWithShadow("Time Elapsed : " + (System.currentTimeMillis() - startedSongTimestamp),5,35,Color.LIGHT_GRAY.getRGB());
        }
        if (slot != null && System.currentTimeMillis() - timestamp < (autoHarpDelay.getValue()+getRandDelay())) {
            Magma.mc.fontRendererObj.drawStringWithShadow(
                    "Click",
                    (event.gui.width - 176) / 2f + slot.xDisplayPosition + 8 - Magma.mc.fontRendererObj.getStringWidth("Click") / 2f,
                    (event.gui.height - 222) / 2f + slot.yDisplayPosition + 24,
                    Color.RED.getRGB()
            );
        }
        RenderUtils.setupRender(false);
    }
}
