package lol.magmaclient.modules.protection;

import lol.magmaclient.Magma;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.ModeSetting;
import lol.magmaclient.settings.NumberSetting;
import lol.magmaclient.utils.MilliTimer;
import lol.magmaclient.ui.notifications.Notification;
import lol.magmaclient.utils.api.PlanckeScraper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class StaffAnalyser extends Module
{
    private ModeSetting mode;
    private NumberSetting delay;
    private MilliTimer timer;
    private int lastBans;

    public StaffAnalyser() {
        super("Staff Analyser", Category.PROTECTIONS);
        this.mode = new ModeSetting("Mode", "Chat", "Chat", "Notification");
        this.delay = new NumberSetting("Check Delay (Seconds)", 5.0, 5.0, 60.0, 1.0);
        this.timer = new MilliTimer();
        this.lastBans = -1;
        this.addSettings(this.mode, this.delay);
    }

    @Override
    public void assign()
    {
        Magma.staffAnalyser = this;
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (this.isToggled() && this.timer.hasTimePassed((long)(this.delay.getValue() * 1000.0))) {
            this.timer.reset();
            new Thread(() -> {
                final int bans = PlanckeScraper.getBans();
                if (bans != this.lastBans && this.lastBans != -1 && bans > this.lastBans) {
                    if(this.mode.is("Notification")) {
                        Magma.notificationManager.showNotification(String.format("Staff has banned %s %s in the last %s seconds", bans - this.lastBans, (bans - this.lastBans > 1) ? "people" : "person", (int)this.delay.getValue()), 2500, (bans - this.lastBans > 2) ? Notification.NotificationType.WARNING : Notification.NotificationType.INFO);
                    } else {
                        Magma.sendMessageWithPrefix(String.format("Staff has banned %s %s in the last %s seconds", bans - this.lastBans, (bans - this.lastBans > 1) ? "people" : "person", (int)this.delay.getValue()));
                    }
                }
                this.lastBans = bans;
            }).start();
        }
    }
}
