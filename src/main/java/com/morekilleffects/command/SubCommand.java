//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\mcp_stable-22-1.8.9"!

package com.morekilleffects.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.morekilleffects.SkywarsKillEffect;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public abstract class SubCommand {
   private String name;
   private int commandLength;
   protected SkywarsKillEffect instance;
   private final String helpBar;
   private final String help;

   public SubCommand(SkywarsKillEffect instance, String name, int commandLength) {
      this.helpBar = ChatFormatting.BLUE + "---------------" + ChatFormatting.WHITE + "Help: MoreKillEffect" + ChatFormatting.BLUE + "---------------";
      this.help = "/killeffect toggle: Toggle This Mod. \n/killeffect preview: Preview Current Killeffect. \n/killeffect set <killEffectName>: Set Killeffect. \n/killeffect nick <nick>: Set your nick.";
      this.instance = instance;
      this.name = name;
      this.commandLength = commandLength;
   }

   public abstract void processCommand(ICommandSender var1, String... var2);

   public String getName() {
      return this.name;
   }

   public int length() {
      return this.commandLength;
   }

   public boolean equals(String value) {
      return this.name.equals(value);
   }

   protected void showHelp(ICommandSender sender) {
      this.sendMessage(sender, this.helpBar);
      this.sendMessage(sender, "/killeffect toggle: Toggle This Mod. \n/killeffect preview: Preview Current Killeffect. \n/killeffect set <killEffectName>: Set Killeffect. \n/killeffect nick <nick>: Set your nick.");
   }

   protected void sendMessage(ICommandSender sender, String message) {
      sender.addChatMessage(new ChatComponentText(message));
   }
}
