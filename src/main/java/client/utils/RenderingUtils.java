package client.utils;

import client.mixin.client.AccessorMinecraft;
import client.utils.font.CFontRenderer;
import client.utils.font.Fonts;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.glu.GLU;
import net.minecraft.util.AxisAlignedBB;

import javax.vecmath.Vector3d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;

import static org.lwjgl.opengl.GL11.*;

public class RenderingUtils implements MCUtil {
    private static final Frustum frustrum = new Frustum();

    private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
    protected static RenderManager renderManager;
    protected static float zLevel;

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }




    public static void drawRainbowRectHorizontal(int x, int y, int x1, int y1, int segments, float alpha) {
        if (segments < 1)
            segments = 1;
        if (segments > x1 - x)
            segments = x1 - x;
        int segmentLength = (x1 - x) / segments;
        long time = System.nanoTime();
        for (int i = 0; i < segments; i++)
            drawRect(x + segmentLength * i, y, x + (segmentLength + 1) * i, y1, Colors.rainbow(time, i, alpha).getRGB());
    }
    public static void drawEsp(EntityLivingBase entity, float pTicks, int hexColor, int hexColorIn) {
        if (entity.isEntityAlive()) {
            double x = getDiff(entity.lastTickPosX, entity.posX, pTicks,mc.getRenderManager().viewerPosX);
            double y = getDiff(entity.lastTickPosY, entity.posY, pTicks, mc.getRenderManager().viewerPosY);
            double z = getDiff(entity.lastTickPosZ, entity.posZ, pTicks,mc.getRenderManager().viewerPosZ);
            boundingBox(entity, x, y, z, hexColor, hexColorIn);
        }
    }
    public static void drawSolidBlockESP(final BlockPos var0, final int var1) {
        final double var2 = var0.getX() - mc.getRenderManager().viewerPosX;
        final double var3 = var0.getY() - mc.getRenderManager().viewerPosY;
        final double var4 = var0.getZ() - mc.getRenderManager().viewerPosZ;
        final double var5 = mc.theWorld.getBlockState(var0).getBlock().getBlockBoundsMaxY() - mc.theWorld.getBlockState(var0).getBlock().getBlockBoundsMinY();
        final float var6 = (var1 >> 16 & 0xFF) / 255.0f;
        final float var7 = (var1 >> 8 & 0xFF) / 255.0f;
        final float var8 = (var1 & 0xFF) / 255.0f;
        final float var9 = (var1 >> 24 & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(var6, var7, var8, var9);
       // drawOutlinedBoundingBox();
        RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(var2, var3, var4, var2 + 1.0, var3 + var5, var4 + 1.0));
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }
    public static void drawLine(final BlockPos var0, final int var1) {
        final double var2 = var0.getX() - mc.getRenderManager().viewerPosX + 0.5;
        final double var3 = var0.getY() - mc.getRenderManager().viewerPosY+ 0.5;
        final double var4 = var0.getZ() - mc.getRenderManager().viewerPosZ + 0.5;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.0f);
        float var5 = (float)(mc.thePlayer.posX - var0.getX());
        var5 = (float)(mc.thePlayer.posY - var0.getY());
        final float var6 = (var1 >> 16 & 0xFF) / 255.0f;
        final float var7 = (var1 >> 8 & 0xFF) / 255.0f;
        final float var8 = (var1 & 0xFF) / 255.0f;
        final float var9 = (var1 >> 24 & 0xFF) / 255.0f;
        GL11.glColor4f(var6, var7, var8, var9);
        GL11.glLoadIdentity();
        final boolean var10 = mc.gameSettings.viewBobbing;
        mc.gameSettings.viewBobbing = false;
        GL11.glBegin(3);
        GL11.glVertex3d(0.0, (double)mc.thePlayer.getEyeHeight(), 0.0);
        GL11.glVertex3d(var2, var3, var4);
        GL11.glVertex3d(var2, var3, var4);
        GL11.glEnd();
        mc.gameSettings.viewBobbing = var10;
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void boundingBox(EntityLivingBase entity, double x, double y, double z, int color, int colorIn) {
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(false);
        GL11.glLineWidth(0.3F);
        AxisAlignedBB var11 = entity.getEntityBoundingBox();
        AxisAlignedBB var12 = new AxisAlignedBB(var11.minX - entity.posX + x, var11.minY - entity.posY + y, var11.minZ - entity.posZ + z, var11.maxX - entity.posX + x, var11.maxY - entity.posY + y, var11.maxZ - entity.posZ + z);
        float[] color1;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float blue = (color >> 8 & 0xFF) / 255.0F;
        float green = (color & 0xFF) / 255.0F;
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        color1 = new float[] { red, blue, green, alpha };
        GL11.glLineWidth(1.0F);
        GL11.glColor4f(color1[0], color1[1], color1[2], 0.8F);
        RenderGlobal.drawSelectionBoundingBox(var12);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
    }
    public static void disableLighting() {
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(3553);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
    }

    public static void drawNametag(Entity entity, String[] text, int color, int type) {
        Vec3 pos = EntityUtils.getInterpolatedPos(entity, TimerUtils.getTimer().renderPartialTicks);
        drawNametag(pos.xCoord, pos.yCoord + entity.height, pos.zCoord, text, color, type);
    }

    public static int[] getFractionIndicies(float[] fractions, float progress) {
        int[] range = new int[2];
        int startPoint = 0;
        while (startPoint < fractions.length && fractions[startPoint] <= progress) {
            startPoint++;
        }

        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }

        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }
    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        Color color = null;
        if (fractions.length == colors.length) {
            int[] indicies = getFractionIndicies(fractions, progress);
            if (indicies[0] < 0 || indicies[0] >= fractions.length || indicies[1] < 0 || indicies[1] >= fractions.length) {
                return colors[0];
            }

            float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
            Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            color = blend(colorRange[0], colorRange[1], 1f - weight);
        }
        return color;
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = (float) 1.0 - r;

        float rgb1[] = new float[3];
        float rgb2[] = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);

        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;

        if (red < 0) {
            red = 0;
        } else if (red > 255) {
            red = 255;
        }
        if (green < 0) {
            green = 0;
        } else if (green > 255) {
            green = 255;
        }
        if (blue < 0) {
            blue = 0;
        } else if (blue > 255) {
            blue = 255;
        }

        Color color = null;
        try {
            color = new Color(red, green, blue);
        } catch (IllegalArgumentException exp) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
            exp.printStackTrace();
        }
        return color;
    }
    public static void drawNametag(double x, double y, double z, String[] text, int color, int type) {
         final CFontRenderer font = Fonts.default18;
        double dist = mc.thePlayer.getDistance(x, y, z);
        double scale = 1, offset = 0;
        int start = 0;
        switch (type) {
            case 0:
                scale = dist / 20 * Math.pow(1.2589254, 0.1 / (dist < 25 ? 0.5 : 2));
                scale = Math.min(Math.max(scale, .5), 5);
                offset = scale > 2 ? scale / 2 : scale;
                scale /= 40;
                start = 10;
                break;
            case 1:
                scale = -((int) dist) / 6.0;
                if (scale < 1) scale = 1;
                scale *= 2.0 / 75.0;
                break;
            case 2:
                scale = 0.0018 + 0.003 * dist;
                if (dist <= 8.0) scale = 0.0245;
                start = -8;
                break;
        }
    //    GlStateManager.pushMatrix();
        GlStateManager.translate(x - mc.getRenderManager().viewerPosX, y + offset - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1 : 1, 0, 0);
        GlStateManager.scale(-scale, -scale, scale);
        if (type == 2) {
            double width = 0;
            int bcolor = new Color(0, 0, 0, 51).getRGB();


            for (int i = 0; i < text.length; i++) {
                double w = font.getStringWidth( text[i]) / 2;
                if (w > width) {
                    width = w;
                }
            }
            drawBorderedRect(-width - 1, -mc.fontRendererObj.FONT_HEIGHT, width + 2, 1, 1.8f, new Color(0, 4, 0, 85).getRGB(), bcolor);
        }
        GlStateManager.enableTexture2D();
        for (int i = 0; i < text.length; i++) {
            font.drawStringWithShadow( text[i], -font.getStringWidth( text[i]) / 2, i * (mc.fontRendererObj.FONT_HEIGHT + 1) + start, color);
        }
        GlStateManager.disableTexture2D();
        if (type != 2) {
            GlStateManager.popMatrix();
        }

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        GlStateManager.enableColorMaterial();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void prepare() {
        glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        glEnable(GL11.GL_LINE_SMOOTH);
        glEnable(GL32.GL_DEPTH_CLAMP);
    }




        public static void drawCustomString(String text, float x, float y, int color, boolean shadow, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        mc.fontRendererObj.drawString(text, x, y, color, shadow);
        GlStateManager.popMatrix();
    }

    public static void drawRectWithEdge(double x, double y, double width, double height, Color color, Color color2) {
        drawRect(x, y, x + width, y + height, color.getRGB());
        int c = color2.getRGB();
        drawRect(x - 1.0D, y, x, y + height, c);
        drawRect(x + width, y, x + width + 1.0D, y + height, c);
        drawRect(x - 1.0D, y - 1.0D, x + width + 1.0D, y, c);
        drawRect(x - 1.0D, y + height, x + width + 1.0D, y + height + 1.0D, c);
    }


    public static void drawLine(float x, float y, float x2, float y2, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        GL11.glLineWidth(1.0F);
        GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        worldrenderer.begin(1, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x, y, 0.0D).endVertex();
        worldrenderer.pos(x2, y2, 0.0D).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void prepareScissorBox(float x, float y, float x2, float y2) {
        ScaledResolution scale = new ScaledResolution(mc);
        int factor = scale.getScaleFactor();
        GL11.glScissor((int) (x * factor), (int) ((scale.getScaledHeight() - y2) * factor), (int) ((x2 - x) * factor), (int) ((y2 - y) * factor));
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer vertexbuffer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        GlStateManager.color(f, f1, f2, f3);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(left, bottom, 0.0D).endVertex();
        vertexbuffer.pos(right, bottom, 0.0D).endVertex();
        vertexbuffer.pos(right, top, 0.0D).endVertex();
        vertexbuffer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        drawRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        drawRect(x + width, y, x1 - width, y + width, borderColor);
        drawRect(x, y, x + width, y1, borderColor);
        drawRect(x1 - width, y, x1, y1, borderColor);
        drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
    }

    public static void drawBorderRect(double x, double y, double x1, double y1, int color, double lwidth) {
        drawHLine(x, y, x1, y, (float) lwidth, color);
        drawHLine(x1, y, x1, y1, (float) lwidth, color);
        drawHLine(x, y1, x1, y1, (float) lwidth, color);
        drawHLine(x, y1, x, y, (float) lwidth, color);
    }

    public static void drawBorderedRect(final double x, final double y, final double x2, final double y2, final double width, final int color1, final int color2) {
        drawRect(x, y, x2, y2, color2);
        drawBorderRect(x, y, x2, y2, color1, width);
//        glEnable(GL_BLEND);
//        glDisable(GL_TEXTURE_2D);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        glEnable(GL_LINE_SMOOTH);
//        glPushMatrix();
//        glColor(color1);
//        glLineWidth((float) width);
//        glBegin(1);
//        glVertex2d(x, y);
//        glVertex2d(x, y2);
//        glVertex2d(x2, y2);
//        glVertex2d(x2, y);
//        glVertex2d(x, y);
//        glVertex2d(x2, y);
//        glVertex2d(x, y2);
//        glVertex2d(x2, y2);
//        glEnd();
//        glPopMatrix();
//        glEnable(GL_TEXTURE_2D);
//        glDisable(GL_BLEND);
//        glDisable(GL_LINE_SMOOTH);
    }

    public static void drawCustomRoundedRect(float x, float y, float x2, float y2, final float round, final int color) {
        GlStateManager.disableBlend();
        x += (float) (round / 2.0f + 0.0);
        y += (float) (round / 2.0f + 0.0);
        x2 -= (float) (round / 2.0f + 0.0);
        y2 -= (float) (round / 2.0f + 0.0);
        drawCircle(x2 - round / 2.0f, y + round / 2.0f, round, 0, 90, color);
        drawCircle(x + round / 2.0f, y + round / 2.0f, round, 90, 180, color);
        drawCircle(x + round / 2.0f, y2 - round / 2.0f, round, 180, 270, color);
        drawCircle(x2 - round / 2.0f, y2 - round / 2.0f, round, 270, 360, color);
        drawRect(x - round / 2.0f, y + round / 2.0f, x2, y2 - round / 2.0f, color);
        drawRect(x2 + round / 2.0f - round / 2.0f, y + round / 2.0f, x2 + round / 2.0f, y2 - round / 2.0f, color);
        drawRect(x + round / 2.0f, y - round / 2.0f, x2 - round / 2.0f, y + round / 2.0f, color);
        drawRect(x + round / 2.0f, y2 - round / 2.0f + 0.0f, x2 - round / 2.0f, y2 + round / 2.0f + 0.0f, color);
        GlStateManager.disableBlend();
    }

    public static void drawRoundedRect(double x, double y, double x1, double y1, int borderC, int insideC) {
        drawRect(x + 0.5F, y, x1 - 0.5F, y + 0.5F, insideC);
        drawRect(x + 0.5F, y1 - 0.5F, x1 - 0.5F, y1, insideC);
        drawRect(x, y + 0.5F, x1, y1 - 0.5F, insideC);
    }

    public static void drawRoundedRect(int xCoord, int yCoord, int xSize, int ySize, int colour) {
        int width = xCoord + xSize;
        int height = yCoord + ySize;
        drawRect(xCoord + 1, yCoord, width - 1, height, colour);
        drawRect(xCoord, yCoord + 1, width, height - 1, colour);
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getWorldRenderer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawGradient(double x, double y, double x2, double y2, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;

        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;

        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);

        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);

        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;

        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);

        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);

        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
        GL11.glColor4d(255, 255, 255, 255);
    }

    public static void drawCircle(float cx, float cy, float r, int num_segments, int c) {
        GL11.glPushMatrix();
        cx *= 2.0F;
        cy *= 2.0F;
        float f = (c >> 24 & 0xFF) / 255.0F;
        float f1 = (c >> 16 & 0xFF) / 255.0F;
        float f2 = (c >> 8 & 0xFF) / 255.0F;
        float f3 = (c & 0xFF) / 255.0F;
        float theta = (float) (6.2831852D / num_segments);
        float p = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        float x = r *= 2.0F;
        float y = 0.0F;
        enableGL2D();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(2);
        int ii = 0;
        while (ii < num_segments) {
            GL11.glVertex2f(x + cx, y + cy);
            float t = x;
            x = p * x - s * y;
            y = s * t + p * y;
            ii++;
        }
        GL11.glEnd();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        disableGL2D();
        GlStateManager.color(1, 1, 1, 1);
        GL11.glPopMatrix();
    }
    public static void drawCircle(final double x, final double y, final float radius, final int startPi, final int endPi, final int c) {
        final float f = (c >> 24 & 0xFF) / 255.0f;
        final float f2 = (c >> 16 & 0xFF) / 255.0f;
        final float f3 = (c >> 8 & 0xFF) / 255.0f;
        final float f4 = (c & 0xFF) / 255.0f;
        GL11.glColor4f(f2, f3, f4, f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.alphaFunc(516, 0.001f);
        final Tessellator tess = Tessellator.getInstance();
        final WorldRenderer render = tess.getWorldRenderer();
        for (double i = startPi; i < endPi; ++i) {
            final double cs = i * 3.141592653589793 / 180.0;
            final double ps = (i - 1.0) * 3.141592653589793 / 180.0;
            final double[] outer = {Math.cos(cs) * radius, -Math.sin(cs) * radius, Math.cos(ps) * radius, -Math.sin(ps) * radius};
            render.begin(6, DefaultVertexFormats.POSITION);
            render.pos(x + outer[2], y + outer[3], 0.0).endVertex();
            render.pos(x + outer[0], y + outer[1], 0.0).endVertex();
            render.pos(x, y, 0.0).endVertex();
            tess.draw();
        }
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.disableAlpha();
        GL11.glEnable(3553);
    }

    public static void drawHLine(double x, double y, double x1, double y1, float width, int color) {
        float var11 = (color >> 24 & 0xFF) / 255.0F;
        float var6 = (color >> 16 & 0xFF) / 255.0F;
        float var7 = (color >> 8 & 0xFF) / 255.0F;
        float var8 = (color & 0xFF) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        glPushMatrix();
        glLineWidth(width);
        glBegin(GL_LINE_STRIP);
        glVertex2d(x, y);
        glVertex2d(x1, y1);
        glEnd();
        glLineWidth(1);
        glPopMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void drawFilledTriangle(float x, float y, float r, int c, int borderC) {
        enableGL2D();
        glColor(c);
        glEnable(GL_POLYGON_SMOOTH);
        glBegin(GL_TRIANGLES);
        glVertex2f(x + r / 2, y + r / 2);
        glVertex2f(x + r / 2, y - r / 2);
        glVertex2f(x - r / 2, y);
        glEnd();
        glLineWidth(1.3f);
        glColor(borderC);
        glBegin(GL_LINE_STRIP);
        glVertex2f(x + r / 2, y + r / 2);
        glVertex2f(x + r / 2, y - r / 2);
        glEnd();
        glBegin(GL_LINE_STRIP);
        glVertex2f(x - r / 2, y);
        glVertex2f(x + r / 2, y - r / 2);
        glEnd();
        glBegin(GL_LINE_STRIP);
        glVertex2f(x + r / 2, y + r / 2);
        glVertex2f(x - r / 2, y);
        glEnd();
        glDisable(GL_POLYGON_SMOOTH);
        disableGL2D();
    }

    public static void drawCornerRect(double left, double top, double right, double bottom, int color, int otherColor, int corner) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha2 = (otherColor >> 24 & 0xFF) / 255.0F;
        float red2 = (otherColor >> 16 & 0xFF) / 255.0F;
        float green2 = (otherColor >> 8 & 0xFF) / 255.0F;
        float blue2 = (otherColor & 0xFF) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        if (corner == 0) {
            worldrenderer.pos(right, top, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(left, top, zLevel).color(red2, green2, blue2, alpha2).endVertex();
            worldrenderer.pos(left, bottom, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(right, bottom, zLevel).color(red, green, blue, alpha).endVertex();
        } else if (corner == 1) {
            worldrenderer.pos(right, top, zLevel).color(red2, green2, blue2, alpha2).endVertex();
            worldrenderer.pos(left, top, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(left, bottom, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(right, bottom, zLevel).color(red, green, blue, alpha).endVertex();
        } else if (corner == 2) {
            worldrenderer.pos(right, top, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(left, top, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(left, bottom, zLevel).color(red2, green2, blue2, alpha2).endVertex();
            worldrenderer.pos(right, bottom, zLevel).color(red, green, blue, alpha).endVertex();
        } else if (corner == 3) {
            worldrenderer.pos(right, top, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(left, top, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(left, bottom, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(right, bottom, zLevel).color(red2, green2, blue2, alpha2).endVertex();
        } else {
            throw new IndexOutOfBoundsException("corner value must be between 0 and 3");
        }
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawImage(final ResourceLocation image, final int x, final int y, final int width, final int height) {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }


    public static void drawOutlinedBoundingBox(final AxisAlignedBB aa2) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawFilledBox(AxisAlignedBB bb) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        tessellator.draw();
    }
    public static void drawLines(AxisAlignedBB bb) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawBoundingBox(final AxisAlignedBB axisalignedbb) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrender = Tessellator.getInstance().getWorldRenderer();
        worldrender.begin(7, DefaultVertexFormats.POSITION);
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawImg(ResourceLocation loc, double posX, double posY, double width, double height) {
        mc.getTextureManager().bindTexture(loc);
        float f = 1.0F / (float) width;
        float f1 = 1.0F / (float) height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(posX, posY + height, 0.0D).tex((0.0F * f), ((0.0F + (float) height) * f1)).endVertex();
        worldrenderer.pos(posX + width, posY + height, 0.0D).tex(((0.0F + (float) width) * f), ((0.0F + (float) height) * f1)).endVertex();
        worldrenderer.pos(posX + width, posY, 0.0D).tex(((0.0F + (float) width) * f), (0.0F * f1)).endVertex();
        worldrenderer.pos(posX, posY, 0.0D).tex((0.0F * f), (0.0F * f1)).endVertex();
        tessellator.draw();
    }

    public static void makeScissorBox(float x, float y, float x2,float y2) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int factor = scaledResolution.getScaleFactor();
        glScissor(
                (int) (x * factor),
                (int) ((scaledResolution.getScaledHeight() - y2) * factor),
                (int) ((x2 - x) * factor),
                (int) ((y2 - y) * factor)
        );
    }
    public static void glColor(final int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255F;
        float red = (hex >> 16 & 0xFF) / 255F;
        float green = (hex >> 8 & 0xFF) / 255F;
        float blue = (hex & 0xFF) / 255F;
        glColor4f(red, green, blue, alpha);
    }

    public static void glColor(final int hex, final float alpha) {
        float red = (hex >> 16 & 0xFF) / 255F;
        float green = (hex >> 8 & 0xFF) / 255F;
        float blue = (hex & 0xFF) / 255F;
        glColor4f(red, green, blue, alpha);
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        GL11.glDepthMask(false);
        GL11.glHint(3154, 4354);
    }

    public static void post3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return (isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck);
    }

    private static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static double getDiff(double lastI, double i, float ticks, double ownI) { return lastI + (i - lastI) * ticks - ownI; }

    public static Vector3d project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        if (GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, vector)) {
            return new Vector3d((vector.get(0) / scaleFactor), ((Display.getHeight() - vector.get(1)) / scaleFactor), vector.get(2));
        }
        return null;
    }

    public static void drawGradientH(double x, double y, double x2, double y2, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;
        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.resetColor();
    }

    public static void drawGradientV(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;
        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.resetColor();
    }

}