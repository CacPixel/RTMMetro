package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.util.RTMMetroException;
import net.minecraft.client.gui.ScaledResolution;

import java.util.EmptyStackException;
import java.util.Stack;

public class MouseScissorManager
{
    public GuiScreenAdvanced screen;
    private final Stack<ScissorParam> scissorStack = new Stack<>();
    private final Stack<ScissorParam> disabledScissorStack = new Stack<>();
    private boolean started = false;

    public MouseScissorManager(GuiScreenAdvanced screen)
    {
        this.screen = screen;
    }

    public void checkStart()
    {
        if (!started)
        {
            throw new RTMMetroException("Not started! can not be used here!");
        }
    }

    public boolean isMouseInside(ScissorParam param, int x, int y, int width, int height, int mouseX, int mouseY)
    {
        checkStart();
        if (param == null)
        {
            return false;
        }
        boolean flag1 = CacGuiUtils.isMouseInside(param.x, param.y, param.width, param.height, mouseX, mouseY);
        boolean flag2 = CacGuiUtils.isMouseInside(x, y, width, height, mouseX, mouseY);
        return flag1 && flag2;
    }

    public boolean isMouseInside(int x, int y, int width, int height, int mouseX, int mouseY)
    {
        ScissorParam param = peek();
        return isMouseInside(param, x, y, width, height, mouseX, mouseY);
    }

    /* 按照提供的ScissorParam和当前栈顶ScissorParam比较、限制范围后再push */
    public void push(ScissorParam param)
    {
        checkStart();
        if (!disabledScissorStack.empty())
        {
            enableAll();
        }
        ScissorParam old = peek();
        if (old != null && param != null && !param.pushOrigin)
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
        checkStart();
        scissorStack.push(param);
    }

    public ScissorParam pop()
    {
        checkStart();
        ScissorParam popped;
        try
        {
            popped = scissorStack.pop();
        }
        catch (EmptyStackException e)
        {
            throw new RTMMetroException("MouseScissorManager stack is empty, MouseScissorManager.pop() too much!", e);
        }
        return popped;
    }

    public ScissorParam peek()
    {
        checkStart();
        return scissorStack.empty() ? getFullSizeParam() : scissorStack.peek();
    }

    public ScissorParam getFullSizeParam()
    {
        ScaledResolution sr = new ScaledResolution(screen.mc);
        return new ScissorParam(0, 0, sr.getScaledWidth(), sr.getScaledHeight());
    }

    public ScissorParam getScreenSizeParam()
    {
        return new ScissorParam(screen.x, screen.y, screen.width, screen.height);
    }

    private void checkStackEmpty()
    {
        if (!scissorStack.empty())
            throw new RTMMetroException("MouseScissorManager stack is not empty, MouseScissorManager.push() too much!");
    }

    private void forceDisableScissor()
    {
        if (!scissorStack.empty())
        {
            this.scissorStack.clear();
        }
        if (!disabledScissorStack.empty())
        {
            this.disabledScissorStack.clear();
        }
    }

    public void disable(int count)
    {
        checkStart();
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
    }

    public int disableAll()
    {
        checkStart();
        int size = scissorStack.size();
        disable(size);
        return size;
    }

    public void enable(int count)
    {
        checkStart();
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
    }

    public void enableAll()
    {
        checkStart();
        enable(disabledScissorStack.size());
    }

    public void start()
    {
        if (started)
            throw new RTMMetroException("Already started!");
        started = true;
    }

    public void end()
    {
        if (!started)
            throw new RTMMetroException("Not started!");
        started = false;
        checkStackEmpty();
        forceDisableScissor();
    }
}
