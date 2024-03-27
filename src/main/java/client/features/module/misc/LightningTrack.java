

package client.features.module.misc;


import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import client.utils.ChatUtils;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;

public class LightningTrack extends Module {
    public LightningTrack() {
        super("Lightning Track", 0,Category.MISC);
    }

    public void onEvent(Event event){
        if(event instanceof EventPacket){
            EventPacket eventPacket= (EventPacket) event;
            if(eventPacket.getPacket() instanceof S2CPacketSpawnGlobalEntity){
                S2CPacketSpawnGlobalEntity aneguvet = (S2CPacketSpawnGlobalEntity)eventPacket.getPacket();
                int nuseceye = (int)((float)Math.round((double)aneguvet.func_149051_d() / 32.0D * 100.0D) / 100.0F);
                int efatayud = (int)((float)Math.round((double)aneguvet.func_149050_e() / 32.0D * 100.0D) / 100.0F);
                int var5 = (int)((float)Math.round((double)aneguvet.func_149049_f() / 32.0D * 100.0D) / 100.0F);
                if (aneguvet.func_149053_g() == 1) {
                    ChatUtils.printChat("Lightning struck at " + nuseceye + ", " + efatayud + ", " + var5 + " (" + mc.thePlayer.getDistance(nuseceye, efatayud,var5) + " blocks away)");
                }
            }
        }
    }
}