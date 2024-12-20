//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\mcp_stable-22-1.8.9"!

package com.morekilleffects.killEffect.killeffects;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.killEffect.KillEffect;
import com.morekilleffects.killEffect.Location;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

public class BloodExplosion extends KillEffect {
   private Random rand = new Random();

   public BloodExplosion(SkywarsKillEffect instance) {
      super(instance, "BloodExplosion");
   }

   public void play(Entity location) {
      this.playEffect(location);
   }

   private void playEffect(Entity location) {
      this.playStepSound(location);
      this.playBlood(location);
   }

   private void playBlood(Entity target) {
      Location location = new Location(target.posX, target.posY, target.posZ);
      AxisAlignedBB axisAlignedBB = target.getEntityBoundingBox();
      int minX = this.floor_double(axisAlignedBB.minX);
      int minY = this.floor_double(axisAlignedBB.minY);
      int minZ = this.floor_double(axisAlignedBB.minZ);

      for(int i = 0; i < 200; ++i) {
         double x = this.randamDouble(true);
         double y = this.randamDouble(false) * 2.0;
         double z = this.randamDouble(true);
         double d0 = location.x + (location.x + x - (double)minX + 0.5) / (double)(i % 17);
         double d1 = location.y + (location.y + y - (double)minY + 0.5) / (double)(i % 17);
         double d2 = location.z + (location.z + z - (double)minZ + 0.5) / (double)(i % 17);
         this.getInstance().mc.effectRenderer.addEffect((new EntityDiggingFX.Factory()).getEntityFX(0, target.worldObj, location.x + x, location.y + y, location.z + z, d0 - location.x - 0.5, d1 - location.y - 0.5, d2 - location.z - 0.5, new int[]{152}));
      }

   }

   private void playStepSound(Entity target) {
      Location location = new Location(target.posX, target.posY, target.posZ);
      Block block = Block.getBlockById(152);
      this.getInstance().mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(block.stepSound.getBreakSound()), block.stepSound.getVolume() + 10F, block.stepSound.getFrequency() * 0.8F, (float)location.x + 0.5F, (float)location.y + 0.5F, (float)location.z + 0.5F));
   }

   private double randamDouble(boolean minus) {
      double randD = this.rand.nextDouble();
      int flag = this.rand.nextInt(2);
      if (flag == 0 && minus) {
         randD = -randD;
      }

      return randD;
   }

   private int floor_double(double value) {
      int i = (int)value;
      return value < (double)i ? i - 1 : i;
   }
}
