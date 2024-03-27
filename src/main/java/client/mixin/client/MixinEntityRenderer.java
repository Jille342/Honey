package client.mixin.client;

import client.Client;
import client.event.listeners.EventCameraTransform;
import client.event.listeners.EventRenderWorld;
import client.features.module.ModuleManager;
import client.features.module.render.ViewClip;
import client.mixin.interfaces.IEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
    Minecraft mc = Minecraft.getMinecraft();
    @Shadow
    private Entity pointedEntity;
    @Shadow protected abstract void setupCameraTransform(float partialTicks, int pass);

    @Shadow private boolean cloudFog;

    @Shadow private float thirdPersonDistanceTemp;

    @Shadow private float thirdPersonDistance;

    @Inject(method = {"renderWorldPass"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;debugView:Z")})
    private void renderWorldPassPre(int pass, float partialTicks, long finishTimeNano, CallbackInfo paramCallbackInfo) {
        EventRenderWorld e = new EventRenderWorld(partialTicks);
        Client.onEvent(e);
    }
    @Inject(method = {"setupCameraTransform"}, at = {@At(value = "HEAD")})
    private void setupCameraTransform(float p_78479_1_, int p_78479_2_, CallbackInfo ci) {
        EventCameraTransform e = new EventCameraTransform(p_78479_1_, p_78479_2_);
        Client.onEvent(e);
    }
    /**
     * @author JIll
     * @reason CAMERA
     */
    @Overwrite
    private void orientCamera(float partialTicks) {
        Entity entity = this.mc.getRenderViewEntity();
        float f = entity.getEyeHeight();
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
        double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + (double) f;
        double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
        if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPlayerSleeping()) {
            f = (float) ((double) f + 1.0D);
            GlStateManager.translate(0.0F, 0.3F, 0.0F);
            if (!this.mc.gameSettings.debugCamEnable) {
                BlockPos blockpos = new BlockPos(entity);
                IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                if (block == Blocks.bed) {
                    int j = iblockstate.getValue(BlockBed.FACING).getHorizontalIndex();
                    GlStateManager.rotate((float) (j * 90), 0.0F, 1.0F, 0.0F);
                }
                GlStateManager.rotate(
                        entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F,
                        0.0F, -1.0F, 0.0F);
                GlStateManager.rotate(
                        entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks,
                        -1.0F, 0.0F, 0.0F);
            }
        } else if (this.mc.gameSettings.thirdPersonView > 0) {
            double d3 = this.thirdPersonDistanceTemp
                    + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * partialTicks;

            if (this.mc.gameSettings.debugCamEnable) {
                GlStateManager.translate(0.0F, 0.0F, -ViewClip.distance.getValue());
            } else {
                float f1 = entity.rotationYaw;
                float f2 = entity.rotationPitch;
                if (this.mc.gameSettings.thirdPersonView == 2) {
                    f2 += 180.0F;
                }
                double d4 = (double) (-MathHelper.sin(f1 / 180.0F * (float) Math.PI)
                        * MathHelper.cos(f2 / 180.0F * (float) Math.PI)) * d3;
                double d5 = (double) (MathHelper.cos(f1 / 180.0F * (float) Math.PI)
                        * MathHelper.cos(f2 / 180.0F * (float) Math.PI)) * d3;
                double d6 = (double) (-MathHelper.sin(f2 / 180.0F * (float) Math.PI)) * d3;

                for (int i = 0; i < 8; ++i) {
                    float f3 = (float) ((i & 1) * 2 - 1);
                    float f4 = (float) ((i >> 1 & 1) * 2 - 1);
                    float f5 = (float) ((i >> 2 & 1) * 2 - 1);
                    f3 = f3 * 0.1F;
                    f4 = f4 * 0.1F;
                    f5 = f5 * 0.1F;
                    MovingObjectPosition movingobjectposition = this.mc.theWorld
                            .rayTraceBlocks(new Vec3(d0 + (double) f3, d1 + (double) f4, d2 + (double) f5), new Vec3(
                                    d0 - d4 + (double) f3 + (double) f5, d1 - d6 + (double) f4, d2 - d5 + (double) f5));
                    if (movingobjectposition != null) {
                        double d7 = movingobjectposition.hitVec.distanceTo(new Vec3(d0, d1, d2));
                        if ((d7 < d3) && (!ModuleManager.getModulebyClass(ViewClip.class).isEnable())) {
                            d3 = d7;
                        }
                    }

                }
                if (this.mc.gameSettings.thirdPersonView == 2) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }
                GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, -ViewClip.distance.getValue());
                GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        } else

        {
            GlStateManager.translate(0.0F, 0.0F, -0.1F);
        }
        if (!this.mc.gameSettings.debugCamEnable) {

                GlStateManager.rotate(
                        entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks,
                        1.0F, 0.0F, 0.0F);

            if (entity instanceof EntityAnimal) {
                EntityAnimal entityanimal = (EntityAnimal) entity;
                GlStateManager.rotate(entityanimal.prevRotationYawHead
                                + (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F,
                        0.0F, 1.0F, 0.0F);
            } else {


                    GlStateManager.rotate(entity.prevRotationYaw
                            + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);

            }
        }
        GlStateManager.translate(0.0F, -f, 0.0F);
        d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
        d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + (double) f;
        d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
        this.cloudFog = this.mc.renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);
    }

}
