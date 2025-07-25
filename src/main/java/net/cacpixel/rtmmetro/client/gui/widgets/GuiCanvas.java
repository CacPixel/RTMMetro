package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.Image;

import java.util.function.IntSupplier;

public class GuiCanvas extends GuiWidget
{
    private IDrawCallback callback = null;
    private Image image;

    public GuiCanvas(IWidgetHolder holder, int id, int x, int y, int width, int height)
    {
        super(holder, id, x, y, width, height);
    }

    public GuiCanvas(IWidgetHolder holder, int id, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height)
    {
        super(holder, id, x, y, width, height);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (image != null)
        {
            image.drawImage(x, y, width, height, zLevel);
        }
        if (callback != null)
        {
            callback.call(mouseX, mouseY, partialTicks);
        }
    }

    public GuiCanvas setCallback(IDrawCallback callback)
    {
        this.callback = callback;
        return this;
    }

    public GuiCanvas setImage(Image image)
    {
        this.image = image;
        return this;
    }

    public interface IDrawCallback
    {
        void call(int mouseX, int mouseY, float partialTicks);
    }
}
