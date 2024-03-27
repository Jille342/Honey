package client.features.module.render;

import client.event.Event;
import client.features.module.Module;
import client.setting.NumberSetting;

public class ViewClip extends Module {
   public static NumberSetting distance;
    public ViewClip() {
        super("ViewClip", 0, Category.RENDER);
    }
    public void init(){
        super.init();
        distance = new NumberSetting("Distance", 4, 1.0, 10.0,1.0);
    }

}
