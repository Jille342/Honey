package client.utils;

import client.features.module.ModuleManager;
import client.mixin.client.AccessorMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.input.Mouse;

import java.nio.ByteBuffer;

public class ClientUtils {

	protected static Minecraft mc = Minecraft.getMinecraft();



	public static void setTimer(float d) {
		((AccessorMinecraft) mc).getTimer().timerSpeed = d;
	}

	public static void setMouseButtonState(int mouseButton, boolean held) {
		MouseEvent m = new MouseEvent();

		ObfuscationReflectionHelper.setPrivateValue(MouseEvent.class, m, mouseButton, "button");
		ObfuscationReflectionHelper.setPrivateValue(MouseEvent.class, m, held, "buttonstate");
		MinecraftForge.EVENT_BUS.post(m);

		ByteBuffer buttons = ObfuscationReflectionHelper.getPrivateValue(Mouse.class, null, "buttons");
		buttons.put(mouseButton, (byte)(held ? 1 : 0));
		ObfuscationReflectionHelper.setPrivateValue(Mouse.class, null, buttons, "buttons");

	}
}
