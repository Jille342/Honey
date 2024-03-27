package client.utils;

import client.mixin.client.AccessorMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import org.lwjgl.opengl.GL11;

public class RenderingUtils2 extends EntityRenderer {
    public RenderingUtils2(Minecraft p_i45076_1_, IResourceManager p_i45076_2_) {
        super(p_i45076_1_, p_i45076_2_);
    }

    public static void drawLine3D(float x, float y, float z, float x1, float y1, float z1, int color) {
        RenderingUtils.pre3D();
        GL11.glLoadIdentity();
        float var11 = (color >> 24 & 0xFF) / 255.0F;
        float var6 = (color >> 16 & 0xFF) / 255.0F;
        float var7 = (color >> 8 & 0xFF) / 255.0F;
        float var8 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(var6, var7, var8, var11);
        GL11.glLineWidth(0.5f);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glEnd();
        RenderingUtils.post3D();
    }
    public static void drawLine(double x1, double y1, int color) {
        float var11 = (color >> 24 & 0xFF) / 255.0F;
        float var6 = (color >> 16 & 0xFF) / 255.0F;
        float var7 = (color >> 8 & 0xFF) / 255.0F;
        float var8 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(var6, var7, var8, var11);
        GL11.glDisable(3553);
        GL11.glLineWidth(1.5F);
        GL11.glBegin(1);
        GL11.glVertex2d(0, 0);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GL11.glEnable(3553);
    }

    public static void draw3DLine(float x, float y, float z, int color) {

        RenderingUtils.pre3D();
        GL11.glLoadIdentity();
        float var11 = (color >> 24 & 0xFF) / 255.0F;
        float var6 = (color >> 16 & 0xFF) / 255.0F;
        float var7 = (color >> 8 & 0xFF) / 255.0F;
        float var8 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(var6, var7, var8, var11);
        GL11.glLineWidth(1.5f);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(0, Minecraft.getMinecraft().thePlayer.getEyeHeight(), 0);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        RenderingUtils.post3D();
    }
}
