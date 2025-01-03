package client;

import client.command.CommandManager;
import client.config.ConfigManager;
import client.event.listeners.EventChat;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.ModuleManager;
import client.ui.HUD2;
import client.ui.theme.ThemeManager;
import client.utils.WorldUtils;
import com.sirapixel.SkywarsMod.SkywarsMod;
import com.sirapixel.supersheep.SuperSheep;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

import static client.Client.MOD_ID;
import static client.Client.NAME;

@Mod(modid = MOD_ID, name = NAME, version = client.Client.VERSION)
public class Client
{
    public static final String MOD_ID = "honey";
    public static final String NAME = "Honey";
    public static final String VERSION = "20240805";
	public static HUD2 hud2 = new HUD2();
	public static String username = null;
	public static ConfigManager configManager;

	public static ThemeManager themeManager = new ThemeManager();
	public static CommandManager commandManager = new CommandManager();
	public static Minecraft mc = Minecraft.getMinecraft();
	public static final File FOLDER = new File(mc.mcDataDir, NAME);
	public static ResourceLocation background = new ResourceLocation("client/bg1.jpg");

    public static void init() {
makeClientDirectory();
		commandManager.init();
		ModuleManager.registerModules();
		ModuleManager.loadModuleSetting();
		configManager = new ConfigManager();
	}
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new SkywarsMod());
		ClientCommandHandler.instance.registerCommand(new SuperSheep.CommandSpawnSuperSheep());
		ClientCommandHandler.instance.registerCommand(new SuperSheep.CommandSpawnDragonRider());
		MinecraftForge.EVENT_BUS.register(new SuperSheep.VictorySpawner());
		MinecraftForge.EVENT_BUS.register(new SuperSheep.PositionPacketCancel());
	}

        public static Event<?> onEvent(Event<?> e) {
		if (e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			Packet p = event.getPacket();
			if (p instanceof S03PacketTimeUpdate) {
				WorldUtils.onTime((S03PacketTimeUpdate) p);
			}
		}
    	ModuleManager.onEvent(e);
		return e;
	}

	/*@SubscribeEvent
	public void keyEvent(InputEvent.KeyInputEvent e) {
		MinecraftForge.EVENT_BUS.register(this);
		if (mc.currentScreen == null) {
			try {
				if (Keyboard.isCreated()) {
					if (Keyboard.getEventKeyState()) {
						int i = Keyboard.getEventKey();
						if (i != 0) {
							ModuleManager.modules.stream().forEach(m -> {
								if(m.getKeyCode() == i) m.toggle();
							});
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
*/
	public static CommandManager getCommandManager(){
		return commandManager;
	}
	public static ConfigManager getConfigManager(){
		return configManager;
	}
	@SubscribeEvent
	public void chatEvent(ClientChatReceivedEvent event) {
		String message = String.valueOf(event.message);

		if (commandManager.handleCommand(message)) {
			event.setCanceled(true);
		}
		onEvent(new EventChat(message));
	}
	public static void makeClientDirectory()
	{
		if(!FOLDER.exists())
		{
			FOLDER.mkdir();
		}
	}
}
