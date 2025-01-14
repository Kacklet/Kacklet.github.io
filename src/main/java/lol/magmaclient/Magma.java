package lol.magmaclient;

import lol.magmaclient.managers.*;
import lol.magmaclient.modules.combat.AimAssist;
import lol.magmaclient.modules.combat.AntiBot;
import lol.magmaclient.modules.combat.AutoClicker;
import lol.magmaclient.modules.combat.KillAura;
import lol.magmaclient.modules.misc.BuildGuesser;
import lol.magmaclient.modules.misc.MurderFinder;
import lol.magmaclient.modules.misc.ServerBeamer;
import lol.magmaclient.modules.movement.BunnyHop;
import lol.magmaclient.modules.movement.GuiMove;
import lol.magmaclient.modules.movement.NoSlow;
import lol.magmaclient.modules.movement.SafeWalk;
import lol.magmaclient.modules.player.*;
import lol.magmaclient.modules.protection.ModHider;
import lol.magmaclient.modules.protection.NickHider;
import lol.magmaclient.modules.protection.Proxy;
import lol.magmaclient.modules.protection.StaffAnalyser;
import lol.magmaclient.modules.render.*;
import lol.magmaclient.modules.skyblock.*;
import lol.magmaclient.modules.ClientSettings;
import lol.magmaclient.modules.Module;
import lol.magmaclient.ui.notifications.Notification;
//import lol.magmaclient.utils.api.ServerUtils;
import lol.magmaclient.utils.font.Fonts;
import lol.magmaclient.utils.render.shader.BlurUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = Magma.MOD_ID, name = Magma.MOD_NAME, version = Magma.VERSION)
public class Magma {
    public static final String MOD_ID = "@ID@";
    public static final String MOD_NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    public static final String VERSION_NUMBER = "@VER_NUM@";
    public static final String licensed = "@LICENSED@";

    // Managers
    public static LicenseManager licenseManager;
    public static UpdateManager updateManager;
    public static ModuleManager moduleManager;
    public static ConfigManager configManager;
    public static ThemeManager themeManager;
    public static NotificationManager notificationManager;
    //public static ServerUtils serverUtils = new ServerUtils();

    // Variables
    public static Minecraft mc;
    public static char fancy = (char) 167;

    // Modules

    // System
    public static Gui clickGui;
    public static ClientSettings clientSettings;

    // Render
    public static Animations animations;
    public static Animator animator;
    public static ChinaHat chinaHat;
    public static Fullbright fullbright;
    public static Giants giants;
    public static ModernInterfaces modernInterfaces;
    public static InventoryDisplay inventoryDisplay;
    public static TargetDisplay targetDisplay;
    public static Nametags nametags;
    public static PlayerESP playerESP;
    public static ChestESP chestESP;
    public static PopupAnimation popupAnimation;
    public static Trail trail;
    public static Trajectories trajectories;

    // Combat
    public static AntiBot antiBot;
    public static AimAssist aimAssist;
    public static AutoClicker autoClicker;
    public static KillAura killAura;
    public static NoSlow noSlow;

    // Player
    public static AutoTool autoTool;
    public static FreeCam freeCam;
    public static ChestStealer chestStealer;
    public static FastPlace fastPlace;
    public static FastBreak fastBreak;
    public static Velocity velocity;

    // Movement
    public static BunnyHop bunnyHop;
    public static GuiMove guiMove;
    public static SafeWalk safeWalk;

    // Skyblock
    public static AutoExperiments autoExperiments;
    public static AutoHarp autoHarp;
    public static EndESP endESP;
    public static GhostBlocks ghostBlock;
    public static PurseSpoofer purseSpoofer;

    // Misc
    public static MurderFinder murderFinder;
    public static ServerBeamer serverBeamer;
    public static BuildGuesser buildGuesser;

    // Protections
    public static ModHider modHider;
    public static NickHider nickHider;
    public static StaffAnalyser staffAnalyser;
    public static Proxy proxy;

    public static void start()
    {
        Magma.mc = Minecraft.getMinecraft();

        licenseManager = new LicenseManager();

        moduleManager = new ModuleManager("lol.magmaclient.modules");

        moduleManager.initReflection();

        configManager = new ConfigManager();

        themeManager = new ThemeManager();

        notificationManager = new NotificationManager();

        CommandManager.init();

        //ServerUtils.loadChangelog();

        for (Module module : moduleManager.modules)
        {
            MinecraftForge.EVENT_BUS.register(module);
        }

        BlurUtils.registerListener();

        updateManager = new UpdateManager();
    }

    public static void handleKey(int key)
    {
        for (Module module : moduleManager.modules)
        {
            if (module.getKeycode() == key)
            {
                module.toggle();
                if (!clickGui.disableNotifs.isEnabled() && !module.getName().equals("Gui"))
                    notificationManager.showNotification((module.isToggled() ? "Enabled" : "Disabled") + " " + module.getName(), 2000, Notification.NotificationType.INFO);
            }
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent pre) {
        MinecraftForge.EVENT_BUS.register(this);
        Fonts.bootstrap();
        Magma.start();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(event.entity instanceof net.minecraft.client.entity.EntityPlayerSP) {
            sendMessageWithPrefix("Enjoy :)");

        }
    }

    public static void sendMessage(Object object) {
        if (Magma.mc.thePlayer != null)
        {
            mc.thePlayer.addChatMessage(new ChatComponentText(object.toString()));
        }
    }

    public static void sendMessageWithPrefix(Object object) {
        if (Magma.mc.thePlayer != null)
        {
            Magma.mc.thePlayer.addChatMessage(new ChatComponentText(Magma.fancy + "7[" + Magma.fancy + "q" + "Magma" + Magma.fancy + "r" + Magma.fancy + "7] " + Magma.fancy + "f" + object.toString().replaceAll("&", Magma.fancy + "")));
        }
    }

}
