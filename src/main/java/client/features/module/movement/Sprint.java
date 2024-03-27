package client.features.module.movement;
import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import scala.tools.nsc.doc.model.Public;

public class Sprint extends Module {
    public static boolean isSprinting = true;
    public Sprint() {

        super("Sprint", Keyboard.KEY_NONE,	Category.MOVEMENT);
    }

    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            if(isSprinting)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            else
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
        }
    }
}

