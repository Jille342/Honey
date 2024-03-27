package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import client.utils.ChatUtils;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S3APacketTabComplete;

public class DropSwing extends Module {
    public DropSwing() {
        super("DropSwing",0,Category.RENDER);
    }
    public void onEvent(Event event){
        if(event instanceof EventPacket) {
            EventPacket eventPacket = ((EventPacket) event);
            if (eventPacket.isOutgoing()) {
                if (eventPacket.getPacket() instanceof C07PacketPlayerDigging) {
                    C07PacketPlayerDigging packet = (C07PacketPlayerDigging) eventPacket.getPacket();
                    if ((packet.getStatus() == C07PacketPlayerDigging.Action.DROP_ITEM) || (packet.getStatus() == C07PacketPlayerDigging.Action.DROP_ALL_ITEMS)) {
                        mc.thePlayer.swingItem();
                    }
                }
                if(eventPacket.getPacket() instanceof C0EPacketClickWindow) {
                    C0EPacketClickWindow packet  =(C0EPacketClickWindow) eventPacket.getPacket();
                    if(packet.getMode() == 4){
                        mc.thePlayer.swingItem();
                    }
                }
            }
        }

    }

}
