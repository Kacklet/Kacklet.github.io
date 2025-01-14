package lol.magmaclient.modules.render;

import lol.magmaclient.Magma;
import lol.magmaclient.events.GuiChatEvent;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.BooleanSetting;
import lol.magmaclient.settings.ModeSetting;
import lol.magmaclient.settings.NumberSetting;
import lol.magmaclient.utils.render.RenderUtils;
import lol.magmaclient.modules.combat.KillAura;
import lol.magmaclient.ui.hud.impl.TargetHud;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class TargetDisplay extends Module
{
    public static TargetDisplay instance;
    public ModeSetting blurStrength;
    public BooleanSetting targetESP;
    public NumberSetting x;
    public NumberSetting y;
    public BooleanSetting outline;

    public static TargetDisplay getInstance() {
        return TargetDisplay.instance;
    }

    public TargetDisplay() {
        super("Target Display", Category.RENDER);
        this.blurStrength = new ModeSetting("Blur Strength", "Low", new String[] { "None", "Low", "High" });
        this.targetESP = new BooleanSetting("Target ESP", true);
        this.x = new NumberSetting("targetX", 0.0, -100000.0, 100000.0, 1.0E-5, a -> true);
        this.y = new NumberSetting("targetY", 0.0, -100000.0, 100000.0, 1.0E-5, a -> true);
        this.outline = new BooleanSetting("Outline", true);
        this.addSettings(this.outline, this.blurStrength, this.targetESP, this.x, this.y);
    }

    @Override
    public void assign()
    {
        Magma.targetDisplay = this;
    }

    @SubscribeEvent
    public void onRender(final RenderWorldLastEvent event) {
        if (KillAura.target != null && KillAura.target.getHealth() > 0.0f && !KillAura.target.isDead && this.targetESP.isEnabled() && this.isToggled()) {
            RenderUtils.drawTargetESP(KillAura.target, Magma.clickGui.getColor(), event.partialTicks);
        }
    }

    @SubscribeEvent
    public void onChatEvent(final GuiChatEvent event) {
        if (!this.isToggled()) {
            return;
        }
        final TargetHud component = TargetHud.INSTANCE;
        if (event instanceof GuiChatEvent.MouseClicked) {
            if (component.isHovered(event.mouseX, event.mouseY)) {
                component.startDragging();
            }
        }
        else if (event instanceof GuiChatEvent.MouseReleased || event instanceof GuiChatEvent.Closed) {
            component.stopDragging();
        }
    }

    @SubscribeEvent
    public void onRender(final RenderGameOverlayEvent.Pre event) {
        if (this.isToggled() && event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            TargetHud.INSTANCE.drawScreen();
        }
    }

    static {
        TargetDisplay.instance = new TargetDisplay();
    }
}