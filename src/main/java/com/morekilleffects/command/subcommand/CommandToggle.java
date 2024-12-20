package com.morekilleffects.command.subcommand;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.command.SubCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class CommandToggle extends SubCommand {
   private final String enableMsg;
   private final String disableMsg;

   public CommandToggle(SkywarsKillEffect instance) {
      super(instance, "toggle", 1);
      this.enableMsg = EnumChatFormatting.GREEN + "KillEffect has been enabled!";
      this.disableMsg = EnumChatFormatting.RED + "KillEffect has been disabled!";
   }

   public void processCommand(ICommandSender sender, String... args) {
      this.instance.setEnabled(!this.instance.isEnabled());
      this.sendMessage(sender, this.instance.isEnabled() ? this.enableMsg : this.disableMsg);
   }
}
