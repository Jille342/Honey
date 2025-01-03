package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventTick;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.RotationUtils;
import client.utils.ServerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AimAssist extends Module {








    private final List<EntityLivingBase> validated = new ArrayList<>();
    private EntityLivingBase primary;
    private int breakTick;
    BooleanSetting ignoreTeamsSetting;
    BooleanSetting notHolding;
    NumberSetting aimSpeedSetting;
    NumberSetting rangeSetting;
    BooleanSetting targetMonstersSetting;
    BooleanSetting targetAnimalsSetting;
    NumberSetting fov;
    NumberSetting aimspeed2;
    BooleanSetting targetInvisibles;
    ModeSetting sortmode;

    public AimAssist() {
        super("Aim Assist",  Keyboard.KEY_NONE, Module.Category.COMBAT);
    }
    @Override
    public void init() {
        super.init();
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        this.notHolding = new BooleanSetting("not Holding", false);
        this.aimSpeedSetting = new NumberSetting("AimSpeed", 45, 5.0, 100, 5.0);
        aimspeed2 = new NumberSetting("AimSpeed2",15, 5, 97, 5);
        this.rangeSetting = new NumberSetting("Range", 5.0, 3.0, 8.0, 0.1);
        this.fov = new NumberSetting("FOV", 90.0D, 15.0D, 360.0D, 1.0D);
        this.targetInvisibles = new BooleanSetting("Target Invisibles", true);
        sortmode = new ModeSetting("SortMode", "Distance", new String[]{"Distance", "Angle"});
        addSetting(targetInvisibles, notHolding, ignoreTeamsSetting, aimSpeedSetting, rangeSetting,  targetAnimalsSetting, targetMonstersSetting, fov, aimspeed2, sortmode);
    }

    @Override
    public void onDisable() {
        validated.clear();
        primary = null;
        breakTick = 0;
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventRender2D) {
            setTag(String.valueOf(sortmode.getMode() +" " +validated.size()));
            primary = findTarget();
            if (e.isPost() || primary == null || !canAssist()) {
                return;
            }
            double n = fovFromEntity(primary);
            if (n > 1.0D || n < -1.0D) {
                double complimentSpeed = n * (ThreadLocalRandom.current().nextDouble(aimspeed2.value - 1.47328, aimspeed2.getValue() + 2.48293) / 100);
                double val2 = complimentSpeed + ThreadLocalRandom.current().nextDouble(aimSpeedSetting.getValue() - 4.723847, aimSpeedSetting.getValue());
                float val = (float) (-(complimentSpeed + n / (101.0D - (float) ThreadLocalRandom.current().nextDouble(aimSpeedSetting.getValue() - 4.723847, aimSpeedSetting.getValue()))));
                mc.thePlayer.rotationYaw += val;

            }
        }

    }
    public static double fovFromEntity(Entity en) {
        return ((double)(mc.thePlayer.rotationYaw - fovToEntity(en)) % 360.0D + 540.0D) % 360.0D - 180.0D;
    }

    public double getSensitivity() {
        double sensitivity = mc.gameSettings.mouseSensitivity * 0.3 + 0.2;
        return sensitivity * sensitivity * sensitivity * RandomUtils.nextFloat(2f, 3f);
    }

    private boolean canAssist() {
        if (mc.isGamePaused() || !mc.inGameHasFocus || mc.currentScreen != null) {
            return false;
        }

        if (!notHolding.enable && !Mouse.isButtonDown(0)) {
            return false;
        }

        if (mc.thePlayer.getItemInUseCount() > 0) {
            return false;
        }

        if (mc.objectMouseOver != null) {
            BlockPos p = mc.objectMouseOver.getBlockPos();
            if (p != null) {
                Block bl = mc.theWorld.getBlockState(p).getBlock();
                if (bl instanceof BlockAir || bl instanceof  BlockLiquid) {
                    return true;
                }

            }
        }
        return true;
    }

    private EntityLivingBase findTarget() {
        validated.clear();

        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase && entity != mc.thePlayer) {
                if (entity.isDead || !entity.isEntityAlive() || entity.ticksExisted < 10) {
                    continue;
                }
            if(entity.isInvisible() && !targetInvisibles.enable)
                continue;
                if (!RotationUtils.fov(entity, fov.value))
                    continue;
                double focusRange = mc.thePlayer.canEntityBeSeen(entity) ? rangeSetting.value : 3.5;
                if (mc.thePlayer.getDistanceToEntity(entity) > focusRange) continue;
                if (entity instanceof EntityPlayer) {

                    if(AntiBot.isBot((EntityPlayer) entity))
                     continue;                    if (ignoreTeamsSetting.enable && ServerHelper.isTeammate((EntityPlayer) entity)) {
                        continue;
                    }

                    validated.add((EntityLivingBase) entity);
                } else if (entity instanceof EntityAnimal && targetAnimalsSetting.enable) {
                    validated.add((EntityLivingBase) entity);
                } else if (entity instanceof EntityMob && targetMonstersSetting.enable) {
                    validated.add((EntityLivingBase) entity);
                }
            }
        }

        if (validated.isEmpty()) return null;
        switch (sortmode.getMode()) {
            case "Angle":
        validated.sort(Comparator.comparingDouble(this::calculateYawChangeToDst));
        break;
            case "Distance":
                validated.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) - o2.getDistanceToEntity(mc.thePlayer)));
                break;
        }

        this.validated.sort(Comparator.comparingInt(o -> o.hurtTime));
        return validated.get(0);
    }

    public static float fovToEntity(Entity ent) {
        double x = ent.posX - mc.thePlayer.posX;
        double z = ent.posZ - mc.thePlayer.posZ;
        double yaw = Math.atan2(x, z) * 57.2957795D;
        return (float)(yaw * -1.0D);
    }
    public float calculateYawChangeToDst(Entity entity) {
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        double deg = Math.toDegrees(Math.atan(diffZ / diffX));
        if (diffZ < 0.0 && diffX < 0.0) {
            return (float) MathHelper.wrapAngleTo180_float((float) -(mc.thePlayer.rotationYaw - (90 + deg)));
        } else if (diffZ < 0.0 && diffX > 0.0) {
            return (float) MathHelper.wrapAngleTo180_double(-(mc.thePlayer.rotationYaw - (-90 + deg)));
        } else {
            return (float) MathHelper.wrapAngleTo180_double(-(mc.thePlayer.rotationYaw - Math.toDegrees(-Math.atan(diffX / diffZ))));
        }
    }
}
