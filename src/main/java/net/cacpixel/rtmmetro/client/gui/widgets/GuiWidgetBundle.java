package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiWidgetBundle implements IGuiWidget, IWidgetHolder
{
    public GuiScreenAdvanced pScr;
    public List<IGuiWidget> widgets = new ArrayList<>();

    public GuiWidgetBundle(GuiScreenAdvanced pScr, IGuiWidget... widgets)
    {
        this.pScr = pScr;
        this.add(widgets);
    }

    @Override
    public GuiWidgetBundle add(IGuiWidget... widgets)
    {
        this.widgets.addAll(Arrays.asList(widgets));
        this.widgets.removeIf(w -> w == this); // avoid add itself, it will cause infinity loop
        return this;
    }

    @Override
    public void onUpdate()
    {
        IWidgetHolder.super.onUpdate();
    }

    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton)
    {
        this.widgets.forEach(x -> x.onClick(mouseX, mouseY, mouseButton));
    }

    @Override
    public void onDrag(int mouseX, int mouseY, int mouseButton)
    {
        this.widgets.forEach(x -> x.onDrag(mouseX, mouseY, mouseButton));
    }

    @Override
    public void onScroll(int mouseX, int mouseY, int scroll)
    {
        this.widgets.forEach(x -> x.onScroll(mouseX, mouseY, scroll));
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        this.widgets.forEach(x -> x.onKeyTyped(typedChar, keyCode));
    }

    @Override
    public boolean isMouseInside()
    {
        return this.widgets.stream().anyMatch(IGuiWidget::isMouseInside);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.widgets.forEach(x -> x.draw(mouseX, mouseY, partialTicks));
    }

    @Override
    public boolean isVisible()
    {
        return this.widgets.stream().allMatch(IGuiWidget::isVisible);
    }

    @Override
    public boolean isEnabled()
    {
        return this.widgets.stream().allMatch(IGuiWidget::isEnabled);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.widgets.forEach(x -> x.setEnabled(enabled));
    }

    @Override
    public void setVisible(boolean visible)
    {
        this.widgets.forEach(x -> x.setVisible(visible));
    }

    public List<IGuiWidget> getWidgets()
    {
        return this.widgets;
    }

    @Override
    public GuiScreenAdvanced getScreen()
    {
        return pScr;
    }
}
