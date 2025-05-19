package net.cacpixel.rtmmetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class ScissorManager
{
    public static final ScissorManager INSTANCE = new ScissorManager();

    public ScissorManager()
    {

    }
    public void apply(int xIn, int yIn, int wIn, int hIn)
    {
        if (this.getScreen() == null)
            return;
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int f = sr.getScaleFactor();
        int screenTX = (int) this.getScreen().translationX;
        int screenTY = (int) this.getScreen().translationY;
        int screenScaleX = (int) this.getScreen().scaleX;
        int screenScaleY = (int) this.getScreen().scaleY;
        int x = f * (screenTX + xIn * screenScaleX);
        int y = f * (sr.getScaledHeight() - screenTY - (yIn + hIn) * screenScaleY);
        int w = f * (wIn * screenScaleX);
        int h = f * (hIn * screenScaleY);
        GL11.glScissor(x, y, w, h);
    }

    public GuiScreenAdvanced getScreen()
    {
        return Minecraft.getMinecraft().currentScreen instanceof GuiScreenAdvanced ?
                ((GuiScreenAdvanced) Minecraft.getMinecraft().currentScreen) : null;
    }

}
