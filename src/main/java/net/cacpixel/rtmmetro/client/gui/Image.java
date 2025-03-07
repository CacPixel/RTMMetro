package net.cacpixel.rtmmetro.client.gui;

import net.minecraft.util.ResourceLocation;

public class Image
{
    public ResourceLocation location;
    public int u = 0;
    public int v = 0;
    public int uWidth = 64;
    public int vHeight = 64;
    public int textureWidth = 64;
    public int textureHeight = 64;
    public int color = 0xFFFFFF;

    public Image(ResourceLocation location, int u, int v, int uWidth, int vHeight,
                 int textureWidth, int textureHeight, int color)
    {
        this.location = location;
        this.u = u;
        this.v = v;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.color = color;
    }

    public Image(ResourceLocation location)
    {
        this.location = location;
    }

    public Image(Image image)
    {
        this(image.location, image.u, image.v, image.uWidth, image.vHeight, image.textureWidth, image.textureHeight,
                image.color);
    }

    public Image setColor(int color)
    {
        this.color = color;
        return this;
    }
}
