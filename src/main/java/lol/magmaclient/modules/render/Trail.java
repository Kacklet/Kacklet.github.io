package lol.magmaclient.modules.render;

import lol.magmaclient.Magma;
import lol.magmaclient.events.PlayerUpdateEvent;
import lol.magmaclient.events.WorldJoinEvent;
import lol.magmaclient.modules.Module;
import lol.magmaclient.settings.NumberSetting;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Trail extends Module
{
    public static final NumberSetting count;
    private static final List<Vec3> vecs;

    public Trail() {
        super("Trail", Category.RENDER);
        this.addSettings(Trail.count);
    }

    @Override
    public void assign()
    {
        Magma.trail = this;
    }

    @SubscribeEvent
    public void onUpdate(final PlayerUpdateEvent event) {
        if (this.isToggled()) {
            Trail.vecs.add(new Vec3(Magma.mc.thePlayer.prevPosX, Magma.mc.thePlayer.prevPosY + 0.1, Magma.mc.thePlayer.prevPosZ));
            while (Trail.vecs.size() > Trail.count.getValue()) {
                Trail.vecs.remove(0);
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(final RenderWorldLastEvent event) {
        if (this.isToggled() && !Trail.vecs.isEmpty() && Magma.mc.thePlayer != null && Magma.mc.getRenderManager() != null) {
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(2.5f);
            GL11.glDisable(3553);
            GL11.glDisable(2884);
            GL11.glShadeModel(7425);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glBegin(3);
            int index = 0;
            for (final Vec3 vec : Trail.vecs) {
                final boolean isFirst = index == 0;
                ++index;
                final Color color = Magma.clickGui.getColor(index);
                GL11.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
                if (isFirst && Trail.vecs.size() > 2) {
                    final Vec3 newVec = Trail.vecs.get(1);
                    GL11.glVertex3d(this.interpolate(vec.xCoord, newVec.xCoord, event.partialTicks) - Magma.mc.getRenderManager().viewerPosX, this.interpolate(vec.yCoord, newVec.yCoord, event.partialTicks) - Magma.mc.getRenderManager().viewerPosY, this.interpolate(vec.zCoord, newVec.zCoord, event.partialTicks) - Magma.mc.getRenderManager().viewerPosZ);
                }
                else {
                    GL11.glVertex3d(vec.xCoord - Magma.mc.getRenderManager().viewerPosX, vec.yCoord - Magma.mc.getRenderManager().viewerPosY, vec.zCoord - Magma.mc.getRenderManager().viewerPosZ);
                }
            }
            final Color color = Magma.clickGui.getColor(index);
            GL11.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
            GL11.glVertex3d(Magma.mc.thePlayer.prevPosX + (Magma.mc.thePlayer.posX - Magma.mc.thePlayer.prevPosX) * event.partialTicks - Magma.mc.getRenderManager().viewerPosX, Magma.mc.thePlayer.prevPosY + (Magma.mc.thePlayer.posY - Magma.mc.thePlayer.prevPosY) * event.partialTicks - Magma.mc.getRenderManager().viewerPosY + 0.1, Magma.mc.thePlayer.prevPosZ + (Magma.mc.thePlayer.posZ - Magma.mc.thePlayer.prevPosZ) * event.partialTicks - Magma.mc.getRenderManager().viewerPosZ);
            GL11.glEnd();
            GL11.glEnable(3553);
            GL11.glShadeModel(7424);
            GL11.glEnable(2884);
            GL11.glDisable(2848);
            GL11.glDisable(2881);
            GL11.glDisable(3042);
        }
    }

    @SubscribeEvent
    public void onWorldJoin(final WorldJoinEvent event) {
        Trail.vecs.clear();
    }

    private double interpolate(final double prev, final double newPos, final float partialTicks) {
        return prev + (newPos - prev) * partialTicks;
    }

    private boolean hasMoved() {
        return Magma.mc.thePlayer.posZ - Magma.mc.thePlayer.prevPosZ != 0.0 || Magma.mc.thePlayer.posY - Magma.mc.thePlayer.prevPosY != 0.0 || Magma.mc.thePlayer.posX - Magma.mc.thePlayer.prevPosX != 0.0;
    }

    static {
        count = new NumberSetting("Points", 20.0, 5.0, 100.0, 1.0);
        vecs = new ArrayList<Vec3>();
    }
}