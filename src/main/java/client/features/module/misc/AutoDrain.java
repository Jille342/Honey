package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.features.module.combat.AntiBot;
import client.setting.NumberSetting;
import client.utils.ChatUtils;
import client.utils.RotationUtils;
import client.utils.ServerHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;

public class AutoDrain extends Module {
    NumberSetting range;
    ArrayList<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
    EntityLivingBase target;
    int current;
    public boolean did;
    NumberSetting fov;

    public AutoDrain() {
        super("AutoDrain", 0, Category.MISC);
    }
    public void init() {
        super.init();
        range= new NumberSetting("Range" ,4.0 ,3,8,0.1);
        this.fov = new NumberSetting("FOV", 20D, 0D, 360D, 1.0D);

        addSetting(range,fov);
    }

    public void onEvent(Event<?> event) {

        if (event instanceof EventUpdate) {
            target = findTarget();
            if(target != null ){
                Scoreboard scoreboard = mc.theWorld.getScoreboard();
                ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
                if(scoreobjective != null) {
                    Score score = scoreboard.getValueFromObjective(target.getName(), scoreobjective);
                    int targetHealth= score.getScorePoints();
                //    String numericString = target.getCustomNameTag().replaceAll("[^0-9]", "");
                    int bestItemIndex = -1;
                 //   int targethealth = Integer.parseInt(numericString);
                    if (targetHealth < 6 && !did) {



                        current = mc.thePlayer.inventory.currentItem;
                        for (int b1 = 0; b1 < 9; b1++) {
                            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[b1];
                            if (itemStack == null) {
                                continue;
                            }
                            if (itemStack.getItem() == Items.dye && itemStack.getDisplayName().contains("READY")) {
                                bestItemIndex = b1;
                            }
                        }
                        if (bestItemIndex != -1) {
                            mc.thePlayer.inventory.currentItem = bestItemIndex;
                            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
did = true;
                        }

                    } else{
KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(),false);
                        did = false;
                    }
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
                if (!RotationUtils.fov(entity, fov.value))
                    continue;
                double focusRange = mc.thePlayer.canEntityBeSeen(entity) ? range.value : 3.5;
                if (mc.thePlayer.getDistanceToEntity(entity) > focusRange) continue;
                if (entity instanceof EntityPlayer) {

                    if (AntiBot.isBot((EntityPlayer) entity))
                        continue;
                    if ( ServerHelper.isTeammate((EntityPlayer) entity)) {
                        continue;
                    }

                    targets.add((EntityLivingBase) entity);
                }
            }
        }
        if (targets.isEmpty()) return null;
        this.targets.sort(Comparator.comparingDouble((entity) -> (double)mc.thePlayer.getDistanceToEntity((Entity) entity)));
        return targets.get(0);
    }
}
