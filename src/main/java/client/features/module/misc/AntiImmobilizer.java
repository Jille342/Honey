package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

public class AntiImmobilizer extends Module {
    public AntiImmobilizer() {
        super("AntiImmobilizer",0, Category.MISC);
    }
    public static int index;
    public void init(){
        super.init();

    }
    public void onEvent(Event<?> event){

        if(event instanceof EventUpdate) {
            if (mc.thePlayer.isPotionActive(Potion.moveSlowdown) ) {
                index++;
                if (mc.thePlayer.onGround && mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getDuration() < 10000) {
                    Potion.moveSlowdown.removeAttributesModifiersFromEntity(mc.thePlayer, mc.thePlayer.getAttributeMap(), 255);
                    mc.thePlayer.setAIMoveSpeed(0.13000001F);

                    for (int i = 0; i < mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getDuration() / 20; ++i) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                    }
                }

            }else {
                index = 0;
            }
            if (index == 2) {
          //      mc.thePlayer.sendChatMessage("!イモビは嫌いよー^^ #Jill's Mod #XX これお前のお父さんだよ" + Math.random());
            }
        }
    }
}
