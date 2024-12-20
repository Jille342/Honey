//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\mcp_stable-22-1.8.9"!

package com.morekilleffects.killEffect.killeffects;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.killEffect.KillEffect;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class HeadRocket extends KillEffect {
   private EntityArmorStand armorStand;
   private double tempY, y;

   public HeadRocket(SkywarsKillEffect instance) {
      super(instance, "HeadRocket");
   }

   public void play(Entity target) {
      final EntityArmorStand armorStand = new EntityArmorStand(target.getEntityWorld());
      this.setupArmorStand(target, armorStand);
      this.playSound();
      (new Timer()).schedule(new TimerTask() {
         public void run() {
            if (armorStand.posY - HeadRocket.this.tempY > 9.0 || HeadRocket.this.getInstance().mc.theWorld.getBlockState(new BlockPos(armorStand.posX, armorStand.posY + 1.0, armorStand.posZ)).getBlock() != Blocks.air) {
               HeadRocket.this.playEffect(armorStand.posX, armorStand.posY, armorStand.posZ);
               armorStand.setDead();
               this.cancel();
            }

            HeadRocket.this.playCloud(armorStand);
            armorStand.setPositionAndRotation(armorStand.posX, armorStand.posY + y, armorStand.posZ, armorStand.rotationYaw + 5.0F, armorStand.rotationPitch);
            if(y < 0.25) {
               y += 0.05;
            }
         }
      }, 1L, 20L);
   }

   private void setupArmorStand(Entity target, EntityArmorStand armorStand) {
      armorStand.setPosition(target.posX, target.posY, target.posZ);
      armorStand.setInvisible(true);
      armorStand.setCurrentItemOrArmor(4, createPlayerHead(this.getInstance().mc.thePlayer.getName()));
      target.getEntityWorld().spawnEntityInWorld(armorStand);
      this.tempY = armorStand.posY;
      this.y = 0;
   }

   private ItemStack createPlayerHead(String playerName) {
      ItemStack itemStack = new ItemStack(Items.skull, 1, 3);
      NBTTagCompound tag = new NBTTagCompound();
      tag.setTag("SkullOwner", new NBTTagString(playerName));
      itemStack.setTagCompound(tag);
      return itemStack;
   }

   private void playEffect(double x, double y, double z) {
      Minecraft.getMinecraft().renderGlobal.playAuxSFX((EntityPlayer)null, 2001, new BlockPos(x, y, z), 152);
      Minecraft.getMinecraft().renderGlobal.playAuxSFX((EntityPlayer)null, 2001, new BlockPos(x, y, z), 152);
      Minecraft.getMinecraft().renderGlobal.playAuxSFX((EntityPlayer)null, 2001, new BlockPos(x, y, z), 152);
   }

   private void playSound() {
      Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("fireworks.launch"), 1F));
   }

   private void playCloud(Entity entity) {
      entity.getEntityWorld().spawnParticle(EnumParticleTypes.CLOUD, entity.posX, entity.posY + 0.3, entity.posZ, 0.0, 0.0, 0.0, new int[0]);
   }
}
