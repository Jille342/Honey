package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventRenderGUI;
import client.event.listeners.EventRenderWorld;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.ui.theme.ThemeManager;
import client.utils.Colors;
import client.utils.PlayerUtils;
import client.utils.TimerUtils;
import client.utils.WVec3;
import client.utils.render.RenderUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.NumberFormat;

public class Tracers extends Module {
    ModeSetting colormode;
    float h;
    BooleanSetting teams;

    public Tracers() {
        super("Tracers", Keyboard.KEY_NONE, Category.RENDER);
    }
    @Override
    public void init() {
        super.init();
        colormode = new ModeSetting("Color Mode", "Team",new String[]{"Rainbow", "Team", "Health","Distance","Normal"});
        teams = new BooleanSetting("Ignore Teams", true);
        addSetting(colormode,teams);
    }


    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            setTag(colormode.getMode());
        }
        if (e instanceof EventRenderWorld) {
            if (h > 255) {
                h = 0;
            }
            h += 0.1;
            GlStateManager.pushMatrix();
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.disableBlend();



            WVec3 eyeVector = (new WVec3(0.0D, 0.0D, 1.0D))
                    .rotatePitch((float)-Math.toRadians(mc.thePlayer.rotationPitch))
                    .rotateYaw((float)-Math.toRadians(mc.thePlayer.rotationYaw));
            for (Entity e1 : mc.theWorld.loadedEntityList) {
                if (e1 == mc.thePlayer) continue;
                if (!(e1 instanceof EntityPlayer)) continue;
                EntityPlayer ent = (EntityPlayer) e1;
                if(PlayerUtils.isOnSameTeam((EntityPlayer) e1) && teams.enable)
                    continue;

                int renderColor = -1;
                switch (colormode.getMode()) {
                    case "Rainbow":
                        final Color color = Color.getHSBColor(h / 255.0f, 0.6f, 1.0f);
                        renderColor = color.getRGB();
                        break;
                    case "Team":
                        String text = e1.getDisplayName().getFormattedText();
                        if (Character.toLowerCase(text.charAt(0)) == '§') {
                            char oneMore = Character.toLowerCase(text.charAt(1));
                            int colorCode = mc.fontRendererObj.getColorCode(oneMore);
                            if (colorCode < 16) {
                                try {
                                    int newColor = colorCode;
                                    renderColor = Colors.getColor((newColor >> 16), (newColor >> 8 & 0xFF), (newColor & 0xFF), 255);
                                } catch (ArrayIndexOutOfBoundsException ignored) {
                                }
                            }
                        } else {
                            renderColor = Colors.getColor(255, 255, 255, 255);
                        }
                        break;
                    case "Health": {
                        float health = ent.getHealth();
                        if (health > 20) {
                            health = 20;
                        }
                        float[] fractions = new float[]{0f, 0.5f, 1f};
                        Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                        float progress = (health * 5) * 0.01f;
                        Color customColor = blendColors(fractions, colors, progress).brighter();
                        renderColor = customColor.getRGB();
                    }
                    break;
                    case "Distance": {
                        float distance = mc.thePlayer.getDistanceToEntity(e1);
                        float[] fractions = new float[]{0f, 0.5f, 1f};
                        Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                        float progress = (distance) * 0.01f;
                        Color customColor = blendColors(fractions, colors, progress).brighter();
                        renderColor = customColor.getRGB();
                    }
                    break;
                }
                RenderUtils.glColorInt(renderColor);
                RenderUtils.drawLine(new Vec3(eyeVector.getXCoord(), mc.thePlayer.getEyeHeight() + eyeVector.getYCoord(), eyeVector.getZCoord()), RenderUtils.renderEntityPos(e1), 1f);
            }

            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        super.onEvent(e);
    }



    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        Color color = null;
        if (fractions != null) {
            if (colors != null) {
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
                } else {
                    throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
                }
            } else {
                throw new IllegalArgumentException("Colours can't be null");
            }
        } else {
            throw new IllegalArgumentException("Fractions can't be null");
        }
        return color;
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


}
