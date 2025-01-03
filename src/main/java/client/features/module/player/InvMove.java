package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventTick;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.util.BlockPos;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;

import java.util.Objects;

public class InvMove extends Module {
    ModeSetting mode;
    boolean inInventory = false;
    boolean isClicked;

    public InvMove() {
        super("InvMove", 0, Category.PLAYER);
    }

    @Override
    public void init() {
        super.init();
        mode = new ModeSetting("Mode ", "Normal", new String[]{"Normal", "Hypixel"});
        addSetting(mode);
    }

    public void onEvent(Event<?> event) {
        if (mc.currentScreen instanceof GuiChat) {
            return;
        }
        if (event instanceof EventUpdate) {
            setTag(mode.getMode());
            EventUpdate em = (EventUpdate) event;
            if (em.isPre()) {
            }
        }
        if (event instanceof EventTick) {
            KeyBinding[] moveKeys = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindJump};
            int var5 = 0;
            int var6;
            KeyBinding var7;
            byte var8 = 0;
            if (!this.mode.getMode().equals("Hypixel")) {
                if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
                    for (KeyBinding bind : moveKeys){
                        KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
                    }
                }

                if (!Objects.isNull(mc.currentScreen)) {
                    return;
                }


                var8 = 0;
                    var7 = moveKeys[var8];
                for (KeyBinding bind : moveKeys){
                    if(!Keyboard.isKeyDown(bind.getKeyCode())) {
                        KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                    }
                }

                    var6 = var8 + 1;
            }

            if (this.isClicked && mc.currentScreen != null) {
                for (KeyBinding bind : moveKeys){
                    if(Keyboard.isKeyDown(bind.getKeyCode())) {
                        KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                    }
                }

            } else {
                if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat) && !(mc.currentScreen instanceof GuiChest) && !(mc.currentScreen instanceof GuiCrafting) && !(mc.currentScreen instanceof GuiFurnace) && !(mc.currentScreen instanceof GuiRepair) && !(mc.currentScreen instanceof GuiEditSign) && !(mc.currentScreen instanceof GuiEnchantment)) {

                    var8 = 0;
                    for (KeyBinding bind : moveKeys){
                        KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
                    }
                }

                if (mc.currentScreen==null) {
                    this.isClicked = false;
                    for (KeyBinding bind : moveKeys){
                        if(!Keyboard.isKeyDown(bind.getKeyCode())) {
                            KeyBinding.setKeyBindState(bind.getKeyCode(), bind.isKeyDown());
                        }
                    }


                }

            }
        }
        if (event instanceof EventPacket) {
            EventPacket ep = (EventPacket) event;

            if (this.mode.getMode().equals("Hypixel")) {
                Packet packet = ep.getPacket();
                if (event.isOutgoing()) {
                    if (packet instanceof C0DPacketCloseWindow) {
                        C0DPacketCloseWindow var3 = (C0DPacketCloseWindow) ep.getPacket();
                        if (mc.currentScreen instanceof GuiInventory && this.isClicked) {
                            boolean var4 = true;


                            ep.setCancelled(true);


                            this.isClicked = false;
                        }
                    }

                        if (packet instanceof C16PacketClientStatus) {
                            C16PacketClientStatus clientStatus = (C16PacketClientStatus) packet;
                            if (clientStatus.getStatus() == EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                                ep.setCancelled(true);
                            }
                        }

                        if (packet instanceof C0EPacketClickWindow) {
                            C0EPacketClickWindow clickWindow = (C0EPacketClickWindow) ep.getPacket();
                            if (mc.currentScreen instanceof GuiInventory) {
                                if ((clickWindow.getMode() == 4 || clickWindow.getMode() == 3) && clickWindow.getSlotId() == -999) {
                                    ep.setCancelled(true);
                                } else {
                                    mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                                    this.isClicked = true;
                                }
                            }
                        }
                    }


                }
            }
        }
    }
