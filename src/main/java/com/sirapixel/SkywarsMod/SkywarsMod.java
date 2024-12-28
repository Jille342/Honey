
package com.sirapixel.SkywarsMod;

import client.features.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkywarsMod {
   private char[] colorCodes = new char[]{'4', 'c', '6', 'e', 'a', 'b', '3', '9', 'd'};

   public String rainbow(String input, int startingPosition) {
      startingPosition %= this.colorCodes.length;
      char[] list = input.toCharArray();
      boolean forward = true;
      String toReturn = "";
      char[] var6 = list;
      int var7 = list.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         char c = var6[var8];
         if (c == ' ') {
            toReturn = toReturn + " ";
         } else {
            char col = this.colorCodes[startingPosition];
            toReturn = toReturn + "§" + col + c;
            if (forward) {
               ++startingPosition;
               if (startingPosition > this.colorCodes.length - 1) {
                  startingPosition -= 2;
                  forward = false;
               }
            } else {
               --startingPosition;
               if (startingPosition < 0) {
                  startingPosition += 2;
                  forward = true;
               }
            }
         }
      }

      return toReturn;
   }
     /*
   @Mod.EventHandler
   public void init(FMLInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(this);
   }

      */

   @SubscribeEvent
   public void chatReader(ClientChatReceivedEvent e) {
      if (ModuleManager.getModulebyClass(client.features.module.misc.SkywarsMod.class).isEnable()) {
         if (e.type == 0) {
            String msg = e.message.getUnformattedText();
            if (msg.contains("was struck down by " + Minecraft.getMinecraft().thePlayer.getName() + ".")) {
               e.setCanceled(true);
               msg = msg.replace("was struck down by " + Minecraft.getMinecraft().thePlayer.getName() + ".", "became victim #114,514 of " + Minecraft.getMinecraft().thePlayer.getName() + ".");
               msg = this.rainbow(msg, 0);
               Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(msg));
            }

            if (msg.contains("was turned to dust by " + Minecraft.getMinecraft().thePlayer.getName() + ".")) {
               e.setCanceled(true);
               msg = msg.replace("was turned to dust by " + Minecraft.getMinecraft().thePlayer.getName() + ".", "was void victim #114,514 of " + Minecraft.getMinecraft().thePlayer.getName() + ".");
               msg = this.rainbow(msg, 0);
               Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(msg));
            }

            if (msg.contains("was turned to ash by " + Minecraft.getMinecraft().thePlayer.getName() + ".")) {
               e.setCanceled(true);
               msg = msg.replace("was turned to ash by " + Minecraft.getMinecraft().thePlayer.getName() + ".", "became victim #114,514 of " + Minecraft.getMinecraft().thePlayer.getName() + ".");
               msg = this.rainbow(msg, 0);
               Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(msg));
            }

            if (msg.contains("was melted by " + Minecraft.getMinecraft().thePlayer.getName() + ".")) {
               e.setCanceled(true);
               msg = msg.replace("was melted by " + Minecraft.getMinecraft().thePlayer.getName() + ".", "was bow kill #1,919 of " + Minecraft.getMinecraft().thePlayer.getName() + ".");
               msg = this.rainbow(msg, 0);
               Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(msg));
            }

            if (msg.contains("was Bomberman'd by " + Minecraft.getMinecraft().thePlayer.getName() + ".")) {
               e.setCanceled(true);
               msg = msg.replace("was Bomberman'd by " + Minecraft.getMinecraft().thePlayer.getName() + ".", "was Bomberman'd by " + Minecraft.getMinecraft().thePlayer.getName() + ".");
               msg = this.rainbow(msg, 0);
               Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(msg));
            }

            if (msg.contains("was toasted by " + Minecraft.getMinecraft().thePlayer.getName() + ".")) {
               e.setCanceled(true);
               msg = msg.replace("was toasted by " + Minecraft.getMinecraft().thePlayer.getName() + ".", "was toasted by " + Minecraft.getMinecraft().thePlayer.getName() + ".");
               msg = this.rainbow(msg, 0);
               Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(msg));
            }
         }

      }
   }
}
