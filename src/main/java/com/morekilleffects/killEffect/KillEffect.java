package com.morekilleffects.killEffect;

import com.morekilleffects.SkywarsKillEffect;
import net.minecraft.entity.Entity;

public abstract class KillEffect {
   private String name;
   private SkywarsKillEffect instance;

   public KillEffect(SkywarsKillEffect instance, String name) {
      this.instance = instance;
      this.name = name;
   }

   public String getEffectName() {
      return this.name;
   }

   protected SkywarsKillEffect getInstance() {
      return this.instance;
   }

   public abstract void play(Entity var1);
}
