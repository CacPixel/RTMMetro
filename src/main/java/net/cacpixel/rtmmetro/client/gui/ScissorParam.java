package net.cacpixel.rtmmetro.client.gui;

public class ScissorParam
{
    public int x;
    public int y;
    public int width;
    public int height;

    public ScissorParam(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
    }

}
