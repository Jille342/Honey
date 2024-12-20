//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\mcp_stable-22-1.8.9"!

package com.morekilleffects.command;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.command.subcommand.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Command extends CommandBase {
   private SkywarsKillEffect instance;
   private String commandName = "killeffect";
   private String usage = "/killeffect";
   private String info = "Usage: /killeffect <help, toggle, set, preview, nick>";
   private List subCommands = new ArrayList();

   public Command(SkywarsKillEffect instance) {
      this.instance = instance;
      this.registerSubCommands();
   }

   public String getCommandName() {
      return this.commandName;
   }

   public String getCommandUsage(ICommandSender sender) {
      return this.usage;
   }

   public boolean canCommandSenderUseCommand(ICommandSender sender) {
      return true;
   }

   public void registerSubCommand(SubCommand subCommand) {
      this.subCommands.add(subCommand);
   }

   public void registerSubCommands() {
      this.registerSubCommand(new CommandHelp(this.instance));
      this.registerSubCommand(new CommandPreview(this.instance));
      this.registerSubCommand(new CommandSet(this.instance));
      this.registerSubCommand(new CommandToggle(this.instance));
      this.registerSubCommand(new CommandNick(this.instance));
   }

   public void processCommand(ICommandSender sender, String[] args) {
      if (sender != null && args.length != 0) {
         Iterator var3 = this.subCommands.iterator();

         SubCommand subCommand;
         do {
            if (!var3.hasNext()) {
               this.showInfo(sender);
               return;
            }

            subCommand = (SubCommand)var3.next();
         } while(!subCommand.equals(args[0]));

         subCommand.processCommand(sender, args);
      } else {
         this.showInfo(sender);
      }
   }

   private void showInfo(ICommandSender sender) {
      sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + this.info));
   }
}
