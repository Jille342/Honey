package client.features.module.movement;
import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeepSprint extends Module {
    public KeepSprint() {

        super("KeepSprint", Keyboard.KEY_NONE,	Category.MOVEMENT);
    }

    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            if(mc.gameSettings.keyBindForward.isKeyDown()){
                mc.thePlayer.setSprinting(true);
            }
        }
    }
}

