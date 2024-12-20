//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\mcp_stable-22-1.8.9"!

package com.morekilleffects.killEffect.killeffects;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.killEffect.KillEffect;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public class PurpleTornado extends KillEffect {
   public PurpleTornado(SkywarsKillEffect instance) {
      super(instance, "PurpleTornado");
   }

   public void play(final Entity target) {
      final long startTime = System.currentTimeMillis();
      (new Timer()).schedule(new TimerTask() {

         public void run() {
            if (System.currentTimeMillis() - startTime > 3500L) {
               this.cancel();
            }

            playSound();
            ParticleSpiral particleSpiral = new ParticleSpiral(target.posX, target.posY, target.posZ);
            MinecraftForge.EVENT_BUS.register(particleSpiral);
         }
      }, 1L, 500L);
   }

   public void playSound() {
      Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("mob.enderdragon.wings"), 0.1F));
      Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("mob.enderdragon.wings"), 0.2F));
   }
}
