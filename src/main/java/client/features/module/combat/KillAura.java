package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventMoveInput;
import client.event.listeners.EventRenderWorld;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.features.module.movement.Sprint;
import client.mixin.client.AccessorMinecraft;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import static net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SPRINTING;

public class KillAura extends Module {

    NumberSetting CPS;
    BooleanSetting targetMonstersSetting;
    BooleanSetting targetAnimalsSetting;
    BooleanSetting ignoreTeamsSetting;

    NumberSetting rangeSetting;
    ModeSetting sortmode;
    BooleanSetting targetInvisibles;
    public static ModeSetting rotationmode;
    NumberSetting minrotationspeed;
    NumberSetting maxrotationspeed;
    BooleanSetting autodisable;
    NumberSetting fov;
    BooleanSetting clickonly;
    BooleanSetting esp;
    EntityLivingBase target = null;
    BooleanSetting keepsprint;
    BooleanSetting oldAttack;
    BooleanSetting moveFix;
    float[] finalRotations;
    public KillAura() {
        super("KillAura", 0,	Category.COMBAT);
    }

    @Override
    public void init() {
        this.rangeSetting = new NumberSetting("Range", 5.0, 3.0, 8.0, 0.1);
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetInvisibles = new BooleanSetting("Target Invisibles", true);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        rotationmode = new ModeSetting("Rotation Mode", "Normal", new String[]{"Normal", "RotationSpeed", "None", "Normal2","DevAAC"});
        minrotationspeed = new NumberSetting("Min Rotation Speed", 50.0D, 1.0D, 180.0D, 1.0D);
        maxrotationspeed = new NumberSetting("Max Rotation Speed", 60.0D, 1.0D, 180.0D, 1.0D);
        this.CPS = new NumberSetting("CPS", 10, 0, 20, 1f);
        this.fov = new NumberSetting("FOV", 20D, 0D, 360D, 1.0D);
        autodisable = new BooleanSetting("Auto Disable", true);
        clickonly = new BooleanSetting("Click Only", false);
        esp = new BooleanSetting("Target ESP",true);
        sortmode = new ModeSetting("SortMode", "Distance", new String[]{"Distance", "Angle", "HurtTime", "Armor"});
        keepsprint = new BooleanSetting("Keep Sprint", true);
        oldAttack = new BooleanSetting("Old Attack",true);
        moveFix = new BooleanSetting("Move Fix", true);
        addSetting(moveFix,oldAttack,keepsprint, esp,clickonly,autodisable,CPS, targetAnimalsSetting, targetMonstersSetting, ignoreTeamsSetting, sortmode, targetInvisibles,rangeSetting,rotationmode, minrotationspeed,maxrotationspeed,fov);
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
                        if(oldAttack.isEnable())
                        mc.thePlayer.swingItem();
                    if(keepsprint.enable) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                    }else {
                      mc.playerController.attackEntity(mc.thePlayer,target);
                      if(target.hurtTime>6){
                          Sprint.isSprinting = false;

                      } else {
                          mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction( mc.thePlayer, STOP_SPRINTING));
                          Sprint.isSprinting =true;
                      }
                    }
                    if(!oldAttack.isEnable())
                        mc.thePlayer.swingItem();
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
                 finalRotations = RotationUtils.limitAngleChange(a, neededRotations, RandomUtils.nextFloat((float) minrotationspeed.getValue(), (float) maxrotationspeed.getValue()));

                } else
                if(rotationmode.getMode().equalsIgnoreCase("Normal")) {
                  finalRotations = RotationUtils.getRatationsAdvanced((EntityLivingBase) target);


                }else
                if(rotationmode.getMode().equalsIgnoreCase("Normal2")){
                   finalRotations = RotationUtils.getRotationsEntity((EntityLivingBase) target);


                } else
                if(rotationmode.getMode().equalsIgnoreCase("DevAAC")){
                    finalRotations = faceTarget(target, Math.max(10, this.getFoVDistance(a[0], target) * 0.8f), 10 + new Random().nextInt(30), false, a[0],a[1]);



                }
                float[]  fixed =RotationUtils.fixedSensitivity(finalRotations, mc.gameSettings.mouseSensitivity);
                event.setYaw(fixed[0]);
                event.setPitch(fixed[1]);
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
    private float getFoVDistance(final float yaw, final Entity e) {
        return ((Math.abs(RotationUtils.getRotationsEntity((EntityLivingBase) e)[0] - yaw) % 360.0f > 180.0f) ? (360.0f - Math.abs(RotationUtils.getRotationsEntity((EntityLivingBase) e)[0] - yaw) % 360.0f) : (Math.abs(RotationUtils.getRotationsEntity((EntityLivingBase) e)[0] - yaw) % 360.0f));
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
    private float[] faceTarget(final Entity target, final float yawSpeed, final float pitchSpeed, final boolean miss, float virtualPitch, float virtualYaw) {
        final double var4 = target.posX - this.mc.thePlayer.posX;
        final double var5 = target.posZ - this.mc.thePlayer.posZ;
        double var7;
        if (target instanceof EntityLivingBase) {
            final EntityLivingBase var6 = (EntityLivingBase) target;
            var7 = var6.posY + var6.getEyeHeight() - (this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight());
        } else {
            var7 = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0 - (this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight());
        }

        Random rnd = new Random();
        final float offset = miss ? (rnd.nextInt(15) * 0.25f + 5.0f) : 0.0f;
        final double var8 = MathHelper.sqrt_double(var4 * var4 + var5 * var5);
        final float var9 = (float) (Math.atan2(var5 + offset, var4) * 180.0 / Math.PI) - 90.0f;
        final float var10 = (float) (-(Math.atan2(var7 - ((target instanceof EntityPlayer) ? 0.5f : 0.0f) + offset, var8) * 180.0 / Math.PI));
        final float pitch = changeRotation(virtualPitch, var10, pitchSpeed);
        final float yaw = changeRotation(virtualYaw, var9, yawSpeed);
        return new float[]{yaw, pitch};
    }

    private float changeRotation(final float var1, final float var2, final float var3) {
        float var4 = MathHelper.wrapAngleTo180_float(var2 - var1);
        if (var4 > var3) {
            var4 = var3;
        }
        if (var4 < -var3) {
            var4 = -var3;
        }
        return var1 + var4;
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
    public void onMoveInput(EventMoveInput eventMoveInput){
        if(moveFix.isEnable())
        MoveUtils.fixMovement(eventMoveInput, finalRotations[0]);
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
        Sprint.isSprinting = true;
        super.onDisable();
    }
}
