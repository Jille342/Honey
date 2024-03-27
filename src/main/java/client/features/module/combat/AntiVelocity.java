//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\1.8.9 MAPPINGS"!

//Decompiled by Procyon!

package client.features.module.combat;

import client.features.module.*;
import client.setting.*;
import client.event.*;
import net.minecraft.network.play.server.*;
import net.minecraft.client.settings.*;
import client.event.listeners.*;
import net.minecraft.client.entity.*;

public class AntiVelocity extends Module
{
    public ModeSetting mode;
    public NumberSetting horizontal;
    public NumberSetting vertical;
    public NumberSetting chance;
    public BooleanSetting clickOnly;

    public AntiVelocity() {
        super("AntiVelocity", 0, Category.COMBAT);
    }

    @Override
    public void init() {
        super.init();
        this.mode = new ModeSetting("Mode", "Simple", new String[] { "Simple", "Legit" });
        this.vertical = new NumberSetting("Vertical", 0.0, 0.0, 100.0, 1.0);
        this.horizontal = new NumberSetting("Horizontal", 0.0, 0.0, 100.0, 1.0);
        this.chance = new NumberSetting("Chance", 90.0, 0.0, 100.0, 1.0);
        this.clickOnly = new BooleanSetting("Click Only", false);
        this.addSetting(this.mode, this.vertical, this.horizontal, this.chance, this.clickOnly);
    }

    @Override
    public void onEvent(final Event<?> e) {
        if (e instanceof EventPacket) {
            final EventPacket a = (EventPacket)e;
            if (this.mode.getMode().equalsIgnoreCase("Simple")) {
                if (this.clickOnly.isEnable() && !AntiVelocity.mc.gameSettings.keyBindAttack.isKeyDown()) {
                    return;
                }
                if (this.chance.getValue() != 100.0) {
                    final double ch = Math.random();
                    if (ch >= this.chance.getValue() / 100.0) {
                        return;
                    }
                }
                if (a.getPacket() instanceof S12PacketEntityVelocity && this.vertical.getValue() == 0.0 && this.horizontal.getValue() == 0.0) {
                    a.setCancelled(true);
                }
                if (a.getPacket() instanceof S27PacketExplosion) {
                    final S27PacketExplosion var8 = (S27PacketExplosion)a.getPacket();
                    if (this.vertical.getValue() == 0.0 && this.horizontal.getValue() == 0.0) {
                        a.setCancelled(true);
                    }
                }
            }
            if (this.mode.getMode().equalsIgnoreCase("Legit")) {
                if (a.getPacket() instanceof S12PacketEntityVelocity) {
                    final S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity)a.getPacket();
                }
                else if (AntiVelocity.mc.currentScreen == null) {
                    final KeyBinding[] array;
                    final KeyBinding[] moveKeys = array = new KeyBinding[] { AntiVelocity.mc.gameSettings.keyBindForward, AntiVelocity.mc.gameSettings.keyBindBack, AntiVelocity.mc.gameSettings.keyBindLeft, AntiVelocity.mc.gameSettings.keyBindRight, AntiVelocity.mc.gameSettings.keyBindSprint, AntiVelocity.mc.gameSettings.keyBindJump };
                    for (final KeyBinding keyBinding : array) {}
                }
            }
        }
        if (e instanceof EventUpdate) {
            this.setTag(this.mode.getMode());
            if(mode.getMode().equalsIgnoreCase("Simple")) {
                if (AntiVelocity.mc.thePlayer.maxHurtTime > 0 && AntiVelocity.mc.thePlayer.hurtTime == AntiVelocity.mc.thePlayer.maxHurtTime) {
                    if (this.clickOnly.isEnable() && !AntiVelocity.mc.gameSettings.keyBindAttack.isKeyDown()) {
                        return;
                    }
                    if (this.chance.getValue() != 100.0) {
                        final double ch2 = Math.random();
                        if (ch2 >= this.chance.getValue() / 100.0) {
                            return;
                        }
                    }
                    if (this.horizontal.getValue() != 100.0) {
                        final EntityPlayerSP thePlayer = AntiVelocity.mc.thePlayer;
                        thePlayer.motionX *= this.horizontal.getValue() / 100.0;
                        final EntityPlayerSP thePlayer2 = AntiVelocity.mc.thePlayer;
                        thePlayer2.motionZ *= this.horizontal.getValue() / 100.0;
                    }
                    if (this.vertical.getValue() != 100.0) {
                        final EntityPlayerSP thePlayer3 = AntiVelocity.mc.thePlayer;
                        thePlayer3.motionY *= this.vertical.getValue() / 100.0;
                    }
                }
            }
        }
    }
}
