
package client.mixin.client;

import client.Client;
import client.event.listeners.EventMoveInput;
import client.utils.ObjectStore;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.lang.annotation.Target;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions {
    @Shadow
    public float moveStrafe;
    @Shadow
    public float moveForward;
    @Shadow
    public boolean jump;
    @Shadow
    public boolean sneak;

    @Inject(method = "updatePlayerMoveState", at = @At("HEAD")
    )
    public void updatePlayerMoveState() {
        final EventMoveInput event = new EventMoveInput(moveForward, moveStrafe, jump, sneak, 0.3D);
        Client.onEvent(event);
        final double sneakMultiplier = event.getSneakSlowDownMultiplier();
        ObjectStore.objects.put("sneakMultiplier", sneakMultiplier);
        ObjectStore.objects.put("moveStrafe", this.moveStrafe);
        ObjectStore.objects.put("moveForward", this.moveForward);
        this.moveForward = event.getForward();
        this.moveStrafe = event.getStrafe();
        this.jump = event.getJump();
        this.sneak = event.getSneak();
    }

    @Inject(
            method = "updatePlayerMoveState",
            at = @At("HEAD"))
    public void updatePlayerMoveStateReturn() {
        final double sneakMultiplier = (double) ObjectStore.objects.get("sneakMultiplier");
        if (this.sneak) {
            this.moveStrafe = (float) ((double) ((Float) ObjectStore.objects.get("moveStrafe")) * sneakMultiplier);
            this.moveForward = (float) ((double) ((Float) ObjectStore.objects.get("moveForward")) * sneakMultiplier);
        }
    }
}
