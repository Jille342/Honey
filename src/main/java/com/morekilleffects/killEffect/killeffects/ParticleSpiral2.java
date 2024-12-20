package com.morekilleffects.killEffect.killeffects;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ParticleSpiral2 {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final double x;
    private final double yStart;
    private final double z;
    private int deg = 0;
    private int tickCount = 0;

    public ParticleSpiral2(double x, double y, double z) {
        this.x = x;
        this.yStart = y;
        this.z = z;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            double rad1 = Math.toRadians(-deg);  // 初期の角度は0
            double rad2 = Math.toRadians(-deg - 180);

            createParticleAtOffset(rad1);
            createParticleAtOffset(rad2);

            if (tickCount >= 50) {
                MinecraftForge.EVENT_BUS.unregister(this); // イベントの登録を解除
                return;
            }

            deg += 10;

            if (deg >= 360) {
                deg = 0;  // 0になったらリセット
            }

            if (deg >= 160) {
                MinecraftForge.EVENT_BUS.unregister(this); // イベントの登録を解除
            }
            tickCount++;
        }
    }

    private void createParticleAtOffset(double rad) {
        Vec3 particlePos = new Vec3(x, yStart, z);
        Vec3 particlePos1 = new Vec3(x + Math.sin(rad) * 0.5f, yStart, z + Math.cos(rad) * 0.5f);
        Vec3 particlePos2 = new Vec3(x + Math.sin(rad) * 0.8f, yStart, z + Math.cos(rad) * 0.8f);
        Vec3 particlePos3 = new Vec3(x + Math.sin(rad) * 1.1f, yStart, z + Math.cos(rad) * 1.1f);
        Vec3 particlePos4 = new Vec3(x + Math.sin(rad) * 1.4f, yStart, z + Math.cos(rad) * 1.4f);
        Vec3 particlePos5 = new Vec3(x + Math.sin(rad) * 1.7f, yStart, z + Math.cos(rad) * 1.7f);
        Vec3 particlePos7 = new Vec3(x + Math.sin(rad) * 0.25f, yStart, z + Math.cos(rad) * 0.25f);

        // 虹色の煙
        mc.theWorld.spawnParticle(EnumParticleTypes.REDSTONE, particlePos.xCoord, particlePos.yCoord, particlePos.zCoord, 255, 255, 255);
        mc.theWorld.spawnParticle(EnumParticleTypes.REDSTONE, particlePos7.xCoord, particlePos1.yCoord + 1, particlePos7.zCoord, 255, 255, 255);
        mc.theWorld.spawnParticle(EnumParticleTypes.REDSTONE, particlePos1.xCoord, particlePos1.yCoord + 2, particlePos1.zCoord, 255, 255, 255);
        mc.theWorld.spawnParticle(EnumParticleTypes.REDSTONE, particlePos2.xCoord, particlePos2.yCoord + 3, particlePos2.zCoord, 255, 255, 255);
        mc.theWorld.spawnParticle(EnumParticleTypes.REDSTONE, particlePos3.xCoord, particlePos3.yCoord + 4, particlePos3.zCoord, 255, 255, 255);
        mc.theWorld.spawnParticle(EnumParticleTypes.REDSTONE, particlePos4.xCoord, particlePos4.yCoord + 5, particlePos4.zCoord, 255, 255, 255);
        mc.theWorld.spawnParticle(EnumParticleTypes.REDSTONE, particlePos5.xCoord, particlePos5.yCoord + 6, particlePos5.zCoord, 255, 255, 255);

        // 下に出る灰色の煙
        if (tickCount >= 8) {
//            mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + 0.5, yStart - 1, z - 0.5, 0.0, 0.0, 0.0, 0);
            mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + 0.5, yStart - 1, z + 0.5, 0.0, 0.0, 0.0, 0);
            mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + 0.5, yStart - 1, z, 0.0, 0.0, 0.0, 0);
            mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, yStart - 1, z - 0.5, 0.0, 0.0, 0.0, 0);
            mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, yStart - 1, z, 0.0, 0.0, 0.0, 0);
            mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, yStart - 1, z + 0.5, 0.0, 0.0, 0.0, 0);
            mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x - 0.5, yStart - 1, z, 0.0, 0.0, 0.0, 0);
            mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x - 0.5, yStart - 1, z - 0.5, 0.0, 0.0, 0.0, 0);
//            mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x - 0.5, yStart - 1, z + 0.5, 0.0, 0.0, 0.0, 0);
        }
    }
}
