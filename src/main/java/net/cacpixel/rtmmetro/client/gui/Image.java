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

    public Image(String locationStr, int u, int v, int uWidth, int vHeight,
                 int textureWidth, int textureHeight, int color)
    {
        this.location = GuiTheme.getCurrentResourceLocation(locationStr);
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

    public Image(String locationStr)
    {
        this(GuiTheme.getCurrentResourceLocation(locationStr));
    }

    public Image(Image image)
    {
        this(image.location, image.u, image.v, image.uWidth, image.vHeight, image.textureWidth, image.textureHeight,
                image.color);
    }

    public void drawImage(int x, int y, int width, int height, float zLevel)
    {
        CacGuiUtils.bindTexture(location);
        CacGuiUtils.glColor(color);
        CacGuiUtils.drawTexturedModalRect(x, y, width, height, this.u, this.v,
                this.uWidth, this.vHeight, zLevel, this.textureWidth, this.textureHeight);
    }

    public Image setColor(int color)
    {
        this.color = color;
        return this;
    }

    public Image setU(int u)
    {
        this.u = u;
        return this;
    }

    public Image setV(int v)
    {
        this.v = v;
        return this;
    }

    public Image setuWidth(int uWidth)
    {
        this.uWidth = uWidth;
        return this;
    }

    public Image setvHeight(int vHeight)
    {
        this.vHeight = vHeight;
        return this;
    }

    public Image setTextureWidth(int textureWidth)
    {
        this.textureWidth = textureWidth;
        return this;
    }

    public Image setTextureHeight(int textureHeight)
    {
        this.textureHeight = textureHeight;
        return this;
    }
}
