package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;

public class Spammer extends Module {

    public Spammer() {
        super("Spammer",0, Category.MISC);
    }

    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            if (mc.thePlayer.ticksExisted % 15 == 0) {
                mc.thePlayer.sendChatMessage("!#XX #XA #ASD(AntiSetsiDef) #Jill's mod #XX Shotbow Client get it this client → https://discord.gg/antisetsidef");
                        mc.thePlayer.ticksExisted = 0;
            }
        }
}
}