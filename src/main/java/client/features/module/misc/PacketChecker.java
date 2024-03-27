package client.features.module.misc;

import client.Client;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import client.utils.ChatUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;

public class PacketChecker extends Module {

    public PacketChecker() {
        super("PacketChecker", 0, Category.MISC);
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventPacket) {
            EventPacket event = ((EventPacket)e);
            if(event.isIncoming()) {
                Packet<?> p = event.getPacket();

                if(p instanceof S02PacketChat) {
                    S02PacketChat packet = (S02PacketChat) event.getPacket();
                    if (packet.toString().contains("")) {
                        ChatUtils.printChat("Debug:" + packet.getChatComponent().toString());
                    }
                }

            }
        }
        super.onEvent(e);
    }

}
