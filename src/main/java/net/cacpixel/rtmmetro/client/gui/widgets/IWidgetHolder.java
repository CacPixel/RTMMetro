package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;

import java.util.Arrays;
import java.util.List;

public interface IWidgetHolder
{
    List<IGuiWidget> getWidgets();

    GuiScreenAdvanced getScreen();

    default IWidgetHolder add(IGuiWidget... widgets)
    {
        this.getWidgets().addAll(Arrays.asList(widgets));
        return this;
    }
}
