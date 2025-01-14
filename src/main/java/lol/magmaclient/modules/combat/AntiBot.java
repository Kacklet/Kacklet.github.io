package lol.magmaclient.modules.combat;

import lol.magmaclient.Magma;
import lol.magmaclient.events.WorldJoinEvent;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.BooleanSetting;
import lol.magmaclient.settings.ModeSetting;
import lol.magmaclient.utils.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

public class AntiBot extends Module
{
    private static final ModeSetting mode;
    private static final BooleanSetting ticksInvis;
    private static final BooleanSetting tabTicks;
    private static final BooleanSetting npcCheck;
    private static final HashMap<Integer, EntityData> entityData;

    public AntiBot() {
        super("Anti Bot", Category.COMBAT);
        this.addSettings(mode, ticksInvis, tabTicks, npcCheck);
    }

    @Override
    public void assign()
    {
        Magma.antiBot = this;
    }

    @SubscribeEvent
    public void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
        final EntityData data = entityData.get(event.entity.getEntityId());
        if (data == null) {
            entityData.put(event.entity.getEntityId(), new EntityData(event.entity));
        }
        else {
            entityData.get(event.entity.getEntityId()).update();
        }
    }

    public static boolean isValidEntity(final Entity entity) {
        if (Magma.antiBot.isToggled() && entity instanceof EntityPlayer && entity != Magma.mc.thePlayer) {
            final EntityData data = AntiBot.entityData.get(entity.getEntityId());
            if (data != null && mode.is("Hypixel")) {
                return (!tabTicks.isEnabled() || data.getTabTicks() >= 150) && (!ticksInvis.isEnabled() || data.getTicksExisted() - data.getTicksInvisible() >= 150) && (!npcCheck.isEnabled() || !PlayerUtils.isNPC(entity));
            }
        }
        return true;
    }

    @SubscribeEvent
    public void onWorldJoin(final WorldJoinEvent event) {
        entityData.clear();
    }

    static {
        mode = new ModeSetting("Mode", "Hypixel", new String[] { "Hypixel" });
        ticksInvis = new BooleanSetting("Invis ticks check", false, aBoolean -> !mode.is("Hypixel"));
        tabTicks = new BooleanSetting("Tab ticks check", false, aBoolean -> !mode.is("Hypixel"));
        npcCheck = new BooleanSetting("NPC check", true, aBoolean -> !mode.is("Hypixel"));
        entityData = new HashMap<Integer, EntityData>();
    }

    private static class EntityData
    {
        private int ticksInvisible;
        private int tabTicks;
        private final Entity entity;

        public EntityData(final Entity entity) {
            this.entity = entity;
            this.update();
        }

        public int getTabTicks() {
            return this.tabTicks;
        }

        public int getTicksInvisible() {
            return this.ticksInvisible;
        }

        public int getTicksExisted() {
            return this.entity.ticksExisted;
        }

        public void update() {
            if (this.entity instanceof EntityPlayer && Magma.mc.getNetHandler() != null && Magma.mc.getNetHandler().getPlayerInfo(this.entity.getUniqueID()) != null) {
                ++this.tabTicks;
            }
            if (this.entity.isInvisible()) {
                ++this.ticksInvisible;
            }
        }
    }
}
