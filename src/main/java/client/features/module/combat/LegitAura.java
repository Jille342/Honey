package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventMoveInput;
import client.event.listeners.EventRenderWorld;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.mixin.client.AccessorMinecraft;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class LegitAura extends Module {

    NumberSetting CPS;
    BooleanSetting targetMonstersSetting;
    BooleanSetting targetAnimalsSetting;
    BooleanSetting ignoreTeamsSetting;

    NumberSetting rangeSetting;
    ModeSetting sortmode;
    BooleanSetting targetInvisibles;
    NumberSetting fov;
    BooleanSetting hitThroughWalls;
    BooleanSetting clickOnly;
    BooleanSetting notAimingOnly;
    BooleanSetting esp;
    BooleanSetting checkpitch;
    BooleanSetting oldAttack;
    float[] finalRotations;
    BooleanSetting moveFix;
  public static  ModeSetting rotationmode;
    public LegitAura() {
        super("LegitAura", 0,	Category.COMBAT);
    }

    @Override
    public void init() {
        this.rangeSetting = new NumberSetting("Range", 3.0, 0, 4.2, 0.1);
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetInvisibles = new BooleanSetting("Target Invisibles", false);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        this.CPS = new NumberSetting("CPS", 10, 0, 20, 1f);
        sortmode = new ModeSetting("SortMode", "Angle", new String[]{"Distance", "Angle"});
        rotationmode = new ModeSetting("Rotation Mode", "Normal", new String[]{"None", "Normal","Normal2"});
        this.fov = new NumberSetting("FOV", 20D, 0D, 360D, 1.0D);
        hitThroughWalls = new BooleanSetting("Hit Through Walls", false);
        clickOnly = new BooleanSetting("Click Only", true);
        notAimingOnly = new BooleanSetting("Not Aiming Only", true);
        esp = new BooleanSetting("Target ESP",true);
        checkpitch = new BooleanSetting("Check Pitch", false);
        oldAttack = new BooleanSetting("Old Attack",true);
        moveFix = new BooleanSetting("Move Fix", true);
        addSetting(moveFix,oldAttack,checkpitch,esp,rotationmode,CPS, targetAnimalsSetting, targetMonstersSetting, ignoreTeamsSetting, sortmode, targetInvisibles,fov,hitThroughWalls,rangeSetting,clickOnly, notAimingOnly);
        super.init();
    }

    ArrayList<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
    private final TimeHelper attackTimer = new TimeHelper();
    EntityLivingBase target = null;

    @Override
    public void onEvent(Event<?> e) {

        if (e instanceof EventUpdate) {
             target = findTarget();
             if(checkpitch.isEnable() && !RayTraceUtils.isMouseOver(mc.thePlayer.rotationYaw,mc.thePlayer.rotationPitch,target,(float) rangeSetting.getValue())){
                target = null;
            }

            setTag(sortmode.getMode() + " " + targets.size());
            if (target != null) {
                float diff = RotationUtils.calculateYawChangeToDst(target);
                if (!mc.thePlayer.isUsingItem() && !(mc.currentScreen instanceof GuiInventory)) {

                    if (e.isPre()) {


                        if (target!=null) {
                            if (attackTimer.hasReached(calculateTime((int) CPS.value)) && !target.isDead && target.isEntityAlive()) {
                                if(!oldAttack.isEnable())
                                    mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                                mc.thePlayer.swingItem();
                                if(oldAttack.isEnable())
                                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                                attackTimer.reset();
                            }

                            if (target.isDead || !target.isEntityAlive() || target.ticksExisted < 10)
                                targets.remove(target);
                        } else {
                            finalRotations = new float[]{
                                    mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch
                            };
                        }
                    }

                    super.onEvent(e);
                }
            }
        }
        if (e instanceof EventMotion) {

            if (target != null) {
                float diff = RotationUtils.calculateYawChangeToDst(target);
                EventMotion event = (EventMotion) e;
                if (!targets.isEmpty()) {
                    if (!isNotAiming(target) && notAimingOnly.enable)
                        return;

                    if (target.isDead || !target.isEntityAlive() || target.ticksExisted < 10 || target == null)
                        return;

                    if (rotationmode.getMode().equalsIgnoreCase("Normal")) {
                        finalRotations = RotationUtils.getRotationsRandom((EntityLivingBase) target);
                    } else
if(rotationmode.getMode().equalsIgnoreCase("Normal2")){
    finalRotations = RotationUtils.getRotationsEntity((EntityLivingBase) target);

}
                    float[] fixed =RotationUtils.fixedSensitivity(finalRotations, mc.gameSettings.mouseSensitivity);
                    event.setYaw(fixed[0]);
                    event.setPitch(fixed[1]);
                }
            }

        }
        if(e instanceof EventRenderWorld) {
            if (esp.enable) {
                if (target != null) {
                    int color = new Color(0, 255, 0, 255).getRGB();
                    if (target.hurtTime != 0) {
                        color = -6750208;
                    }
                    RenderingUtils.drawEsp(target, ((AccessorMinecraft) mc).getTimer().renderPartialTicks, color, 0 );
                }
            }
        }
    }
    public void onMoveInput(EventMoveInput eventMoveInput){
        if(moveFix.isEnable())
            MoveUtils.fixMovement(eventMoveInput, finalRotations[0]);
    }

    public boolean isNotAiming(EntityLivingBase entity) {
        double diffX = entity.posX  - Minecraft.getMinecraft().thePlayer.posX;
        double diffY = entity.posY - (Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight());
        double diffZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        float currentYaw = mc.thePlayer.rotationYaw;
        float currentPitch = mc.thePlayer.rotationPitch;
        double dist = (double)MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float max = 5.0F;
        boolean aim = false;
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        if (MathHelper.wrapAngleTo180_float(yaw - currentYaw) > max * 2.0F) {
            aim = true;
        } else if (MathHelper.wrapAngleTo180_float(yaw - currentYaw) < -max * 2.0F) {
            aim = true;
        }

        if (MathHelper.wrapAngleTo180_float(pitch - currentPitch) > max * 4.0F) {
            aim = true;
        } else if (MathHelper.wrapAngleTo180_float(pitch - currentPitch) < -max * 4.0F) {
            aim = true;
        }
        return aim;
    }
    private EntityLivingBase findTarget() {
        targets.clear();

        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase && entity != mc.thePlayer) {
                if (entity.isDead || !entity.isEntityAlive() || entity.ticksExisted < 10) {
                    continue;
                }
                if(clickOnly.enable && !mc.gameSettings.keyBindAttack.isKeyDown())
                    continue;
                if (entity.isInvisible() && !targetInvisibles.enable)
                    continue;
if(mc.thePlayer.isUsingItem())
    continue;

                if (!RotationUtils.fov(entity, fov.value))
                    continue;
                if(!mc.thePlayer.canEntityBeSeen(entity)&& !hitThroughWalls.isEnable())
                    continue;
                double focusRange = rangeSetting.value ;
                if (mc.thePlayer.getDistanceToEntity(entity) > focusRange) continue;
                if (entity instanceof EntityPlayer) {

                    if (ignoreTeamsSetting.enable && ServerHelper.isTeammate((EntityPlayer) entity)) {
                        continue;
                    }
                    if(AntiBot.isBot((EntityPlayer) entity))
                        continue;

                    targets.add((EntityLivingBase) entity);
                } else if (entity instanceof EntityAnimal && targetAnimalsSetting.enable) {
                    targets.add((EntityLivingBase) entity);
                } else if (entity instanceof EntityMob && targetMonstersSetting.enable) {
                    targets.add((EntityLivingBase) entity);
                }
            }
        }

        if (targets.isEmpty()) return null;
        switch(sortmode.getMode()) {
            case "Distance":
                this.targets.sort(Comparator.comparingDouble((entity) -> (double)mc.thePlayer.getDistanceToEntity((Entity) entity)));
                break;
            case"Angle":
                targets.sort(Comparator.comparingDouble(RotationUtils::getYawChangeToEntity));
        }
        this.targets.sort(Comparator.comparingInt(o -> o.hurtTime));
        return (EntityLivingBase) targets.get(0);
    }

    private long calculateTime(int cps) {
        return (long) ((Math.random() * (1000 / (cps - 2) - 1000 / cps + 1)) + 1000 / cps);
    }

    public float calculateYawChangeToDst(Entity entity) {
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        double deg = Math.toDegrees(Math.atan(diffZ / diffX));
        if (diffZ < 0.0 && diffX < 0.0) {
            return (float) MathHelper.wrapAngleTo180_double(-(mc.thePlayer.rotationYaw - (90 + deg)));
        } else if (diffZ < 0.0 && diffX > 0.0) {
            return (float) MathHelper.wrapAngleTo180_double(-(mc.thePlayer.rotationYaw - (-90 + deg)));
        } else {
            return (float) MathHelper.wrapAngleTo180_double(-(mc.thePlayer.rotationYaw - Math.toDegrees(-Math.atan(diffX / diffZ))));
        }
    }

    @Override
    public void onEnable() {
        targets.clear();
        target =null;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        targets.clear();
        target =null;
        super.onDisable();
    }
}
