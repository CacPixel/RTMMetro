package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.Image;

import java.util.function.IntSupplier;

public class GuiImage extends GuiWidget
{
    Image image;

    public GuiImage(IWidgetHolder holder, int id, IntSupplier xSupplier,
                    IntSupplier ySupplier, IntSupplier widthSupplier,
                    IntSupplier heightSupplier, Image image)
    {
        super(holder, id, xSupplier, ySupplier, widthSupplier, heightSupplier);
        this.image = image;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        CacGuiUtils.drawTexturedModalRect(x, y, width, height, zLevel, new Image(image));
    }
}
