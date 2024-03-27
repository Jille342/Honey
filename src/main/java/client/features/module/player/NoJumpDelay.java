package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.NumberSetting;

public class NoJumpDelay extends Module {
   public static NumberSetting delay;
    public NoJumpDelay() {
        super("NoJumpDelay",0,Category.PLAYER);
    }
    public void init(){
        super.init();
        delay = new NumberSetting("Delay",0.0, 0.0, 10.0, 1.0);
    }
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate)
            setTag(String.valueOf(delay.getValue()));
    }
}
