package client.ui.clicckgui2;

import client.features.module.Module;
import client.features.module.ModuleManager;
import client.features.module.render.ClickGUI;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGui extends GuiScreen {

    private final List<ClickGuiWindow> windows = new ArrayList<>();

    public ClickGui(int screen) {
        double currentX = 50;

        for (Module.Category c : Module.Category.values()) {
            windows.add(new ClickGuiWindow((float) currentX,  30, c));
            currentX += 150;
        }
    }
    @Override
    public void initGui() {
        windows.forEach(ClickGuiWindow::init);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (((ClickGUI)ModuleManager.getModulebyClass(ClickGUI.class)).autoGuiScale.enable) {
            float size = (float) Display.getWidth()/2048;
            if (mc.gameSettings.guiScale == 3) {
                size /= 1.5;
            }
        GlStateManager.scale(size, size, 1);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        windows.forEach(m -> m.render(mouseX, mouseY, partialTicks));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (((ClickGUI)ModuleManager.getModulebyClass(ClickGUI.class)).autoGuiScale.enable) {
            float size = (float)Display.getWidth()/2048;
            if (mc.gameSettings.guiScale == 3) {
                size /= 1.5;
            }
            mouseX /= size;
            mouseY /= size;

        }
        int finalMouseY = mouseY;
        int finalMouseX = mouseX;
        windows.forEach(m -> m.mouseClicked(finalMouseX, finalMouseY, mouseButton));
         super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (((ClickGUI)ModuleManager.getModulebyClass(ClickGUI.class)).autoGuiScale.enable) {
            float size = (float)Display.getWidth()/2048;
            if (mc.gameSettings.guiScale == 3) {
                size /=1.5;
            }
            mouseX /= size;
            mouseY /= size;
        }
        int finalMouseX = mouseX;
        int finalMouseY = mouseY;
        windows.forEach(m -> m.mouseReleased(finalMouseX, finalMouseY, mouseButton));
         super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        windows.forEach(m -> m.keyPressed(typedChar, keyCode));
         super.keyTyped(typedChar, keyCode);
    }

  @Override  public void onGuiClosed() {
      ModuleManager.toggle(ClickGUI.class);

        windows.forEach(m -> m.onClose());
       super.onGuiClosed();
    }
}
