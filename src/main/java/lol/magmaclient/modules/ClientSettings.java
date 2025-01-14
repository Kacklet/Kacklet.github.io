package lol.magmaclient.modules;

import lol.magmaclient.Magma;
import lol.magmaclient.settings.BooleanSetting;
import lol.magmaclient.settings.ModeSetting;
import lol.magmaclient.ui.notifications.Notification;

import org.json.JSONObject;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.RichPresence;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class ClientSettings extends Module {
    public ModeSetting hideModules;
    public BooleanSetting debug;
    public BooleanSetting richPresence;
    public BooleanSetting autoUpdate;
    public BooleanSetting cosmeticsUnlocker;

    // Rich Presence

    public static IPCClient ipcClient = new IPCClient(1196540533611450588L);
    private static boolean hasConnected;
    private static boolean shouldConnect = true;
    private static RichPresence richPresenceData;

    // Cosmetics Unlocker

    private boolean unlockerToggle;

    public ClientSettings() {
        super("Client Settings", Category.SETTINGS);
        this.hideModules = new ModeSetting("Hidden modules", "None", "None", "Detected", "Premium", "Premium + Detected");
        this.debug = new BooleanSetting("Developer Mode", false);
        this.richPresence = new BooleanSetting("Rich Presence", true);
        this.autoUpdate = new BooleanSetting("Auto Update", true);
        this.cosmeticsUnlocker = new BooleanSetting("Unlock Cosmetics", true);

        unlockerToggle = unlockerToggle();

        this.addSettings("Client", hideModules, debug, autoUpdate, richPresence, cosmeticsUnlocker);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(!hasConnected && this.richPresence.isEnabled() && shouldConnect) {
            setupIPC();
        } else if(hasConnected && !this.richPresence.isEnabled()) {
            disableRichPresence();
        }

        if (cosmeticsUnlocker.isEnabled() && !unlockerToggle) {
            Magma.notificationManager.showNotification("Please reboot to apply changes", 5000, Notification.NotificationType.INFO);
            toggleUnlocker(true);
        } else if (!cosmeticsUnlocker.isEnabled() && unlockerToggle) {
            Magma.notificationManager.showNotification("Please reboot to apply changes", 5000, Notification.NotificationType.INFO);
            toggleUnlocker(false);
        }
    }

    public void disableRichPresence() {
        try {
            ipcClient.close();
            hasConnected = false;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setupIPC() {
    }

    private boolean unlockerToggle() {
        try {
            File configFile = new File(System.getenv("LOCALAPPDATA"), "koreCosmetics.json");
            if (!configFile.exists()) {
                return true;
            }

            JSONObject json = new JSONObject(new String(Files.readAllBytes(configFile.toPath())));

            return json.optBoolean("enabled", true);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Kore] Could not read 'enabled' value from config file, returning default value (true)");
            return true;
        }
    }

    private void toggleUnlocker(boolean toggle) {
        try {
            File configFile = new File(System.getenv("LOCALAPPDATA"), "koreCosmetics.json");
            if (!configFile.exists()) {
                return; // it should exist tho since the mixin creates the file before this class is initialized
            }

            JSONObject json = new JSONObject(new String(Files.readAllBytes(configFile.toPath())));

            json.put("enabled", toggle);
            unlockerToggle = toggle;

            PrintWriter pw = new PrintWriter(configFile);
            pw.print(json.toString(2));
            pw.close();

            System.out.println("[Kore] toggled 'enabled' value in config file");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Kore] Could not update 'enabled' value in config file");
        }
    }

    @Override
    public void assign() {
        Magma.clientSettings = this;
    }

}