package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventRenderWorld;
import client.event.listeners.EventTick;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.Colors;
import client.utils.RenderingUtils;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class XRay extends Module {
    private static final HashSet blockPosList = new HashSet();
    private static int opacity2 = 160;
    public static BooleanSetting esp;
    public static BooleanSetting diamondOre;

    public static BooleanSetting ironOre;
    public static BooleanSetting emeraldOre;
    public static BooleanSetting goldOre;

    public static BooleanSetting tracers;
    public static NumberSetting opacity;
    public static NumberSetting distance;
    public static int alpha;
    public static BooleanSetting uhc;
    public static BooleanSetting cave;
    public static BooleanSetting teleport;

    public static List blockIdList = Lists.newArrayList((Object[]) new Integer[]{10, 11, 8, 9, 14, 15, 16, 21, 41, 42, 46, 48, 52, 56, 57, 61, 62, 73, 74, 84, 89, 103, 116, 117, 118, 120, 129, 133, 137, 145, 152, 153, 154});

    public XRay() {
        super("XRay", 0, Category.RENDER);
    }

    public void init() {
        super.init();
        distance = new NumberSetting("Distance", 42.0, 12.0, 64.0, 4.0);
        diamondOre = new BooleanSetting("Diamond Ore", true);
        ironOre = new BooleanSetting("Iron Ore", true);
        emeraldOre = new BooleanSetting("Emerald Ore", true);
        goldOre = new BooleanSetting("Gold Ore", true);
        tracers = new BooleanSetting("Tracers", true);
        esp = new BooleanSetting("ESP", true);
        uhc = new BooleanSetting("UHC", false);
        cave = new BooleanSetting("Cave", false);
        teleport = new BooleanSetting("Teleport", true);
        opacity = new NumberSetting("Opacity", 160.0, 0.0, 255.0, 5.0);
        addSetting(distance, diamondOre, ironOre,teleport, emeraldOre, goldOre, tracers, esp, uhc, cave, opacity);
    }

    public void onEvent(Event e) {
        if (e instanceof EventRenderWorld) {
          int radius = (int) distance.getValue();
            for (int x = -radius; x < radius; x++) {
                for (int y = -radius; y < radius; y++) {
                    for (int z = -radius; z < radius; z++) {
                           BlockPos pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                            final Block var4 = this.mc.theWorld.getBlockState(pos).getBlock();
                            if (var4 == Blocks.diamond_ore && diamondOre.isEnabled()) {
                                this.render3D(pos, 0, 255, 255);
                            }
                            if (var4 == Blocks.iron_ore && ironOre.isEnabled()) {
                                this.render3D(pos, 225, 225, 225);
                            }

                            if (var4 == Blocks.emerald_ore && emeraldOre.isEnabled()) {
                                this.render3D(pos, 0, 255, 0);
                            }
                            if (var4 == Blocks.gold_ore && goldOre.isEnable()) {
                                this.render3D(pos, 255, 255, 0);
                            }
                            if(var4 == Blocks.quartz_ore && teleport.isEnabled()){
                                this.render3D(pos, 255, 255, 0);
                            }
                        }
                }
            }
        }

        if(e instanceof EventTick){
            if (alpha != opacity.getValue()) {
                this.mc.renderGlobal.loadRenderers();
                alpha = (int) opacity.getValue();
            }
        }
    }
public void onEnable(){
        super.onEnable();
opacity2 = (int) opacity.getValue();
mc.renderGlobal.loadRenderers();
blockPosList.clear();
}
public void onDisable(){
        blockPosList.clear();
        super.onDisable();
        mc.renderGlobal.loadRenderers();
}

    private void render3D(final BlockPos var1, final int var2, final int var3, final int var4) {
        if (esp.isEnabled()) {
            RenderingUtils.drawSolidBlockESP(var1, Colors.getColor(var2, var3, var4));
        }
        if (tracers.isEnabled()) {
            RenderingUtils.drawLine(var1, Colors.getColor(var2, var3, var4));
        }
    }
    public static int getOpacity() {
        return opacity2;
    }
    public static HashSet<Integer> getBlocks() {
        return (HashSet<Integer>)blockPosList;
    }
    public static boolean containsID(final int id) {
        return blockPosList.contains(id);
    }
    public static int getDistance() {
        return (int) distance.getValue();
    }

}
