package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class GuiWidgetBundle extends GuiWidget implements IWidgetHolder
{
    public List<GuiWidget> widgets = new ArrayList<>();

    public GuiWidgetBundle(GuiScreenAdvanced pScr, int id, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height,
                           GuiWidget... widgets)
    {
        super(pScr, id, x, y, width, height);
        this.add(widgets);
    }

    public GuiWidgetBundle(GuiScreenAdvanced pScr, int id, GuiWidget... widgets)
    {
        this(pScr, id, GuiWidget.ZERO, GuiWidget.ZERO, GuiWidget.ZERO, GuiWidget.ZERO, widgets);
    }

    @Override
    public GuiWidgetBundle add(GuiWidget... widgets)
    {
        GuiWidgetBundle ret = (GuiWidgetBundle) IWidgetHolder.super.add(widgets);
        ret.widgets.removeIf(w -> w == this); // avoid add itself, it will cause infinity loop
        return ret;
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

    public boolean isMouseInside()
    {
        return this.isPositionIndependent() ? this.widgets.stream().anyMatch(GuiWidget::isMouseInside) : super.isMouseInside();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.widgets.forEach(x -> x.draw(mouseX, mouseY, partialTicks));
    }

    public List<GuiWidget> getWidgets()
    {
        return this.widgets;
    }

    @Override
    public GuiScreenAdvanced getScreen()
    {
        return pScr;
    }
}
