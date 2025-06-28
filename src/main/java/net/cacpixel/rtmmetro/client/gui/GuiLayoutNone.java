package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.client.gui.widgets.GuiWidget;
import net.cacpixel.rtmmetro.client.gui.widgets.IWidgetHolder;

public class GuiLayoutNone extends GuiLayoutBase
{
    public GuiLayoutNone(IWidgetHolder holder)
    {
        super(holder);
    }

    @Override
    public void makeLayout()
    {
        holder.updatePosAndSize();
        holder.getAllWidgets().forEach(GuiWidget::updatePosAndSize);
        holder.onMakeLayoutFinish();
        holder.getWidgets().stream().filter(it-> !(it instanceof IWidgetHolder)).forEach(GuiWidget::onMakeLayoutFinish);
    }
}
