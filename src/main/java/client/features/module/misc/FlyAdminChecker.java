package client.features.module.misc;

import client.Client;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.ChatUtils;
import client.utils.TimeHelper;
import client.utils.font.CFontRenderer;
import client.utils.font.Fonts;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class FlyAdminChecker extends Module {
    private final TimeHelper timer= new TimeHelper();
    private final TimeHelper timer2 = new TimeHelper();
    private String currentName;
    public NumberSetting checkswitchdelay;
    public static int lastAdminSize =0;
    private final TimeHelper timer3 = new TimeHelper();
    int i = 0;
    int time =0;
    public static int index;
    int color = -1;
    private final ArrayList<String> adminsforcheck = new ArrayList<>();
    private final ArrayList<String> checkedAdmins = new ArrayList<>();
    NumberSetting soundTime;
    BooleanSetting sound;
    NumberSetting scaling;
    private final CFontRenderer font = Fonts.elliot18;

    ModeSetting noticeMode;
    BooleanSetting autotoggle;
    public FlyAdminChecker() {
        super("FlyAdminChecker", 0, Category.MISC);
    }
    public void init(){
        super.init();
        checkswitchdelay =new NumberSetting("Check Switch Delay", 100, 10, 1000, 1);
        this.noticeMode = new ModeSetting("NoticeMode", "Display", new String[]{ "Display"});
        sound= new BooleanSetting("Sound" ,true);
        this.soundTime = new NumberSetting("Sound Time", 50,10,200,1);
        this.scaling = new NumberSetting("Size", 1.0F,1.0, 4.0, 2.0);;
        autotoggle = new BooleanSetting("Auto Toggle",true);
        addSetting(checkswitchdelay,sound,soundTime,noticeMode,scaling,autotoggle);
    }
    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventRender2D) {
            if(noticeMode.getMode().equals("Display")){
                double scale = (0.0018 + (double) this.scaling.getValue());
                if (!this.checkedAdmins.isEmpty() ) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(scale,scale,scale);
                    color = TwoColoreffect(Color.RED,Color.WHITE,Math.abs(System.currentTimeMillis() / 2L) / 100.0 + 3.0F * (1 * 2.55) / 60).getRGB();
                    font.drawStringWithShadow("Admin INC " + checkedAdmins + " " + checkedAdmins.size(), (3 / scale), 30 / scale, color);
                    GlStateManager.popMatrix();
                } else {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(scale,scale,scale);
                    color = Color.WHITE.getRGB();
                    font.drawStringWithShadow("No Admins" + checkedAdmins + " " + checkedAdmins.size(), (3 / scale), 30 / scale, color);
                    GlStateManager.popMatrix();
                }

            }
        }
if(e instanceof EventUpdate) {
    if (timer2.hasReached(checkswitchdelay.getValue())) {
        if (mc.thePlayer == null)
            return;
        index++;
        timer2.reset();
        if (index >= getAdministrators().length) {
            ChatUtils.printChat("Finished checking");
            if (autotoggle.enable) {
                toggle();
            }
            index = 0;
        }

        currentName = getAdministrators()[index];
        mc.thePlayer.sendChatMessage("/rank " + currentName);


    }
    if (lastAdminSize > checkedAdmins.size()) {
        time++;
        if (sound.enable && time <= soundTime.getValue()) {
            mc.thePlayer.playSound("random.pop", 0.5F, 1.0F);
        } else {
            time = 0;
            lastAdminSize = checkedAdmins.size();
        }

    }
    setTag(String.valueOf(checkedAdmins.size()));
    if (!this.checkedAdmins.isEmpty()) {

        i++;
        if (lastAdminSize < checkedAdmins.size()) {
            i = 0;
            lastAdminSize = checkedAdmins.size();
        }
        if (sound.enable) {
            if (i < soundTime.getValue()) {
                mc.thePlayer.playSound("random.orb", 0.2F, 1.0F);
            }

        }
    } else {
        i = 0;
    }
}

        if(e instanceof EventPacket) {
            EventPacket event = ((EventPacket)e);
            if(event.isIncoming()) {
                if(event.getPacket() instanceof S02PacketChat) {
                    S02PacketChat packet = (S02PacketChat) event.getPacket();
                    if (packet.getChatComponent().toString().contains("No player was found") && !packet.getChatComponent().toString().contains("TranslatableComponent")) {
                        ChatUtils.printChat("Debug:" + currentName +" が見つかりました！");
                        checkedAdmins.add(currentName);

                    }
                }
            }
        }
    }
    public void onEnable(){
        super.onEnable();
        checkedAdmins.clear();

        timer.reset();
    }


    public String[] getAdministrators() {
        return new String[] {
                "ImbC","Carrots386","DJ_Pedro","ImAbbyy","LangScott","Sevy13"
        };
    }
    public static Color TwoColoreffect(final Color color, final Color color2, double delay) {
        if (delay > 1.0) {
            final double n2 = delay % 1.0;
            delay = (((int) delay % 2 == 0) ? n2 : (1.0 - n2));
        }
        final double n3 = 1.0 - delay;
        return new Color((int) (color.getRed() * n3 + color2.getRed() * delay), (int) (color.getGreen() * n3 + color2.getGreen() * delay), (int) (color.getBlue() * n3 + color2.getBlue() * delay), (int) (color.getAlpha() * n3 + color2.getAlpha() * delay));
    }
}
