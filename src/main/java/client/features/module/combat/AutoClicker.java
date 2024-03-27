package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventTick;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.PlayerHelper;
import client.utils.ServerHelper;
import client.utils.TimeHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MovingObjectPosition;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoClicker extends Module {
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


    public AutoClicker() {
        super("Auto Clicker", Keyboard.KEY_NONE, Category.COMBAT);


    }

    @Override
    public void init() {
        super.init();
        this.leftClickSetting = new BooleanSetting("LeftClick", true);
        this.ignoreTeamsSetting = new BooleanSetting("IgnoreTeams", true);
        this.maxCPS = new NumberSetting("MaxCPS", 7, 2, 20, 1f);
        minCPS = new NumberSetting("MinCPS", 6, 1, 19, 1f);

        addSetting(leftClickSetting, ignoreTeamsSetting, leftClickSetting, maxCPS, minCPS);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
            if (mc.gameSettings.keyBindAttack.isKeyDown() && shouldClick(true)) {
                doLeftClick();

            } else {
                cps =0;
            }

        }
    }


    private void doLeftClick() {
        if (timer.hasReached(calculateTime(minCPS.getValue(), maxCPS.getValue()))) {
            timer.reset();
            legitAttack();
        }
    }

    public void legitAttack() {
        mc.thePlayer.swingItem();

        if (mc.objectMouseOver == null || mc.thePlayer.isRiding()) {
            return;
        }

        if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
           mc.playerController.attackEntity(mc.thePlayer,mc.objectMouseOver.entityHit);
        } else if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockpos = mc.objectMouseOver.getBlockPos();
            if (!mc.theWorld.isAirBlock(blockpos)) {
                mc.playerController.clickBlock(blockpos, mc.objectMouseOver.sideHit);
            }
        }
    }


    private double calculateTime(double mincps, double maxcps) {
        if (mincps > maxcps)
            mincps = maxcps;
        cps = (client.utils.RandomUtils.nextInt((int) mincps, (int) maxcps) + client.utils.RandomUtils.nextInt(-3,3));
        if (cps > maxcps)
            cps = (int)maxcps;

        return   ((Math.random() * (1000 / (cps - 2) - 1000 / cps + 1)) + 1000 / cps);

    }

    public boolean shouldClick(boolean left) {
        if (mc.isGamePaused() || !mc.inGameHasFocus) {
            return false;
        }

        if (mc.thePlayer.getItemInUseCount() > 0) {
            return false;
        }
        return true;
    }
}

