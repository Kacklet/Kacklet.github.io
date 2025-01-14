package lol.magmaclient.modules.skyblock;

import lol.magmaclient.Magma;
import lol.magmaclient.events.PacketReceivedEvent;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.ModeSetting;
import lol.magmaclient.settings.NumberSetting;
import lol.magmaclient.utils.SkyblockUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;

public class PurseSpoofer extends Module {
    public ModeSetting mode = new ModeSetting("Mode","Add to current purse","Add to current purse", "Set purse to");
    public NumberSetting coins = new NumberSetting("Coins", 0.0, Double.MIN_VALUE + 1, Double.MAX_VALUE - 1, 1.0E-5, a -> true);

    public PurseSpoofer() {
        super("Purse Spoofer", Category.SKYBLOCK);
        this.addSettings(mode, coins);
        //this.setVersionType(VersionType.PREMIUM);
    }

    @Override
    public void assign() {
        Magma.purseSpoofer = this;
    }

    @Override
    public void onEnable() {
        Magma.sendMessageWithPrefix("(&cPurseSpoofer&f) Usage -> .setpurse <value>");
    }

    @SubscribeEvent
    public void packet(PacketReceivedEvent event) {
        if (!isToggled() || Magma.mc.thePlayer == null || Magma.mc.theWorld == null) return;

        Packet<?> packet = event.packet;
        if (packet instanceof S3EPacketTeams) {
            S3EPacketTeams team = (S3EPacketTeams) packet;
            String strip = StringUtils.stripControlCodes(team.getPrefix()).toLowerCase();

            if (!SkyblockUtils.isOnHypixel() || !SkyblockUtils.isOnSkyBlock() || !strip.startsWith("purse: ")) return;

            final double purseValue = Double.parseDouble(strip.split(" ")[1].replaceAll(",", ""));

            String newPurse = null;
            if(mode.getSelected().equals("Add to current purse")) {
                newPurse = Magma.fancy + "fPurse: " + Magma.fancy + "6" + String.format("%,.1f", (purseValue + coins.getValue()));
            } else {
                newPurse = Magma.fancy + "fPurse: " + Magma.fancy + "6" + String.format("%,.1f", (coins.getValue()));
            }

            if(Magma.clientSettings.debug.isEnabled()) {
                System.out.println("found purse. New purse is " + newPurse);
                System.out.println("Values are purse: " + purseValue + " and add is " + coins.getValue());
            }

            try {
                Field field = S3EPacketTeams.class.getDeclaredField("prefix");
                field.setAccessible(true);
                field.set(team, newPurse);
            } catch (Exception e) {
                try {
                    Field field = S3EPacketTeams.class.getDeclaredField("field_149319_c");
                    field.setAccessible(true);
                    field.set(team, newPurse);
                } catch (Exception ex) {
                    e.printStackTrace();
                }
            }
        }
    }
}