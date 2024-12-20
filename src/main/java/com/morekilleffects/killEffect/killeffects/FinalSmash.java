//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\mcp_stable-22-1.8.9"!

package com.morekilleffects.killEffect.killeffects;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.killEffect.KillEffect;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class FinalSmash extends KillEffect {
   public FinalSmash(SkywarsKillEffect instance) {
      super(instance, "FinalSmash");
   }

   private void setArmorStand(Entity target, EntityArmorStand armorStand) {
      armorStand.setPosition(target.posX, target.posY, target.posZ);
      armorStand.setInvisible(true);
      armorStand.setCurrentItemOrArmor(4, createPlayerHead(this.getInstance().mc.thePlayer.getName()));
      armorStand.setCurrentItemOrArmor(3, new ItemStack(Items.leather_chestplate));
      armorStand.setCurrentItemOrArmor(2, new ItemStack(Items.leather_leggings));
      armorStand.setCurrentItemOrArmor(1, new ItemStack(Items.leather_boots));
      target.getEntityWorld().spawnEntityInWorld(armorStand);
      Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("fireworks.launch"), 0.5F));
   }

   private ItemStack createPlayerHead(String playerName) {
      ItemStack itemStack = new ItemStack(Items.skull, 1, 3);
      NBTTagCompound tag = new NBTTagCompound();
      tag.setTag("SkullOwner", new NBTTagString(playerName));
      itemStack.setTagCompound(tag);
      return itemStack;
   }

   public void play(Entity target) {
      final EntityArmorStand armorStand = new EntityArmorStand(target.getEntityWorld());
      this.setArmorStand(target, armorStand);

      // 移動処理用の変数
      float yaw = this.getInstance().mc.thePlayer.rotationYaw;
      final double addX = Math.cos(Math.toRadians(yaw + 90.0F)) * 0.25;
      final double addZ = Math.sin(Math.toRadians(yaw + 90.0F)) * 0.25;
      final double tempY = armorStand.posY;

      // 毎tickで処理を実行
      new Thread(() -> {
         while (true) {
            try {
               Thread.sleep(1); // 50msごとに実行（1tick = 50ms）
            } catch (InterruptedException e) {
               e.printStackTrace();
            }

            // 13ブロック上がった場合は停止
            if (armorStand.posY - tempY > 13.0 ||
                    armorStand.getEntityWorld().getBlockState(new BlockPos(armorStand.posX + addX, armorStand.posY + 0.05, armorStand.posZ + addZ)).getBlock() != Blocks.air) {
               armorStand.setDead();
               Minecraft.getMinecraft().theWorld.removeEntity(armorStand);
               createFireworks(armorStand);
               break;
            }

            // 煙を生成
            if (Minecraft.getMinecraft().thePlayer.ticksExisted % 3 == 0) {
               playerCloud(armorStand);
            }

            // 位置を更新
            armorStand.setPositionAndRotation(armorStand.posX + addX, armorStand.posY + 0.05, armorStand.posZ + addZ, armorStand.rotationYaw + 4.0F, armorStand.rotationPitch);
         }
      }).start();
   }

   private void createFireworks(EntityArmorStand armorStand) {
      try {
         NBTTagCompound nbt = JsonToNBT.getTagFromJson("{Flight:1,Explosions:[0:{Type:1,Trail:1,Colors:[11743532],FadeColors:[11743532]}]}");
         armorStand.getEntityWorld().makeFireworks(armorStand.posX, armorStand.posY, armorStand.posZ, 0.0, 0.0, 0.0, nbt);
      } catch (NBTException e) {
         System.err.println("Error: Invalid NBT data for fireworks!");
      }
   }


   private void playerCloud(Entity entity) {
      Random rand = entity.worldObj.rand;
      double x = (double)(rand.nextInt(10) / 4) - 1.25;
      double z = (double)(rand.nextInt(10) / 4) - 1.25;
      Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.CLOUD, entity.posX, entity.posY - 0.3, entity.posZ, 0.0, 0.0, 0.0, new int[0]);
   }
}
