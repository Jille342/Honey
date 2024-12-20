//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\mcp_stable-22-1.8.9"!

package com.morekilleffects.killEffect.killeffects;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.killEffect.KillEffect;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class WitherSmash extends KillEffect {
   public WitherSmash(SkywarsKillEffect instance) {
      super(instance, "WitherSmash");
   }

   public void play(Entity target) {
      final EntityWitherSkull witherSkull = new EntityWitherSkull(target.getEntityWorld());
      witherSkull.setPosition(target.posX, target.posY, target.posZ);
      target.getEntityWorld().spawnEntityInWorld(witherSkull);
      float yaw = this.getInstance().mc.thePlayer.rotationYaw;
      final double addX = Math.cos(Math.toRadians((double)(yaw + 90.0F))) * 0.15;
      double addY = 0.05;
      final double addZ = Math.sin(Math.toRadians((double)(Math.abs(yaw) + 90.0F))) * 0.15;
      final double tempY = witherSkull.posY;
      this.playShootSound();
      (new Timer()).schedule(new TimerTask() {
         public void run() {
            label11: {
               if (!(witherSkull.posY - tempY > 20.0)) {
                  double var10003 = witherSkull.posX + addX;
                  if (WitherSmash.this.getInstance().mc.theWorld.getBlockState(new BlockPos(var10003, witherSkull.posY + 0.05, witherSkull.posZ + addZ)).getBlock() == Blocks.air) {
                     break label11;
                  }
               }

               WitherSmash.this.playExplosion(witherSkull);
               witherSkull.setDead();
               this.cancel();
            }

            witherSkull.setPositionAndRotation(witherSkull.posX + addX, witherSkull.posY + 0.05, witherSkull.posZ + addZ, witherSkull.rotationYaw + 3.0F, witherSkull.rotationPitch);
            witherSkull.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, witherSkull.posX, witherSkull.posY, witherSkull.posZ, 0.0, 0.0, 0.0, new int[0]);
         }
      }, 1L, 10L);
   }

   private void playShootSound() {
      this.getInstance().mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("mob.wither.shoot"), 1.0F));
   }

   private void playExplosion(Entity entity) {
      this.getInstance().mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.explode"), 1.0F));
      entity.getEntityWorld().spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, entity.posX, entity.posY, entity.posZ, 0.0, 0.0, 0.0, new int[0]);
   }
}
