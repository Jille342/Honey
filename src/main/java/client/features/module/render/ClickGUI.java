package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventRenderGUI;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.ui.clicckgui2.ClickGui;
import client.ui.theme.Theme;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Module {

	public ClickGUI() {
		super("ClickGUI", Keyboard.KEY_RSHIFT,	Category.RENDER);
	}

	public ModeSetting theme;
	public static BooleanSetting autoGuiScale;
	public static NumberSetting size;

	@Override
	public void init() {
		size= new NumberSetting("Size", 50, 40,150,1);
		List<String> mode = new ArrayList<String>();
		for(Theme theme : client.Client.themeManager.themes) {
			mode.add(theme.getName());
		}
		String[] modes = mode.toArray(new String[mode.size()]);
		this.theme = new ModeSetting("Theme", mode.get(0), modes);
		this.autoGuiScale = new BooleanSetting("AutoResize", true);
		if(this.getKeyCode()== 0)
			this.setKeyCode(Keyboard.KEY_RSHIFT);
		addSetting(theme, autoGuiScale,size);
		super.init();
	}

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventRender2D) {
			client.Client.themeManager.setTheme(theme.getMode());
			mc.displayGuiScreen(new ClickGui(0));
		}
		super.onEvent(e);
	}

	@Override
	public void onEnable() {
		mc.displayGuiScreen(new ClickGui(0));
		super.onEnable();
	}
}
