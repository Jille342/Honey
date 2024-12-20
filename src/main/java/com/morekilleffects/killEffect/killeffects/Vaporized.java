//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\mcp_stable-22-1.8.9"!

package com.morekilleffects.killEffect.killeffects;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.killEffect.KillEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.Timer;
import java.util.TimerTask;

public class Vaporized extends KillEffect {
   public Vaporized(SkywarsKillEffect instance) {
      super(instance, "Vaporized");
   }

   public static final Minecraft mc = Minecraft.getMinecraft();

   public void play(final Entity target) {
      spawnVaporizedEffect(target);
      playSound();
   }

   public void playSound() {
      Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("mob.ghast.fireball"), 4F));
   }

   // プレイヤーが死亡したときに「蒸発」エフェクトを生成
   private static void spawnVaporizedEffect(Entity target) {
      Minecraft minecraft = Minecraft.getMinecraft();

      // プレイヤーがキルされた位置でエフェクトを発動
      spawnRainbowRedstoneEffect(target.worldObj, target.posX, target.posY, target.posZ);

      // 縦に多くのパーティクルを出すために位置を変更
      for (int i = 0; i < 100; i++) {
         // 縦方向にランダムな位置でパーティクルを発生
         double offsetY = target.posY + Math.random() * 2 - 0.01;

         // 縦方向に広がるように位置を少しずつずらして複数回パーティクルを発生させる
         spawnRainbowRedstoneEffect(target.worldObj, target.posX + (Math.random() - 0.5) * 0.5,
                 offsetY,  // 少しずつ縦方向にずらしていく
                 target.posZ + (Math.random() - 0.5) * 0.5);

         spawnRainbowRedstoneEffect(target.worldObj, target.posX + (Math.random() - 0.5) * 0.5,
                 offsetY,  // さらに縦にズレた位置で2回目
                 target.posZ + (Math.random() - 0.5) * 0.5);
      }
   }

   private static void spawnRainbowRedstoneEffect(World world, double x, double y, double z) {
      int colorStep;
      float[] rainbowColor = new float[0];

      // 0.05秒ごとに色を変えるための更新間隔
      for (int i = 0; i < 10; i++) {  // 200回繰り返すことで、パーティクルが早く色を変化させます
         // 時間の経過に合わせて色が変わる
         colorStep = (i * 360 / 10) % 360;  // 200回で1周するように色を変化させる

         // 虹色のRGBを計算
         rainbowColor = getRainbowColor(colorStep);

         // spawnParticleを使用してRedstoneパーティクルを発生させる
         world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z,
                 rainbowColor[0], rainbowColor[1], rainbowColor[2]);
      }
   }

   private static float[] getRainbowColor(int step) {
      float red = 255;
      float green = 255;
      float blue = 255;
      return new float[]{red, green, blue};
   }
}
