//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\mcp_stable-22-1.8.9"!

package com.morekilleffects;

import com.morekilleffects.killEffect.KillEffect;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class Event {
   private SkywarsKillEffect instance;
   private World lastWorld = null;
   private String[] triggers;
   private Map playerList = new HashMap();

   public Event(SkywarsKillEffect instance) {
      this.instance = instance;
      this.triggers = instance.getMainCategory().getTriggers();
   }

   @SubscribeEvent
   public void onChat(ClientChatReceivedEvent event) {
      if (this.instance.isEnabled()) {
         String message = event.message.getUnformattedText();
         String[] var3 = this.triggers;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String trigger = var3[var5];
            String[] splitMessage = message.split(" ");
            if (message.contains(trigger) && (message.contains(this.instance.mc.thePlayer.getName()) || message.contains(this.instance.getMainCategory().getNick())) && !(splitMessage[0].equalsIgnoreCase(this.instance.mc.thePlayer.getName()) || splitMessage[0].equalsIgnoreCase(this.instance.getMainCategory().getNick()))) {
               KillEffect killEffect = this.instance.getKillEffectManager().getCurrentKillEffect();
               if (killEffect != null) {
                  killEffect.play((Entity)this.playerList.get(splitMessage[0]));
               }
            }
         }

      }
   }

   @SubscribeEvent
   public void onChangeConfig(ConfigChangedEvent event) {
      SkywarsKillEffect var10001 = this.instance;
      if (event.modID.equals("morekilleffect")) {
         this.refreshTriggers();
      }

   }

   private void refreshTriggers() {
      this.triggers = this.instance.getMainCategory().getTriggers();
   }

   @SubscribeEvent
   public void onPlayerLogout(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
      this.instance.getMainCategory().save();
   }

   @SubscribeEvent
   public void onPlayerJoin(EntityJoinWorldEvent event) {
      if (this.lastWorld != null && !this.lastWorld.equals(event.world)) {
         this.playerList.clear();
      }

      if (event.entity instanceof EntityPlayer) {
         this.playerList.put(event.entity.getName(), (EntityPlayer)event.entity);
      }

      this.lastWorld = event.world;
   }
}
