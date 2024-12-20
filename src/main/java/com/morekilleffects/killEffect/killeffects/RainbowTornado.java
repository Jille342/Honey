//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\mcp_stable-22-1.8.9"!

package com.morekilleffects.killEffect.killeffects;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.killEffect.KillEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.util.Timer;
import java.util.TimerTask;

public class RainbowTornado extends KillEffect {
   public RainbowTornado(SkywarsKillEffect instance) {
      super(instance, "RainbowTornado");
   }

   public void play(final Entity target) {
      final long startTime = System.currentTimeMillis();
      (new Timer()).schedule(new TimerTask() {

         public void run() {
            if (System.currentTimeMillis() - startTime > 3500L) {
               this.cancel();
            }

            playSound();
            ParticleSpiral2 particleSpiral2 = new ParticleSpiral2(target.posX, target.posY, target.posZ);
            MinecraftForge.EVENT_BUS.register(particleSpiral2);
         }
      }, 1L, 500L);
   }

   public void playSound() {
      Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("mob.enderdragon.wings"), 0.1F));
      Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("mob.enderdragon.wings"), 0.2F));
   }
}
