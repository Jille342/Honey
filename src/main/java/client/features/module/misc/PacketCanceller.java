package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import client.utils.ChatUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

public class PacketCanceller extends Module {
    public PacketCanceller() {
        super("PacketCanceller", 0, Category.MISC);
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventPacket) {
            EventPacket event = ((EventPacket) e);
            if (event.isIncoming()) {
                Packet<?> p = event.getPacket();
                if(p instanceof S02PacketChat) {
                    S02PacketChat packet = (S02PacketChat) event.getPacket();
                    if (packet.getChatComponent().toString().contains("No player was found")) {
                      event.setCancelled(true);
                    }
                }

            }
        }
    }
}
