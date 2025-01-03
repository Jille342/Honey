package client.utils.font;

import client.utils.MCUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

public class Fonts implements MCUtil {
    public static CFontRenderer default25 = new CFontRenderer(getDefault(25), true, true);
    public static CFontRenderer default22 = new CFontRenderer(getDefault(22), true, true);
    public static CFontRenderer default20 = new CFontRenderer(getFontTTF("ElliotSans-Regular",20), true, true);
    public static CFontRenderer default18 = new CFontRenderer(getFontTTF("ElliotSans-Regular",18), true, true);
    public static CFontRenderer default16 = new CFontRenderer(getFontTTF("ElliotSans-Regular",16), true, true);
    public static CFontRenderer default15 = new CFontRenderer(getDefault(15), true, true);
    public static CFontRenderer defaultTitle = new CFontRenderer(getFontTTF("ElliotSans-Regular",50), true, true);
    public static CFontRenderer default13 = new CFontRenderer(getDefault(13), true, true);
    public static CFontRenderer default14 = new CFontRenderer(getDefault(14), true, true);
    public static CFontRenderer default12 = new CFontRenderer(getDefault(12), true, true);
    public static CFontRenderer default10 = new CFontRenderer(getDefault(10), true, true);
    public static CFontRenderer comfortaa10 = new CFontRenderer(getFontTTF("Comfortaa", 10), true, true);
    public static CFontRenderer comfortaa12 = new CFontRenderer(getFontTTF("Comfortaa", 12), true, true);
    public static CFontRenderer comfortaa15 = new CFontRenderer(getFontTTF("Comfortaa", 15), true, true);
    public static CFontRenderer comfortaa16 = new CFontRenderer(getFontTTF("Comfortaa", 16), true, true);
    public static CFontRenderer comfortaa17 = new CFontRenderer(getFontTTF("Comfortaa", 17), true, true);
    public static CFontRenderer comfortaa18 = new CFontRenderer(getFontTTF("Comfortaa", 18), true, true);
    public static CFontRenderer elliot12 = new CFontRenderer(getFontTTF("ElliotSans-Regular", 12), true, true);
    public static CFontRenderer elliot15 = new CFontRenderer(getFontTTF("ElliotSans-Regular", 15), true, true);
    public static CFontRenderer elliot17 = new CFontRenderer(getFontTTF("ElliotSans-Regular", 17), true, true);
    public static CFontRenderer elliot18 = new CFontRenderer(getFontTTF("ElliotSans-Regular", 18), true, true);
    public static CFontRenderer elliot20 = new CFontRenderer(getFontTTF("ElliotSans-Regular", 20), true, true);
    public static CFontRenderer consolas13 = new CFontRenderer(getFontTTF("consolas", 13), true, true);
    public static CFontRenderer consolas15 = new CFontRenderer(getFontTTF("consolas", 15), true, true);
    public static CFontRenderer simpleton13 = new CFontRenderer(getFontTTF("consolas", 13), true, true);
    public static CFontRenderer simpleton15 = new CFontRenderer(getFontTTF("consolas", 15), true, true);
    public static CFontRenderer simpleton16 = new CFontRenderer(getFontTTF("consolas", 16), true, true);
    public static CFontRenderer simpleton17 = new CFontRenderer(getFontTTF("consolas", 16), true, true);

    private static Font getFontTTF(String name, int size) {
        Font font;
        try {
            InputStream is = Fonts.class.getResourceAsStream("/assets/minecraft/client/" + name + ".ttf");
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    private static Font getFontOTF(String name, int size) {
        Font font;
        try {
            InputStream is = mc.getResourceManager().getResource(new ResourceLocation("client/" + name + ".otf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    private static Font getDefault(int size) {
        Font font;
        font = new Font("default", 0, size);
        return font;
    }
}