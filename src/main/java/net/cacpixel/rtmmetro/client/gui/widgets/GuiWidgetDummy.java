package net.cacpixel.rtmmetro.client.gui.widgets;

import java.util.function.IntSupplier;

public class GuiWidgetDummy extends GuiWidget
{
    public GuiWidgetDummy(IWidgetHolder holder, int id, int x, int y, int width, int height)
    {
        super(holder, id, x, y, width, height);
    }

    public GuiWidgetDummy(IWidgetHolder holder, int id, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height)
    {
        super(holder, id, x, y, width, height);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {

    }
}
