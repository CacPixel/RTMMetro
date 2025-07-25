package net.cacpixel.rtmmetro.client.gui;

import org.lwjgl.input.Mouse;

public class MouseGrabber
{
    public static final MouseGrabber INSTANCE = new MouseGrabber();
    public int prevMouseXForScroll;
    public int prevMouseYForScroll;
    private boolean grabbed = false;

    public void grabMouseCursor()
    {
        Mouse.setGrabbed(true);
        this.prevMouseXForScroll = 0;
        this.prevMouseYForScroll = 0;
        grabbed = true;
    }

    public void ungrabMouseCursor()
    {
        Mouse.setGrabbed(false);
        grabbed = false;
    }

    public void mouseXYChange()
    {
        this.prevMouseXForScroll = Mouse.getDX();
        this.prevMouseYForScroll = Mouse.getDY();
    }

    public boolean isGrabbed()
    {
//        return grabbed;
        return Mouse.isGrabbed();
    }
}
