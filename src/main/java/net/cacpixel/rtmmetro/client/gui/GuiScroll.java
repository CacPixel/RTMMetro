package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.client.gui.widgets.IGuiWidget;
import net.cacpixel.rtmmetro.client.gui.widgets.IWidgetHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiScroll implements IWidgetHolder
{
    public GuiScreenAdvanced pScr;
    List<IGuiWidget> widgets = new ArrayList<>();


    public GuiScroll(GuiScreenAdvanced pScr, IGuiWidget... widgets)
    {
        this.pScr = pScr;
        this.widgets.addAll(Arrays.asList(widgets));
    }

    public void drawScrollBefore()
    {

    }

    public void drawScroll()
    {

    }

    public void drawScrollAfter()
    {

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
