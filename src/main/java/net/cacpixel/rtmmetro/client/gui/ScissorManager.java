package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.util.RTMMetroException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.util.EmptyStackException;
import java.util.Stack;

public class ScissorManager
{
    public GuiScreenAdvanced screen;
    private final Stack<ScissorParam> scissorStack = new Stack<>();
    private final Stack<ScissorParam> disabledScissorStack = new Stack<>();
    private boolean glScissorEnabled = false;

    public ScissorManager(GuiScreenAdvanced screen)
    {
        this.screen = screen;
    }

    private void glDisableScissor()
    {
        if (glScissorEnabled)
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        glScissorEnabled = false;
    }

    private void glEnableScissor()
    {
        if (!glScissorEnabled)
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
        glScissorEnabled = true;
    }

    private void apply(int xIn, int yIn, int wIn, int hIn)
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

    private void apply(ScissorParam param)
    {
        if (param != null)
        {
            this.apply(param.x, param.y, param.width, param.height);
        }
        else
        {
            glDisableScissor();
        }
    }

    public void apply()
    {
        glEnableScissor();
        apply(peek());
    }

    /* 按照提供的ScissorParam和当前栈顶ScissorParam比较、限制范围后再push */
    public void push(ScissorParam param)
    {
        if (!disabledScissorStack.empty())
        {
            enableAll();
        }
        ScissorParam old = peek();
        if (old != null && param != null)
        {
            int sxNew = param.x;
            int syNew = param.y;
            int exNew = param.x + param.width - 1;
            int eyNew = param.y + param.height - 1;
            int sxOld = old.x;
            int syOld = old.y;
            int exOld = old.x + old.width - 1;
            int eyOld = old.y + old.height - 1;
            int sx = Math.max(sxNew, sxOld);
            int sy = Math.max(syNew, syOld);
            int ex = Math.min(exNew, exOld);
            int ey = Math.min(eyNew, eyOld);
            pushOrigin(new ScissorParam(sx, sy, ex - sx + 1, ey - sy + 1));
        }
        else
        {
            pushOrigin(param);
        }
    }

    public void pushOrigin(ScissorParam param)
    {
        scissorStack.push(param);
    }

    public ScissorParam pop()
    {
        ScissorParam popped;
        try
        {
            popped = scissorStack.pop();
        }
        catch (EmptyStackException e)
        {
            throw new RTMMetroException("ScissorManager stack is empty, ScissorManager.pop() too much!", e);
        }
        if (!scissorStack.empty())
            apply();
        else
            glDisableScissor();
        return popped;
    }

    public ScissorParam peek()
    {
        return scissorStack.empty() ? null : scissorStack.peek();
    }

    public void checkStackEmpty()
    {
        if (!scissorStack.empty())
            throw new RTMMetroException("ScissorManager stack is not empty, ScissorManager.push() too much!");
    }

    public void forceDisableScissor()
    {
        if (!scissorStack.empty())
        {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            this.scissorStack.clear();
        }
        if (!disabledScissorStack.empty())
        {
            this.disabledScissorStack.clear();
        }
    }

    public void disable(int count)
    {
        try
        {
            for (int i = 0; i < count; i++)
            {
                ScissorParam popped = scissorStack.pop();
                disabledScissorStack.push(popped);
            }
        }
        catch (EmptyStackException e)
        {
            throw new RTMMetroException("scissorStack is empty, count too large!", e);
        }
        if (!scissorStack.empty())
            apply();
        else
            glDisableScissor();
    }

    public int disableAll()
    {
        int size = scissorStack.size();
        disable(size);
        return size;
    }

    public void enable(int count)
    {
        try
        {
            for (int i = 0; i < count; i++)
            {
                ScissorParam popped = disabledScissorStack.pop();
                scissorStack.push(popped);
            }
        }
        catch (EmptyStackException e)
        {
            throw new RTMMetroException("disabledScissorStack is empty, count too large!", e);
        }
        if (!scissorStack.empty())
            apply();
        else
            glDisableScissor();
    }

    public void enableAll()
    {
        enable(disabledScissorStack.size());
    }
}
