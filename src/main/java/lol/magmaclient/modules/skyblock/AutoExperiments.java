package lol.magmaclient.modules.skyblock;

import lol.magmaclient.Magma;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.BooleanSetting;
import lol.magmaclient.settings.ModeSetting;
import lol.magmaclient.settings.NumberSetting;
import lol.magmaclient.utils.GuiUtils;
import lol.magmaclient.utils.font.Fonts;
import lol.magmaclient.utils.render.RenderUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoExperiments extends Module {
    public NumberSetting autoExperimentsDelay;
    public ModeSetting delayRandomizer;
    public BooleanSetting chronomatronSolver;
    public BooleanSetting ultrasequencerSolver;
    // Global
    private int ticks = 0;
    private final Random rand = new Random(System.currentTimeMillis());
    private long lastClickTime = 0L;
    // Chronomatron
    static int lastChronomatronRound = 0;
    static List<String> chronomatronPattern = new ArrayList<>();
    static int chronomatronMouseClicks = 0;
    // Ultrasequencer
    static Slot[] clickInOrderSlots = new Slot[36];
    static int lastUltraSequencerClicked = 0;
    private int until = 0;

    public AutoExperiments()
    {
        super("Auto Experiments", Category.SKYBLOCK);
        this.autoExperimentsDelay = new NumberSetting("Click delay (Ticks)", 15, 0, 30, 1);
        this.delayRandomizer = new ModeSetting("Delay Randomizer", "High", new String[] { "Off", "Low", "Medium", "High" });
        this.chronomatronSolver = new BooleanSetting("Chronomatron",true);
        this.ultrasequencerSolver = new BooleanSetting("Ultrasequencer",true);
        this.addSettings(autoExperimentsDelay, delayRandomizer, chronomatronSolver, ultrasequencerSolver);
        //this.setVersionType(VersionType.PREMIUM);
        setToggled(false);
    }

    @Override
    public void assign()
    {
        Magma.autoExperiments = this;
    }

    public int getRandDelay()
    {
        String delayRandomizerIs = delayRandomizer.getSelected();

        switch (delayRandomizerIs) {
            case "Off":
                return 0;
            case "Low":
                return rand.nextInt(75);
            case "High":
                return rand.nextInt(300);
            default:
                return rand.nextInt(150);
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        lastClickTime = 0;
        lastChronomatronRound = 0;
        chronomatronPattern.clear();
        chronomatronMouseClicks = 0;
        clickInOrderSlots = new Slot[36];
    }

    @SubscribeEvent
    public void onTick(GuiScreenEvent.BackgroundDrawnEvent event) {
        if(!Magma.autoExperiments.isToggled()) return;

        if (Magma.mc.currentScreen instanceof GuiChest) {
            GuiChest inventory = (GuiChest) event.gui;
            Container containerChest = inventory.inventorySlots;
            if (containerChest instanceof ContainerChest) {
                List<Slot> invSlots = containerChest.inventorySlots;
                String invName = ((ContainerChest) containerChest).getLowerChestInventory().getDisplayName().getUnformattedText().trim();

                if (chronomatronSolver.isEnabled() && invName.startsWith("Chronomatron (")) {
                    EntityPlayerSP player = Magma.mc.thePlayer;
                    if (player.inventory.getItemStack() == null && invSlots.size() > 48 && invSlots.get(49).getStack() != null) {
                        if (invSlots.get(49).getStack().getDisplayName().startsWith(Magma.fancy+"7Timer: "+ Magma.fancy+"a") && invSlots.get(4).getStack() != null) { // §7Timer: §a
                            int round = invSlots.get(4).getStack().stackSize;
                            int timerSeconds = Integer.parseInt(StringUtils.stripControlCodes(invSlots.get(49).getStack().getDisplayName()).replaceAll("[^\\d]", ""));
                            if (round != lastChronomatronRound && timerSeconds == round + 2) {
                                lastChronomatronRound = round;
                                for (int i = 10; i <= 43; i++) {
                                    ItemStack stack = invSlots.get(i).getStack();
                                    if (stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.stained_hardened_clay)) {
                                        chronomatronPattern.add(stack.getDisplayName());
                                        break;
                                    }
                                }
                            }
                            if (player.inventory.getItemStack() == null && chronomatronMouseClicks < chronomatronPattern.size()) {
                                for (int i = 10; i <= 43; i++) {
                                    ItemStack glass = invSlots.get(i).getStack();
                                    if (player.inventory.getItemStack() == null && glass != null && ticks % 5 == 0  && lastClickTime+((autoExperimentsDelay.getValue()*50L)+getRandDelay()) < System.currentTimeMillis()) {
                                        Slot glassSlot = invSlots.get(i);
                                        if (glass.getDisplayName().equals(chronomatronPattern.get(chronomatronMouseClicks))) {
                                            Magma.mc.playerController.windowClick(Magma.mc.thePlayer.openContainer.windowId,glassSlot.slotNumber,2,3, Magma.mc.thePlayer);
                                            if(Magma.clientSettings.debug.isEnabled()) {
                                                Magma.sendMessageWithPrefix("(&cChronomatron&f) Clicked Slot " + glassSlot.slotNumber + " (&c" + glassSlot.getStack().getDisplayName() + "&f)");
                                                if(lastClickTime > 0) {
                                                    Magma.sendMessageWithPrefix("(&cChronomatron&f) Since last click &c"+(System.currentTimeMillis()-lastClickTime)+"ms&f passed");
                                                }
                                            }
                                            lastClickTime = System.currentTimeMillis();
                                            chronomatronMouseClicks++;
                                            break;
                                        }
                                    }
                                }
                            }
                        } else if (invSlots.get(49).getStack().getDisplayName().equals(Magma.fancy+"aRemember the pattern!")) { // §aRemember the pattern!
                            chronomatronMouseClicks = 0;
                        }
                    }
                }

                if (ultrasequencerSolver.isEnabled() && invName.startsWith("Ultrasequencer (")) {
                    EntityPlayerSP player = Magma.mc.thePlayer;
                    if (player.inventory.getItemStack() == null && invSlots.size() > 48 && invSlots.get(49).getStack() != null && invSlots.get(49).getStack().getDisplayName().startsWith(Magma.fancy+"7Timer: "+ Magma.fancy+"a")) { // §7Timer: §a
                        lastUltraSequencerClicked = 0;
                        for (Slot slot: clickInOrderSlots) {
                            if (slot != null && slot.getStack() != null && StringUtils.stripControlCodes(slot.getStack().getDisplayName()).matches("\\d+")) {
                                int number = Integer.parseInt(StringUtils.stripControlCodes(slot.getStack().getDisplayName()));
                                if (number > lastUltraSequencerClicked) {
                                    lastUltraSequencerClicked = number;
                                }
                            }
                        }
                        if (player.inventory.getItemStack() == null && clickInOrderSlots[lastUltraSequencerClicked] != null && ticks % 2 == 0 && lastUltraSequencerClicked != 0 && until == lastUltraSequencerClicked) {
                            Slot nextSlot = clickInOrderSlots[lastUltraSequencerClicked];
                            if(lastClickTime+((autoExperimentsDelay.getValue()*50L)+getRandDelay()) < System.currentTimeMillis()) {
                                Magma.mc.playerController.windowClick(Magma.mc.thePlayer.openContainer.windowId, nextSlot.slotNumber, 2, 3, Magma.mc.thePlayer);
                                if(Magma.clientSettings.debug.isEnabled()) {
                                    Magma.sendMessageWithPrefix("(&cUltrasequencer&f) Clicked Slot " + nextSlot.slotNumber + " (&c" + (lastUltraSequencerClicked+1) + "&f)");
                                    if(lastClickTime > 0) {
                                        Magma.sendMessageWithPrefix("(&cUltrasequencer&f) Since last click &c"+(System.currentTimeMillis()-lastClickTime)+"ms&f passed");
                                    }
                                }
                                lastClickTime = System.currentTimeMillis();
                                until = lastUltraSequencerClicked + 1;
                                ticks = 0;
                            }
                        }
                        if (player.inventory.getItemStack() == null && clickInOrderSlots[lastUltraSequencerClicked] != null && ticks % 5 == 0 && lastUltraSequencerClicked < 1) {
                            Slot nextSlot = clickInOrderSlots[lastUltraSequencerClicked];
                            if(lastClickTime+((autoExperimentsDelay.getValue()*50L)+getRandDelay()) < System.currentTimeMillis()) {
                                Magma.mc.playerController.windowClick(Magma.mc.thePlayer.openContainer.windowId, nextSlot.slotNumber, 2, 3, Magma.mc.thePlayer);
                                if(Magma.clientSettings.debug.isEnabled()) {
                                    Magma.sendMessageWithPrefix("(&cUltrasequencer&f) Clicked Slot " + nextSlot.slotNumber + " (&c" + (lastUltraSequencerClicked+1) + "&f)");
                                    if(lastClickTime > 0) {
                                        Magma.sendMessageWithPrefix("(&cUltrasequencer&f) Since last click &c"+(System.currentTimeMillis()-lastClickTime)+"ms&f passed");
                                    }
                                }
                                lastClickTime = System.currentTimeMillis();
                                ticks = 0;
                                until = 1;
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        if(!Magma.autoExperiments.isToggled()) return;

        if(GuiUtils.getInventoryName(event.gui).startsWith("Chronomatron") || GuiUtils.getInventoryName(event.gui).startsWith("Ultrasequencer") || GuiUtils.getInventoryName(event.gui).startsWith("Experimentation Table")) {
            RenderUtils.setupRender(true);
            Fonts.getSecondary().drawSmoothString("ore", Fonts.getSecondary().drawSmoothString("K", 5.0, 5.0f, Color.white.darker().getRGB()) + 1.0f, 5.0f, Magma.themeManager.getSecondaryColor(0).getRGB());
            if(Magma.clientSettings.debug.isEnabled()) {
                if(chronomatronSolver.isEnabled()) {
                    Magma.mc.fontRendererObj.drawStringWithShadow("chronomatronSolver is ",5,15,Color.WHITE.getRGB());
                    Magma.mc.fontRendererObj.drawStringWithShadow("Enabled",125,15,Color.GREEN.getRGB());
                } else {
                    Magma.mc.fontRendererObj.drawStringWithShadow("chronomatronSolver is ",5,15,Color.WHITE.getRGB());
                    Magma.mc.fontRendererObj.drawStringWithShadow("Disabled",125,15,Color.RED.getRGB());
                }
                if(ultrasequencerSolver.isEnabled()) {
                    Magma.mc.fontRendererObj.drawStringWithShadow("ultrasequencerSolver is",5,25,Color.WHITE.getRGB());
                    Magma.mc.fontRendererObj.drawStringWithShadow("Enabled",133,25,Color.GREEN.getRGB());
                } else {
                    Magma.mc.fontRendererObj.drawStringWithShadow("ultrasequencerSolver is",5,25,Color.WHITE.getRGB());
                    Magma.mc.fontRendererObj.drawStringWithShadow("Disabled",133,25,Color.RED.getRGB());
                }
            }
            RenderUtils.setupRender(false);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(!Magma.autoExperiments.isToggled() || event.phase != TickEvent.Phase.START) return;

        ticks = (ticks + 1) % 20 == 0 ? 0 : ticks;

        if (Magma.mc.currentScreen instanceof GuiChest) {
            if (Magma.mc.thePlayer != null) {
                ContainerChest chest = (ContainerChest) Magma.mc.thePlayer.openContainer;
                List<Slot> invSlots = ((GuiChest) Magma.mc.currentScreen).inventorySlots.inventorySlots;
                String chestName = chest.getLowerChestInventory().getDisplayName().getUnformattedText().trim();

                if (ultrasequencerSolver.isEnabled() && chestName.startsWith("Ultrasequencer (")) {
                    if (invSlots.get(49).getStack() != null && invSlots.get(49).getStack().getDisplayName().equals(Magma.fancy+"aRemember the pattern!")) { // §aRemember the pattern!
                        for (int i = 9; i <= 44; i++) {
                            if (invSlots.get(i) != null && invSlots.get(i).getStack() != null) {
                                String itemName = StringUtils.stripControlCodes(invSlots.get(i).getStack().getDisplayName());
                                if (itemName.matches("\\d+")) {
                                    int number = Integer.parseInt(itemName);
                                    clickInOrderSlots[number - 1] = invSlots.get(i);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
