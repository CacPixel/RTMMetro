package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.client.gui.widgets.IWidgetHolder;

public abstract class GuiLayoutBase
{
    public IWidgetHolder holder;

    public GuiLayoutBase(IWidgetHolder holder)
    {
        this.holder = holder;
    }

    public abstract void makeLayout();
}
