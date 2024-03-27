package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.mixin.IBlock;
import client.setting.BooleanSetting;
import client.setting.KeyBindSetting;
import client.setting.NumberSetting;
import client.utils.ChatUtils;
import client.utils.PacketUtils;
import net.minecraft.block.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;

public class TPPlacer extends Module {
NumberSetting health;
boolean did;
    public TPPlacer() {
        super("TPPlacer",0,Category.PLAYER);
    }
    public void init(){
        super.init();
health= new NumberSetting("Health",6.0,1,19,0.1);
        addSetting(health);
    }
    public void onEvent(Event<?> e) {
        if(e instanceof EventMotion){
            if(mc.thePlayer.getHealth()<= health.getValue() && !(mc.thePlayer.getHealth()== 0) ){
                if(!did ) {
                    BlockPos grassPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    EnumFacing down = EnumFacing.DOWN;
                    Block grass = mc.theWorld.getBlockState(grassPos).getBlock();
                    if (grass != Blocks.air && (grass instanceof BlockFlower || grass instanceof BlockTallGrass || grass instanceof BlockSapling || grass instanceof BlockCrops || grass instanceof BlockStem || grass.isPassable(null, grassPos))) {
                        PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, grassPos, down));
                        PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, grassPos, down));
                        PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, grassPos, down));
                    } else {
                        BlockPos tpPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
                        Vec3 vec3 = new Vec3(mc.thePlayer.posX,mc.thePlayer.posY-1.0,mc.thePlayer.posZ);
                        Block tp = mc.theWorld.getBlockState(tpPos).getBlock();
                        int last = mc.thePlayer.inventory.currentItem;
                        if (tp.isBlockNormalCube()) {
                            int bestItemIndex = -1;
                            for (int i = 0; i < 9; i++) {
                                ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
                                if (stack == null) {
                                    continue;
                                }
                                if (stack.getItem() == Items.quartz) {
                                    bestItemIndex = i;


                                }
                            }
                            if (bestItemIndex != -1) {
                                mc.thePlayer.inventory.currentItem = bestItemIndex;
                                ChatUtils.printChat("Debug:Changed to " + bestItemIndex);
                                EventMotion event = (EventMotion) e;
                                event.setPitch(90.0F);
                                mc.playerController.onPlayerRightClick(mc.thePlayer,mc.theWorld,mc.thePlayer.inventory.getCurrentItem(),tpPos,EnumFacing.DOWN,vec3);
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(),true);
                                did = true;
                            }
                        }

                    }
                }
            } else{
                did= false;
            }
        }
    }
}
