package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import client.utils.ChatUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S18PacketEntityTeleport;

public class TestModule extends Module {
    public TestModule() {
        super("TestModule", 0, Category.MISC);
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventPacket) {
            EventPacket event = ((EventPacket) e);
            if (event.isIncoming()) {
                Packet<?> p = event.getPacket();
                if(p instanceof S18PacketEntityTeleport) {
                    S18PacketEntityTeleport packet = (S18PacketEntityTeleport) event.getPacket();
                        ChatUtils.printChat("Detected!");
                }

            }
        }
    }
}
