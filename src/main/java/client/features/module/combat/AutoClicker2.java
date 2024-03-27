package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventTick;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.concurrent.ThreadLocalRandom;

public class AutoClicker2 extends Module {
//    private final BooleanSetting ignoreFriendsSetting = registerSetting(BooleanSetting.builder()
    //          .name("Ignore Friends")
    //          .value(true)
    //        .build()
    // );


    private final TimeHelper timer = new TimeHelper();
    private boolean breakHeld;

    BooleanSetting leftClickSetting;

    BooleanSetting ignoreTeamsSetting;
    NumberSetting maxCPS;
    public static double cps;
    NumberSetting minCPS;
    BooleanSetting breakBlocks;
    private long lastClick;
    private long leftHold;


    public AutoClicker2() {
        super("Auto Clicker2", Keyboard.KEY_NONE, Category.COMBAT);


    }

    @Override
    public void init() {
        super.init();
        this.leftClickSetting = new BooleanSetting("LeftClick", true);
        this.ignoreTeamsSetting = new BooleanSetting("IgnoreTeams", true);
        this.maxCPS = new NumberSetting("MaxCPS", 7, 2, 20, 1f);
        minCPS = new NumberSetting("MinCPS", 6, 1, 19, 1f);

        breakBlocks = new BooleanSetting("BreakBlocks", true);
        addSetting(leftClickSetting, ignoreTeamsSetting, leftClickSetting, maxCPS, minCPS,breakBlocks);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
            if (Mouse.isButtonDown(0) && shouldClick()) {
                if(breakBlock()) return;
                double speedLeft = 1.0 / ThreadLocalRandom.current().nextDouble(minCPS.getValue() - 0.2, maxCPS.getValue());
                double speedLeft1 = 1.0 / io.netty.util.internal.ThreadLocalRandom.current().nextDouble(minCPS.getValue()- 0.2D, maxCPS.getValue());
                double leftHoldLength = speedLeft1 / io.netty.util.internal.ThreadLocalRandom.current().nextDouble(minCPS.getValue() - 0.02D, maxCPS.getValue());
                if (System.currentTimeMillis() - lastClick > speedLeft * 1000) {
                    lastClick = System.currentTimeMillis();
                    if (leftHold < lastClick){
                        leftHold = lastClick;
                    }
                    int key = mc.gameSettings.keyBindAttack.getKeyCode();
                    KeyBinding.setKeyBindState(key, true);
                    KeyBinding.onTick(key);
                    ClientUtils.setMouseButtonState(0, true);
                } else if (System.currentTimeMillis() - leftHold > leftHoldLength * 1000) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
                   ClientUtils.setMouseButtonState(0, false);
                }

            }

        }
    }



    public boolean breakBlock() {
        if (breakBlocks.isEnabled() && mc.objectMouseOver != null) {
            BlockPos p = mc.objectMouseOver.getBlockPos();

            if (p != null) {
                Block bl = mc.theWorld.getBlockState(p).getBlock();
                if (bl != Blocks.air && !(bl instanceof BlockLiquid)) {
                    if (!breakHeld) {
                        int e = mc.gameSettings.keyBindAttack.getKeyCode();
                        KeyBinding.setKeyBindState(e, true);
                        KeyBinding.onTick(e);
                        breakHeld = true;
                    }
                    return true;
                }
                if(breakHeld) {
                    breakHeld = false;
                }
            }
        }
        return false;
    }


    public boolean shouldClick() {
        if (mc.isGamePaused() || !mc.inGameHasFocus) {
            return false;
        }

        if (mc.thePlayer.getItemInUseCount() > 0) {
            return false;
        }
        return true;
    }
}

