package com.morekilleffects.command.subcommand;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.command.SubCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class CommandSet extends SubCommand {
   public CommandSet(SkywarsKillEffect instance) {
      super(instance, "set", 2);
   }

   public void processCommand(ICommandSender sender, String... args) {
      if (args.length == 1) {
         this.showUsage(sender);
      } else {
         if (this.instance.getKillEffectManager().isKillEffect(args[1])) {
            this.instance.getKillEffectManager().setCurrentKillEffect(this.instance.getKillEffectManager().getKillEffectByName(args[1]));
            this.sendMessage(sender, EnumChatFormatting.GREEN + "KillEffect is now " + this.instance.getKillEffectManager().getKillEffectByName(args[1]).getEffectName());
         } else {
            this.sendMessage(sender, EnumChatFormatting.RED + "Invalid KillEffect Name!");
            StringBuilder builder = new StringBuilder();
            String[] var4 = this.instance.getKillEffectManager().getKillEffects();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               String killEffectName = var4[var6];
               builder.append(killEffectName);
               builder.append(", ");
            }

            builder.replace(builder.length() - 2, builder.length(), "");
            this.sendMessage(sender, "killEffects: " + EnumChatFormatting.GREEN + builder.toString());
         }

      }
   }

   private void showUsage(ICommandSender sender) {
      this.sendMessage(sender, EnumChatFormatting.RED + "Usage: /killeffect set <killEffectName>");
   }
}
