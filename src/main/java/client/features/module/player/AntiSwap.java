package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import client.utils.ChatUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.potion.Potion;

public class AntiSwap extends Module {
    public AntiSwap() {
        super("AntiSwap", 0, Category.MISC);
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventPacket) {
            EventPacket event = ((EventPacket) e);
            if (event.isIncoming()) {
                Packet<?> p = event.getPacket();
                if(p instanceof S02PacketChat) {
                    S02PacketChat packet = (S02PacketChat) event.getPacket();
                    if (packet.getChatComponent().toString().contains("swapped you")) {
                        if (mc.thePlayer.onGround && mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getDuration() < 10000 && mc.thePlayer.getAbsorptionAmount()>0) {
                            Potion.moveSlowdown.removeAttributesModifiersFromEntity(mc.thePlayer, mc.thePlayer.getAttributeMap(), 255);
                            mc.thePlayer.setAIMoveSpeed(0.13000001F);
                            for (int i = 0; i < mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getDuration() / 20; ++i) {
                                mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
                            }
                        }
                    }
                }

            }
        }
    }
}
