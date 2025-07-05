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
        holder.doAsGuiWidget(GuiWidget::updateSize);
        holder.getWidgets().forEach(GuiWidget::updateSize);

        holder.onMakeLayoutStart();
        holder.getWidgets().stream().filter(it -> !(it instanceof IWidgetHolder)).forEach(GuiWidget::onMakeLayoutStart);

        holder.doAsGuiWidget(GuiWidget::updatePos);
        holder.getWidgets().forEach(GuiWidget::updatePos);

        holder.onMakeLayoutFinish();
        holder.getWidgets().stream().filter(it -> !(it instanceof IWidgetHolder)).forEach(GuiWidget::onMakeLayoutFinish);
    }
}
