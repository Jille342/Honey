package com.morekilleffects.command.subcommand;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.command.SubCommand;
import net.minecraft.command.ICommandSender;

public class Test extends SubCommand {
   public Test(SkywarsKillEffect instance) {
      super(instance, "test", 1);
   }

   public void processCommand(ICommandSender sender, String... args) {
   }
}
