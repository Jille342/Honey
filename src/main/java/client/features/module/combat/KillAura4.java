package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventRenderWorld;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.mixin.client.AccessorMinecraft;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class KillAura4 extends Module {

    NumberSetting CPS;
    BooleanSetting targetMonstersSetting;
    BooleanSetting targetAnimalsSetting;
    BooleanSetting ignoreTeamsSetting;

    NumberSetting rangeSetting;
    ModeSetting sortmode;
    BooleanSetting targetInvisibles;
    public static ModeSetting rotationmode;
    Entity lastTarget;
    NumberSetting minrotationspeed;
    NumberSetting maxrotationspeed;
    BooleanSetting autodisable;
    NumberSetting fov;
    BooleanSetting clickonly;
    BooleanSetting esp;
    EntityLivingBase target = null;
    public KillAura4() {
        super("KillAura4", 0,	Category.COMBAT);
    }

    @Override
    public void init() {
        this.rangeSetting = new NumberSetting("Range", 5.0, 3.0, 8.0, 0.1);
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetInvisibles = new BooleanSetting("Target Invisibles", true);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        rotationmode = new ModeSetting("Rotation Mode", "Normal", new String[]{"Normal", "RotationSpeed", "None", "Normal2"});
        minrotationspeed = new NumberSetting("Min Rotation Speed", 50.0D, 1.0D, 180.0D, 1.0D);
        maxrotationspeed = new NumberSetting("Max Rotation Speed", 60.0D, 1.0D, 180.0D, 1.0D);
        this.CPS = new NumberSetting("CPS", 10, 0, 20, 1f);
        this.fov = new NumberSetting("FOV", 20D, 0D, 360D, 1.0D);
        autodisable = new BooleanSetting("Auto Disable", true);
        clickonly = new BooleanSetting("Click Only", false);
        esp = new BooleanSetting("Target ESP",true);
        sortmode = new ModeSetting("SortMode", "Distance", new String[]{"Distance", "Angle", "HurtTime", "Armor"});
        addSetting(esp,clickonly,autodisable,CPS, targetAnimalsSetting, targetMonstersSetting, ignoreTeamsSetting, sortmode, targetInvisibles,rangeSetting,rotationmode, minrotationspeed,maxrotationspeed,fov);
        super.init();
    }

    ArrayList<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
    private final TimeHelper attackTimer = new TimeHelper();

    @Override
    public void onEvent(Event<?> e) {

        if (e instanceof EventUpdate) {
            setTag(sortmode.getMode() + " " + targets.size());
            target = findTarget();

            if (e.isPre()) {
                if(autodisable.enable) {
                    if ((!mc.thePlayer.isEntityAlive() || (mc.currentScreen != null && mc.currentScreen instanceof GuiGameOver))) {
                        this.toggle();
                        return;
                    }
                    if(mc.thePlayer.ticksExisted <= 1){
                        this.toggle();
                        return;
                    }
                }
                if(clickonly.enable && !mc.gameSettings.keyBindAttack.isKeyDown())
                    return;

                if (target != null) {
                    if (attackTimer.hasReached(calculateTime((int) CPS.value)) && !target.isDead && target.isEntityAlive()) {
                        mc.thePlayer.swingItem();
                        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                        attackTimer.reset();
                    }

                    if (target.isDead || !target.isEntityAlive() || target.ticksExisted < 10)
                        targets.remove(target);
                }
            }

            super.onEvent(e);
        }
        if (e instanceof EventMotion) {
            if(clickonly.enable && !mc.gameSettings.keyBindAttack.isKeyDown())
                return;
            EventMotion event = (EventMotion) e;
            float[] a = {event.yaw ,event.pitch};
            if (target != null && event.isPre()) {
                if (target.isDead || !target.isEntityAlive() || target.ticksExisted < 10 && target ==null)
                    return;
                if(rotationmode.getMode().equals("RotationSpeed")) {
                    float[] neededRotations = RotationUtils.getRotationsRandom((EntityLivingBase) target);
                    float[] limited = RotationUtils.limitAngleChange(a, neededRotations, RandomUtils.nextFloat((float) minrotationspeed.getValue(), (float) maxrotationspeed.getValue()));
                    float[] fixed=  RotationUtils.getFixedRotation(a,limited);

                    event.yaw = fixed[0];
                    event.pitch = fixed[1];
                }
                if(rotationmode.getMode().equalsIgnoreCase("Normal")) {
                    float[] angles = RotationUtils.getRatationsAdvanced((EntityLivingBase) target);
                    float[]  fixed =RotationUtils.getFixedRotation(a,angles);

                    event.setYaw(fixed[0]);
                    event.setPitch(fixed[1]);
                }
                if(rotationmode.getMode().equalsIgnoreCase("Normal2")){
                    float[] angles = RotationUtils.getRotationsEntity((EntityLivingBase) target);
                    float[]  fixed =RotationUtils.fixedSensitivity(angles, 0.1F);
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
    private EntityLivingBase findTarget() {
        targets.clear();

        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase && entity != mc.thePlayer) {
                if (entity.isDead || !entity.isEntityAlive() || entity.ticksExisted < 10) {
                    continue;
                }
                ;
                if (!RotationUtils.fov(entity, fov.value))
                    continue;
                if (entity.isInvisible() && !targetInvisibles.enable)
                    continue;
                double focusRange = mc.thePlayer.canEntityBeSeen(entity) ? rangeSetting.value : 3.5;
                if (mc.thePlayer.getDistanceToEntity(entity) > focusRange) continue;
                if (entity instanceof EntityPlayer) {


                    if(AntiBot.isBot((EntityPlayer) entity))
                        continue;
                    if (ignoreTeamsSetting.enable && ServerHelper.isTeammate((EntityPlayer) entity)) {
                        continue;
                    }

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
                break;
            case"HurtTime":
                this.targets.sort(Comparator.comparingInt(o -> o.hurtTime));
                break;
            case"Armor":
                this.targets.sort(Comparator.comparingInt(o -> o.getTotalArmorValue()));
                break;
        }
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
        target=null;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        targets.clear();
        target=null;
        super.onDisable();
    }
}
