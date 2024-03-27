package client.mixin.client;

import client.Client;
import client.event.listeners.EventBlockBounds;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.features.module.render.XRay;
import client.mixin.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.minecraft.block.Block.getIdFromBlock;


@Mixin({Block.class})
public abstract class MixinBlock  {

    @Shadow
    public abstract AxisAlignedBB getCollisionBoundingBox(World p_getCollisionBoundingBox_1_, BlockPos p_getCollisionBoundingBox_2_, IBlockState p_getCollisionBoundingBox_3_);

    @Shadow
    @Final
    protected BlockState blockState;

    /**
     * @author JIll
     * @reason FFF
     */
    @Overwrite
    public void addCollisionBoxesToList(World p_addCollisionBoxesToList_1_, BlockPos p_addCollisionBoxesToList_2_, IBlockState p_addCollisionBoxesToList_3_, AxisAlignedBB p_addCollisionBoxesToList_4_, List<AxisAlignedBB> p_addCollisionBoxesToList_5_, Entity p_addCollisionBoxesToList_6_) {
        AxisAlignedBB axisalignedbb = this.getCollisionBoundingBox(p_addCollisionBoxesToList_1_, p_addCollisionBoxesToList_2_, p_addCollisionBoxesToList_3_);
        EventBlockBounds event = new EventBlockBounds(blockState.getBlock(), p_addCollisionBoxesToList_2_, p_addCollisionBoxesToList_4_);
        Client.onEvent(event);
        if (axisalignedbb != null && p_addCollisionBoxesToList_4_.intersectsWith(axisalignedbb) && !event.isCancelled()) {
            p_addCollisionBoxesToList_5_.add(axisalignedbb);
        }

    }
    /**
     * @author JIll
     * @reason FFF
     */
    @Overwrite
    public EnumWorldBlockLayer getBlockLayer() {

        if (ModuleManager.getModulebyClass(XRay.class).isEnable()) {
            return XRay.containsID(getIdFromBlock((Block) (Object) this)) ? EnumWorldBlockLayer.SOLID : EnumWorldBlockLayer.TRANSLUCENT;
        }
        return EnumWorldBlockLayer.SOLID;
    }



    @Inject(method = "getAmbientOcclusionLightValue", at = { @At("HEAD") }, cancellable = true)
    private void getAmbientOcclusionLightValue(CallbackInfoReturnable<Float> cir) {
        if (ModuleManager.getModulebyClass(XRay.class).isEnable()) {
            cir.setReturnValue(1.0f);
        }
    }
}
