
package com.morekilleffects;

import com.morekilleffects.command.Command;
import com.morekilleffects.config.MainCategory;
import com.morekilleffects.killEffect.KillEffectManager;
import com.morekilleffects.utils.ConfigUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class SkywarsKillEffect {
   public static final String name = "Skywars Kill Effect";
   public static final String modid = "SkywarsKillEffect";
   public static final String version = "1.0";
   public final Minecraft mc = Minecraft.getMinecraft();
   private KillEffectManager killEffectManager;
   private MainCategory mainCategory;
   private boolean enabled = true;

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      ConfigUtil.loadConfig(event);
      this.mainCategory = new MainCategory(this);
      this.killEffectManager = new KillEffectManager(this);
      MinecraftForge.EVENT_BUS.register(new Event(this));
      ClientCommandHandler.instance.registerCommand(new Command(this));
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
   }

   public KillEffectManager getKillEffectManager() {
      return this.killEffectManager;
   }

   public MainCategory getMainCategory() {
      return this.mainCategory;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
