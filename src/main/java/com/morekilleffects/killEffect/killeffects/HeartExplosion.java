//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\mcp_stable-22-1.8.9"!

package com.morekilleffects.killEffect.killeffects;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.killEffect.KillEffect;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;

public class HeartExplosion extends KillEffect {
   private Random rand = new Random();

   public HeartExplosion(SkywarsKillEffect instance) {
      super(instance, "HeartExplosion");
   }

   public void play(final Entity target) {
      final long startTime = System.currentTimeMillis();
      (new Timer()).schedule(new TimerTask() {
         public void run() {
            if (System.currentTimeMillis() - startTime > 3000L) {
               this.cancel();
            }

            List hearts = HeartExplosion.this.generateHearts(target, 7);
            Iterator var2 = hearts.iterator();

            while(var2.hasNext()) {
               Heart heart = (Heart)var2.next();
               target.worldObj.spawnParticle(EnumParticleTypes.HEART, target.posX + heart.addX, target.posY + heart.addY, target.posZ + heart.addZ, 0.0, 0.0, 0.0, new int[0]);
            }

         }
      }, 5L, 300L);
   }

   private double randamDouble(boolean minus) {
      double randD = this.rand.nextDouble();
      int flag = this.rand.nextInt(2);
      if (flag == 0 && minus) {
         randD = -randD;
      }

      return randD;
   }

   private List generateHearts(Entity target, int size) {
      List result = new ArrayList();

      for(int i = 0; i < size; ++i) {
         double randX = this.randamDouble(true);
         double randY = this.randamDouble(false);
         double randZ = this.randamDouble(true);
         result.add(new Heart(randX, randY, randZ));
      }

      return result;
   }

   private class Heart {
      double addX;
      double addY;
      double addZ;

      public Heart(double addX, double addY, double addZ) {
         this.addX = addX;
         this.addY = addY;
         this.addZ = addZ;
      }
   }
}
