package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.util.ModLog;
import net.cacpixel.rtmmetro.util.RTMMetroException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.util.Stack;

public class ScissorManager
{
    public static final ScissorManager INSTANCE = new ScissorManager();
    private final Stack<ScissorParam> scissorStack = new Stack<>();

    public ScissorManager()
    {

    }

    private void apply(GuiScreenAdvanced screen, int xIn, int yIn, int wIn, int hIn)
    {
        if (screen.getScreen() == null)
            return;
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int f = sr.getScaleFactor();
        int screenTX = (int) screen.getScreen().translationX;
        int screenTY = (int) screen.getScreen().translationY;
        int screenScaleX = (int) screen.getScreen().scaleX;
        int screenScaleY = (int) screen.getScreen().scaleY;
        int x = f * (screenTX + xIn * screenScaleX);
        int y = f * (sr.getScaledHeight() - screenTY - (yIn + hIn) * screenScaleY);
        int w = f * (wIn * screenScaleX);
        int h = f * (hIn * screenScaleY);
        GL11.glScissor(x, y, w, h);
    }

    public void apply(ScissorParam param)
    {
        if (param != null)
        {
            this.apply(param.screen, param.x, param.y, param.width, param.height);
        }
        else
        {
            ModLog.warn("ScissorParam is null");
        }
    }

    public void push(ScissorParam param)
    {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        scissorStack.push(param);
//        ModLog.debug("scissor stack push: x %d, y %d, w %d, h %d", param.x, param.y, param.width, param.height);
        this.apply(param);
    }

    public ScissorParam pop()
    {
        ScissorParam popped;
        try
        {
            popped = scissorStack.pop();
        }
        catch (Exception e)
        {
            throw new RTMMetroException("ScissorManager stack is empty, ScissorManager.pop() too much!");
        }
        if (!scissorStack.empty())
            this.apply(scissorStack.peek());
        else
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        return popped;
    }

    public ScissorParam peek()
    {
        return scissorStack.empty() ? null : scissorStack.peek();
    }

    public void checkStackEmpty()
    {
         if (!scissorStack.isEmpty())
            throw new RTMMetroException("ScissorManager stack is not empty, ScissorManager.push() too much!");
    }

    public void forceDisableScissor()
    {
        if (!scissorStack.empty())
        {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            this.scissorStack.clear();
        }
    }
}
