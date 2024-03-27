package client.features.module.combat;
import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.features.module.movement.Flight;
import client.features.module.movement.Speed;
import client.setting.ModeSetting;
import client.utils.PacketUtils;
import client.utils.RandomUtils;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Criticals extends Module {
    ModeSetting mode;
    public Criticals() {

        super("Criticals", Keyboard.KEY_NONE,	Category.COMBAT);
    }
    static double[] y1 = {0.104080378093037, 0.105454222033912, 0.102888018147468, 0.099634532004642};
    public void init() {
        super.init();
        mode = new ModeSetting("Mode", "ACP", new String[]{"Packet", "ACP", "NCP","Test","Hop","Ground","Matrix","MatrixPacket","Acrobat"});
        addSetting(mode);
    }
boolean isAttacked;

    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate)
            setTag(mode.getMode());

        if (e instanceof EventPacket) {
            EventPacket event = ((EventPacket)e);
            if(event.isOutgoing()) {
                if (event.getPacket() instanceof C02PacketUseEntity) {
                    C02PacketUseEntity packetUseEntity = (C02PacketUseEntity) event.getPacket();
                    if (packetUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK  && mc.thePlayer.onGround) {
                        switch (mode.getMode()) {
                            case("ACP"):
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.28E-7D, mc.thePlayer.posZ, false));
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                                break;
                            case("Packet"):
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625D, mc.thePlayer.posZ, false));
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                                break;
                            case "NCP":
                                double random = Math.random() * 0.0003;
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.06252F + random, mc.thePlayer.posZ, true));
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + random, mc.thePlayer.posZ, false));
                                break;
                            case"Test":
                                double[] edit = new double[]{0.075 + ThreadLocalRandom.current().nextDouble(0.008) * (new Random().nextBoolean() ? 0.96 : 0.97) + mc.thePlayer.ticksExisted % 0.0215 * 0.92,
                                        (new Random().nextBoolean() ? 0.010634691223 : 0.013999777) * (new Random().nextBoolean() ? 0.95 : 0.96) * y1[new Random().nextInt(y1.length)] * 9.5f};
                                for (double offset : edit) {
                                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (offset * (1 + getRandomDoubleInRange(-0.005, 0.005))), mc.thePlayer.posZ, false));
                                }
                                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
break;
                            case"Hop":if(mc.thePlayer.onGround) {
                                mc.thePlayer.motionY = 0.1D;



                            }
                                break;
                            case"Ground":
                                 isAttacked = true;
                                break;
                            case"Matrix":
                                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.00000000001, mc.thePlayer.posZ, false));

                                break;
                            case"MatrixPacket":
                                mc.thePlayer.onGround = false;
                                double yMotion = 1.0E-12;
                                mc.thePlayer.fallDistance = (float) yMotion;
                                mc.thePlayer.motionY = yMotion;
                                mc.thePlayer.isCollidedVertically = true;
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + RandomUtils.nextFloat(0.00000001F, 0.0000004F), mc.thePlayer.posZ, false));
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + RandomUtils.nextFloat(0.00000001F, 0.0000002F), mc.thePlayer.posZ, false));
                                break;
                        }
                    } else{
                        isAttacked = false;
                    }
                }
            }
        }
        if(e instanceof EventMotion) {
         if(   e.isPre()) {

             EventMotion event = (EventMotion) e;
             if(mode.getMode().equalsIgnoreCase("Ground") && mc.gameSettings.keyBindJump.isKeyDown() && !mc.thePlayer.onGround){
                 event.setOnGround(false);
             }
            }
        }
    }
    public boolean isFlying(){
        return (mc.theWorld.getCollidingBoundingBoxes((Entity)mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -0.01D, 0.0D)).isEmpty());
    }
    public static double getRandomDoubleInRange(double minDouble, double maxDouble) {
        return minDouble >= maxDouble ? minDouble : new Random().nextDouble() * (maxDouble - minDouble) + minDouble;
    }
}

