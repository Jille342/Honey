//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\mcp_stable-22-1.8.9"!

package com.morekilleffects.command.subcommand;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.command.SubCommand;
import net.minecraft.command.ICommandSender;

public class CommandPreview extends SubCommand {
   public CommandPreview(SkywarsKillEffect instance) {
      super(instance, "preview", 1);
   }

   public void processCommand(ICommandSender sender, String... args) {
      if (this.instance.getKillEffectManager().getCurrentKillEffect() != null) {
         this.instance.getKillEffectManager().getCurrentKillEffect().play(this.instance.mc.thePlayer);
      }

   }
}
