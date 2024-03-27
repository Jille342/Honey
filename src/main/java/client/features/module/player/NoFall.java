package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;

public class NoFall extends Module {

    public NoFall() {
        super("No Fall", 0,Category.PLAYER);
    }

    public void onEvent(Event event){

        if(event instanceof EventUpdate){
           if( mc.thePlayer.fallDistance > 2.0F && !mc.thePlayer.onGround) {
                mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer(true));
            }
        }
    }
}
