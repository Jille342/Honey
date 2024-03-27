package client.features.module.combat;

import client.features.module.Module;
import client.utils.ChatUtils;

public class SetRotationNormal2 extends Module {
    public SetRotationNormal2() {
        super("SetRotationNormal2", 0,	Module.Category.COMBAT);
    }
    public void onEnable(){
        super.onEnable();
        KillAura.rotationmode.setModes("Normal2");
        KillAura2.rotationmode.setModes("Normal2");
        KillAura3.rotationmode.setModes("Normal2");
        LegitAura.rotationmode.setModes("Normal2");
        KillAura4.rotationmode.setModes("Normal2");
        ChatUtils.printChat("All KillAuras and LegitAura's Rotation Mode set to Normal2.");
        toggle();
    }
}
