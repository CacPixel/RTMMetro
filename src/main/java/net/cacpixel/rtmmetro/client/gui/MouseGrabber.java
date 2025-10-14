package net.cacpixel.rtmmetro.client.gui;

import org.lwjgl.input.Mouse;

public class MouseGrabber
{
    public static final MouseGrabber INSTANCE = new MouseGrabber();
    public int dx = 0;
    public int dy = 0;
    public int x = 0;
    public int y = 0;
    private boolean grabbed = false;

    public void grabMouseCursor()
    {
        Mouse.setGrabbed(true);
        dx = 0;
        dy = 0;
        x = 0;
        y = 0;
        grabbed = true;
    }

    public void ungrabMouseCursor()
    {
        Mouse.setGrabbed(false);
        grabbed = false;
    }

    public void mouseXYChange()
    {
        this.dx = Mouse.getDX();
        this.dy = Mouse.getDY();
        x += dx;
        y += dy;
    }

    public int[] step(int step)
    {
        int[] ret = new int[]{0, 0};
        mouseXYChange();
        if (x >= step || x <= -step)
        {
            ret[0] = x;
            x = 0;
        }
        if (y >= step || y <= -step)
        {
            ret[1] = y;
            y = 0;
        }
        return ret; // [x, y]
    }

    public boolean isGrabbed()
    {
//        return grabbed;
        return Mouse.isGrabbed();
    }
}
