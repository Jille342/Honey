package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventRenderWorld;
import client.features.module.Module;
import client.mixin.client.AccessorMinecraft;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.Colors;
import client.utils.font.CFontRenderer;
import client.utils.font.Fonts;
import com.google.gson.internal.bind.JsonTreeReader;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import org.lwjgl.opengl.GL11;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.awt.*;

import static java.awt.Color.RED;

public class NameTags extends Module {
    private final CFontRenderer font = Fonts.default18;
    BooleanSetting renderSelf;
    BooleanSetting showItems;
    BooleanSetting ench;
    BooleanSetting health;
    BooleanSetting smartScaling;
    NumberSetting scaling;
    NumberSetting factor;
    BooleanSetting strengthCheck;
    public NameTags() {
        super("NameTags", 0, Category.RENDER);
    }

    public void init() {
        renderSelf = new BooleanSetting("Render Self", false);
        showItems = new BooleanSetting("Show Items", true);
        ench = new BooleanSetting("show Enchant", true);
        health = new BooleanSetting("Health", true);
        smartScaling= new BooleanSetting("Smart Scaling", true);
        scaling = new NumberSetting("Size", 1.0F,1.0, 10, 1);
        factor = new NumberSetting("Factor", 1.0F,1.0, 10, 1);
        strengthCheck = new BooleanSetting("Strength Check", true);
        addSetting(renderSelf, showItems, ench,smartScaling, health, scaling, factor, strengthCheck);
        super.init();
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventRenderWorld) {


            for (Entity entity : mc.theWorld.getLoadedEntityList()) {
                if (entity instanceof EntityPlayer) {
                    if (entity == null) continue;
                    if (((EntityPlayer) entity).deathTime > 0) continue;
                    if (entity == mc.thePlayer && !renderSelf.enable) continue;
                    double x = ((entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ((AccessorMinecraft) mc).getTimer().renderPartialTicks
                            - mc.getRenderManager().viewerPosX));
                    double y = ((entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ((AccessorMinecraft) mc).getTimer().renderPartialTicks
                            - mc.getRenderManager().viewerPosY));
                    double z = ((entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ((AccessorMinecraft) mc).getTimer().renderPartialTicks
                            - mc.getRenderManager().viewerPosZ));
                    renderNameTag((EntityPlayer) entity, x, y, z, ((AccessorMinecraft) mc).getTimer().renderPartialTicks);

                }
            }
        }
    }

    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        double tempY = y;
        tempY += player.isSneaking() ? 0.5 : 0.7;
        Entity camera = mc.getRenderViewEntity();
        assert (camera != null);
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = this.interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = this.interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = this.interpolate(camera.prevPosZ, camera.posZ, delta);
        String displayTag = this.getDisplayTag(player);
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = font.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + (double) this.scaling.getValue() * (distance * (double) this.factor.getValue())) / 1000.0;
        if (distance <= 8.0 && this.smartScaling.enable) {
            scale = 0.0245;
        }
        if (!this.smartScaling.enable) {
            scale = (double) this.scaling.getValue() / 100.0;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();

        GL11.glTranslatef((float) x, (float) tempY + 1.4f, (float) z);
        GlStateManager.disableBlend();
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glDisable(GL11.GL_TEXTURE_2D);

if(player.getActivePotionEffect(Potion.damageBoost).getIsAmbient() && strengthCheck.isEnable()) {
    drawRect(-width - 2, -(mc.fontRendererObj.FONT_HEIGHT + 1), (float) width + 2.0f, 1.5f, 0x90000000);
} else {
    drawRect(-width - 2, -(mc.fontRendererObj.FONT_HEIGHT + 1), (float) width + 2.0f, 1.5f, new Color(255, 0, 0, 255).getRGB());
}
        ItemStack renderMainHand = player.getHeldItem();
        if (this.showItems.enable) {
            GlStateManager.pushMatrix();
            int xOffset = -8;
            for (ItemStack stack : player.inventory.armorInventory) {
                if (stack == null) continue;
                xOffset -= 8;
            }
            xOffset -= 8;

            xOffset += 16;
            for (ItemStack stack : player.inventory.armorInventory) {
                if (stack == null) continue;
                this.renderItemStack(stack, xOffset, -26);
                xOffset += 16;
            }
            this.renderItemStack(renderMainHand, xOffset, -26);
            GlStateManager.popMatrix();
        }
        font.drawStringWithShadow(displayTag, -width, -(mc.fontRendererObj.FONT_HEIGHT - 1), Colors.getColor(255,255,255));
        mc.entityRenderer.disableLightmap();
        GL11.glLineWidth(1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);

        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

    }
    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }

    private String getDisplayTag(EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();
        name = name.replace(player.getDisplayName().getFormattedText(), "\247f" + player.getDisplayName().getFormattedText());
        if (name.contains(mc.getSession().getUsername())) {
            name = "You";
        }
        if (!this.health.enable) {
            return name;
        }
        if(health.enable)
        {
            float health = player.getHealth();
            name = name + health;
        }
        return name ;
    }
    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, y);
        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GL11.glScaled(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        if (ench.enable)
            this.renderEnchantmentText(stack, x, y);
        GL11.glScaled(2.0f, 2.0f, 2.0f);
        GlStateManager.enableBlend();
    }

    public void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }


    private void renderEnchantmentText(ItemStack stack, int x, int y) {
        int enchantmentY = y - 8;
if(stack == null)
    return;
        NBTTagList enchants = stack.getEnchantmentTagList();
        if(enchants ==null)
            return;
        for (int index = 0; index < enchants.tagCount(); index++) {
            short id = enchants.getCompoundTagAt(index).getShort("id");
            short level = enchants.getCompoundTagAt(index).getShort("lvl");
            Enchantment enc = Enchantment.getEnchantmentById(id);
            if (enc == null) continue;
            String encName = enc.getTranslatedName(level).substring(0, 1).toLowerCase();
            encName = encName + level;
            font.drawStringWithShadow(encName, x * 2, enchantmentY, -1);
            enchantmentY -= 8;
        }

    }
}
