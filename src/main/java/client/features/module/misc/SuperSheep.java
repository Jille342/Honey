package client.features.module.misc;

import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.ModeSetting;
import client.setting.NumberSetting;

public class SuperSheep extends Module {
    public static ModeSetting mode;
    public SuperSheep() {
        super("SuperSheep",0 , Category.MISC);
    }
    public void init(){
        super.init();
      mode = new ModeSetting("Mode", "SuperSheep","SuperSheep","DragonRider");
        addSetting(mode);
    }
    public void onUpdate(EventUpdate eventUpdate){
  setTag(mode.getMode());
    }
}
