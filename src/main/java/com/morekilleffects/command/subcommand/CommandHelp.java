package com.morekilleffects.command.subcommand;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.command.SubCommand;
import net.minecraft.command.ICommandSender;

public class CommandHelp extends SubCommand {
   public CommandHelp(SkywarsKillEffect instance) {
      super(instance, "help", 1);
   }

   public void processCommand(ICommandSender sender, String... args) {
      this.showHelp(sender);
   }
}
