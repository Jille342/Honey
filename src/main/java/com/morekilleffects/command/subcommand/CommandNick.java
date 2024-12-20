package com.morekilleffects.command.subcommand;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.command.SubCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class CommandNick extends SubCommand {
   public String nick = this.instance.getMainCategory().getNick();
   public CommandNick(SkywarsKillEffect instance) {
      super(instance, "nick", 2);
   }

   public void processCommand(ICommandSender sender, String... args) {
      if (args.length == 1) {
         this.showUsage(sender);
      } else {
         nick = args[1];
         this.instance.getMainCategory().writeNick(args[1]);
         this.sendMessage(sender, EnumChatFormatting.GREEN + "Set nick to " + args[1] + ".");
      }
   }

   private void showUsage(ICommandSender sender) {
      this.sendMessage(sender, EnumChatFormatting.RED + "Usage: /killeffect nick <nick>");
   }
}
