package client.features.module.combat;

import client.features.module.Module;
import client.utils.ChatUtils;

public class SetRotationNone extends Module {
    public SetRotationNone() {
        super("SetRotationNone", 0,	Module.Category.COMBAT);
    }
    public void onEnable(){
        super.onEnable();
        KillAura.rotationmode.setModes("None");
        KillAura2.rotationmode.setModes("None");
        KillAura3.rotationmode.setModes("None");
        LegitAura.rotationmode.setModes("None");
        KillAura4.rotationmode.setModes("None");
        ChatUtils.printChat("All KillAuras and LegitAura's Rotation Mode set to None.");
        toggle();
    }
}
