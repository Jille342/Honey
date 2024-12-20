package com.morekilleffects.utils;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigUtil {
   public static Configuration cfg;

   public static void loadConfig(FMLPreInitializationEvent event) {
      cfg = new Configuration(event.getSuggestedConfigurationFile(), "1.2", true);
   }

   public static void writeBoolean(String category, String key, boolean defaultBoolean, boolean value) {
      try {
         cfg.load();
         cfg.get(category, key, defaultBoolean).set(value);
      } catch (Exception var8) {
         System.out.println("Error: config cannot load!");
      } finally {
         cfg.save();
      }

   }

   public static boolean getBoolean(String category, String key, boolean defaultBoolean) {
      try {
         cfg.load();
         boolean var3 = cfg.get(category, key, defaultBoolean).getBoolean();
         return var3;
      } catch (Exception var7) {
         System.out.println("Error: config cannot load!");
      } finally {
         cfg.save();
      }

      return false;
   }

   public static void writeString(String category, String key, String defaultString, String value) {
      try {
         cfg.load();
         cfg.get(category, key, defaultString).set(value);
      } catch (Exception var8) {
         System.out.println("Error: config cannot load!");
      } finally {
         cfg.save();
      }

   }

   public static String getString(String category, String key, String defaultString) {
      try {
         cfg.load();
         String var3 = cfg.get(category, key, defaultString).getString();
         return var3;
      } catch (Exception var7) {
         System.out.println("Error: config cannot load!");
      } finally {
         cfg.save();
      }

      return null;
   }

   public static void writeArrayString(String category, String key, String[] defaultArrayString, String[] value) {
      try {
         cfg.load();
         cfg.get(category, key, defaultArrayString).set(value);
      } catch (Exception var8) {
         System.out.println("Error: config cannot load!");
      } finally {
         cfg.save();
      }

   }

   public static String[] getArrayString(String category, String key, String[] defaultArrayString) {
      try {
         cfg.load();
         String[] var3 = cfg.get(category, key, defaultArrayString).getStringList();
         return var3;
      } catch (Exception var7) {
         System.out.println("Error: config cannot load!");
      } finally {
         cfg.save();
      }

      return null;
   }
}
