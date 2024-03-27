//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Null\Downloads\1.8.9 MAPPINGS"!

//Decompiled by Procyon!

package client.features.module.misc;

import client.event.Event;
import client.features.module.*;
import client.utils.font.*;
import client.setting.*;
import client.event.*;
import net.minecraft.client.renderer.*;
import java.awt.*;
import java.util.stream.*;
import net.minecraft.client.network.*;
import client.utils.*;
import client.event.listeners.*;
import net.minecraft.network.play.server.*;
import java.util.*;

public class AdminCheckerTest3 extends Module
{
    private final TimeHelper timer;
    private final TimeHelper timer2;
    private String currentName;
    public NumberSetting checkswitchdelay;
    public static int lastAdminSize;
    private final TimeHelper timer3;
    int i;
    int time;
    public static int index;
    int color;
    private final ArrayList<String> adminsforcheck;
    private final ArrayList<String> checkedAdmins;

    NumberSetting soundTime;
    BooleanSetting sound;
    NumberSetting scaling;
    private final CFontRenderer font;
    ModeSetting noticeMode;
    BooleanSetting autotoggle;
    BooleanSetting autoShutdown;
    ModeSetting mode;

    public AdminCheckerTest3() {
        super("AdminCheckerTest3", 0, Category.MISC);
        this.timer = new TimeHelper();
        this.timer2 = new TimeHelper();
        this.timer3 = new TimeHelper();
        this.i = 0;
        this.time = 0;
        this.color = -1;
        this.adminsforcheck = new ArrayList<String>();
        this.checkedAdmins = new ArrayList<String>();
       this.font = Fonts.elliot18;
    }

    @Override
    public void init() {
        super.init();
        this.checkswitchdelay = new NumberSetting("Check Switch Delay", 100.0, 10.0, 1000.0, 1.0);
        this.noticeMode = new ModeSetting("NoticeMode", "Display", new String[] { "Display" });
        this.sound = new BooleanSetting("Sound", true);
        this.mode = new ModeSetting("Mode", "Rank", new String[] { "Rank", "Tell" ,"Ban"});
        this.soundTime = new NumberSetting("Sound Time", 50.0, 10.0, 200.0, 1.0);
        this.scaling = new NumberSetting("Size", 1.0, 1.0, 4.0, 2.0);
        this.autotoggle = new BooleanSetting("Auto Toggle", false);
        this.autoShutdown = new BooleanSetting("Auto Shutdown", false);
        this.addSetting(this.checkswitchdelay, this.sound, this.soundTime, this.noticeMode, this.scaling, this.autotoggle, this.autoShutdown, this.mode);
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventRender2D && this.noticeMode.getMode().equals("Display")) {
            final double scale = 0.0018 + this.scaling.getValue();
            if (this.checkedAdmins == null) {
                return;
            }
            if (!this.checkedAdmins.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, scale);
                this.color = TwoColoreffect(Color.RED, Color.WHITE, Math.abs(System.currentTimeMillis() / 2L) / 100.0 + 0.1275).getRGB();
                this.font.drawStringWithShadow("Admin INC " + this.checkedAdmins + " " + this.checkedAdmins.size(), 3.0 / scale, 70 / scale, this.color);
                GlStateManager.popMatrix();
            }
            else {
                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, scale);
                this.color = Color.WHITE.getRGB();
                this.font.drawStringWithShadow("No Admins" + this.checkedAdmins + " " + this.checkedAdmins.size(), 3.0 / scale, 70 / scale, this.color);
                GlStateManager.popMatrix();
            }
        }
        if (e instanceof EventUpdate) {
            for (final NetworkPlayerInfo info : AdminCheckerTest.mc.getNetHandler().getPlayerInfoMap()) {
                for (final String admin : this.getAdministrators()) {
                    if(currentName != null) {
                        if (info.getGameProfile().getName().contains(currentName))
                            currentName = null;
                    }

                    if (info.getGameProfile().getName().contains(admin)) {
                        if(checkedAdmins.contains(admin))
                            checkedAdmins.remove(admin);
                        this.checkedAdmins.add(admin);
                    }
                }
            }

            if (this.timer2.hasReached(this.checkswitchdelay.getValue())) {
                if (AdminCheckerTest.mc.thePlayer == null) {
                    return;
                }
                ++AdminCheckerTest.index;
                this.timer2.reset();
                if (AdminCheckerTest.index >= this.getAdministrators().length) {
                    ChatUtils.printChat("Finished checking");
                    if (this.autotoggle.enable) {
                        this.toggle();
                    }
                    AdminCheckerTest.index = 0;
                }
                this.currentName = this.getAdministrators()[AdminCheckerTest.index];
                if (this.mode.getMode().equalsIgnoreCase("Rank")) {
                    AdminCheckerTest.mc.thePlayer.sendChatMessage("/rank " + this.currentName);
                }
                else if (this.mode.getMode().equalsIgnoreCase("Tell")) {
                    if(currentName==null)
                        return;
                    AdminCheckerTest.mc.thePlayer.sendChatMessage("/tell " + this.currentName + " #If there is ImbC only, he abuses compromised account ban.");
                }else if(mode.getMode().equalsIgnoreCase("Ban")){
                    mc.thePlayer.sendChatMessage("/ban " + currentName);
                }
            }
            if (AdminCheckerTest.lastAdminSize > this.checkedAdmins.size()) {
                ++this.time;
                if (this.sound.enable && this.time <= this.soundTime.getValue()) {
                    AdminCheckerTest.mc.thePlayer.playSound("random.pop", 0.5f, 1.0f);
                }
                else {
                    this.time = 0;
                    AdminCheckerTest.lastAdminSize = this.checkedAdmins.size();
                }
            }
            this.setTag(String.valueOf(this.checkedAdmins.size()));
            if (!this.checkedAdmins.isEmpty()) {
                if (this.autoShutdown.enable) {
                    AdminCheckerTest.mc.shutdown();
                }
                ++this.i;
                if (AdminCheckerTest.lastAdminSize < this.checkedAdmins.size()) {
                    this.i = 0;
                    AdminCheckerTest.lastAdminSize = this.checkedAdmins.size();
                }
                if (this.sound.enable && this.i < this.soundTime.getValue()) {
                    AdminCheckerTest.mc.thePlayer.playSound("random.orb", 0.2f, 1.0f);
                }
            }
            else {
                this.i = 0;
            }
        }
        if (e instanceof EventPacket) {
            final EventPacket event = (EventPacket)e;
            if (event.isIncoming() && event.getPacket() instanceof S02PacketChat) {
                final S02PacketChat packet = (S02PacketChat)event.getPacket();
                if (packet.getChatComponent().toString().contains("No player was found") && !packet.getChatComponent().toString().contains("TranslatableComponent")) {
                    ChatUtils.printChat("Debug:" + this.currentName + " が見つかりました！");
                    if(checkedAdmins.contains(currentName))
                        checkedAdmins.remove(currentName);
                    this.checkedAdmins.add(this.currentName);
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.checkedAdmins.clear();
        this.timer.reset();
    }

    public String[] getAdministrators() {
        return new String[] {
                "Agypagy",
                "BasicAly",
                "MrJack",
                "GunOverdose",
                "Pyachi2002",
                "Rinjani",
                "naqare",
                "ACrispyTortilla",
                "Hughzaz",
                "Moshyn",
                "Navarr",
                "ShadowLAX",
                "Bupin",
                "Xhat",
                "EnderMCx",
                "WTDpuddles",
                "Daggez",
                "TurtleCobra",
                "OrcaHedral",

        };
    }
    public static Color TwoColoreffect(final Color color, final Color color2, double delay) {
        if (delay > 1.0) {
            final double n2 = delay % 1.0;
            delay = (((int)delay % 2 == 0) ? n2 : (1.0 - n2));
        }
        final double n3 = 1.0 - delay;
        return new Color((int)(color.getRed() * n3 + color2.getRed() * delay), (int)(color.getGreen() * n3 + color2.getGreen() * delay), (int)(color.getBlue() * n3 + color2.getBlue() * delay), (int)(color.getAlpha() * n3 + color2.getAlpha() * delay));
    }

    static {
        AdminCheckerTest.lastAdminSize = 0;
    }
}
