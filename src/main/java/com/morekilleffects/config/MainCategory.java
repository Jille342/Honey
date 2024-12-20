package com.morekilleffects.config;

import com.morekilleffects.SkywarsKillEffect;
import com.morekilleffects.command.subcommand.CommandNick;
import com.morekilleffects.utils.ConfigUtil;

public class MainCategory {
   private SkywarsKillEffect instance;
   private final String CATEGORY = "MoreKillEffect";
   private final String[] defaultTriggers = new String[]{"was thrown into the void by", "was killed by", "was shot by", "was toasted by", "got rekt by", "took the L to", "got dabbed on by", "got bamboozled by"};
   private final boolean defaultEnabled = true;
   private final String defaultKillEffect = "HeadRocket";
   private final String triggersKey = "triggers";
   private final String enabledKey = "enabled";
   private final String currentKillEffectKey = "currentKillEffect";
   private final String nick = "nick";

   public MainCategory(SkywarsKillEffect instance) {
      this.instance = instance;
      this.loadCategory();
   }

   private void loadCategory() {
      if (!ConfigUtil.cfg.hasCategory("MoreKillEffect")) {
         ConfigUtil.writeBoolean("MoreKillEffect", "enabled", true, true);
         ConfigUtil.writeString("MoreKillEffect", "currentKillEffect", "HeadRocket", "HeadRocket");
         ConfigUtil.writeArrayString("MoreKillEffect", "triggers", this.defaultTriggers, this.defaultTriggers);
         ConfigUtil.writeString("MoreKillEffect", "nick", "nick", "nick");
      }

      ConfigUtil.cfg.save();
   }

   public boolean getEnabled() {
      return ConfigUtil.getBoolean("MoreKillEffect", "enabled", true);
   }

   public void writeEnabled(boolean value) {
      ConfigUtil.writeBoolean("MoreKillEffect", "enabled", true, value);
   }

   public String getCurrentKillEffect() {
      return ConfigUtil.getString("MoreKillEffect", "currentKillEffect", "HeadRocket");
   }

   public void writeCurrentKillEffect(String value) {
      ConfigUtil.writeString("MoreKillEffect", "currentKillEffect", "HeadRocket", value);
   }

   public String getNick() {
      return ConfigUtil.getString("MoreKillEffect", "nick", "nick");
   }

   public void writeNick(String value) {
      ConfigUtil.writeString("MoreKillEffect", "nick", "nick", value);
   }

   public String[] getTriggers() {
      return ConfigUtil.getArrayString("MoreKillEffect", "triggers", this.defaultTriggers);
   }

   public void writeTriggers(String[] value) {
      ConfigUtil.writeArrayString("MoreKillEffect", "triggers", this.defaultTriggers, value);
   }

   public void save() {
      this.writeEnabled(this.instance.isEnabled());
      this.writeCurrentKillEffect(this.instance.getKillEffectManager().getCurrentKillEffect().getEffectName());
      this.writeNick(new CommandNick(this.instance).nick);
   }
}
