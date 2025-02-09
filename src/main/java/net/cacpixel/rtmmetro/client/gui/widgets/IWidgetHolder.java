package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.minecraft.client.gui.GuiTextField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface IWidgetHolder
{
    List<IGuiWidget> getWidgets();

    GuiScreenAdvanced getScreen();

    default IWidgetHolder add(IGuiWidget... widgets)
    {
        this.getWidgets().addAll(Arrays.asList(widgets));
        return this;
    }

    default void onUpdate()
    {
        // update other holder
        this.forEachHolder(IWidgetHolder::onUpdate);
    }

    @SuppressWarnings("unchecked")
    default void forEachHolder(Consumer<? super IWidgetHolder> consumer)
    {
        this.getWidgets().stream().filter(w -> w instanceof IWidgetHolder)
                .collect(Collectors.toList()).forEach((Consumer<? super IGuiWidget>) consumer);
    }

    default GuiTextFieldAdvanced getCurrentTextField()
    {
        return this.getTextFieldList().stream().filter(GuiTextField::isFocused).findFirst().orElse(null);
    }

    default List<GuiTextFieldAdvanced> getTextFieldList()
    {
        List<GuiTextFieldAdvanced> textFields = new ArrayList<>();
        this.forEachHolder(holder -> textFields.addAll(holder.getTextFieldList()));
        this.getWidgets().stream().filter(w -> w instanceof GuiTextFieldAdvanced).forEach(w -> textFields.add((GuiTextFieldAdvanced) w));
        return textFields;
    }

    default List<GuiButtonAdvanced> getButtonList()
    {
        List<GuiButtonAdvanced> buttonList = new ArrayList<>();
        this.forEachHolder(holder -> buttonList.addAll(holder.getButtonList()));
        this.getWidgets().stream().filter(w -> w instanceof GuiButtonAdvanced).forEach(b -> buttonList.add((GuiButtonAdvanced) b));
        return buttonList;
    }

    default List<IGuiWidget> getAllWidgets()
    {
        List<IGuiWidget> list = new ArrayList<>();
        this.forEachHolder(holder -> list.addAll(holder.getAllWidgets()));
        list.addAll(this.getWidgets());
        return list;
    }
}
