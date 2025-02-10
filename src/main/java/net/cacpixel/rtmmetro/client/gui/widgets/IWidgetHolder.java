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
        return this.getAllWidgetFromClass(GuiTextFieldAdvanced.class);
    }

    default List<GuiButtonAdvanced> getButtonList()
    {
        return this.getAllWidgetFromClass(GuiButtonAdvanced.class);
    }

    default List<IGuiWidget> getAllWidgets()
    {
        List<IGuiWidget> list = new ArrayList<>();
        this.forEachHolder(holder -> list.addAll(holder.getAllWidgets()));
        list.addAll(this.getWidgets());
        return list;
    }

    default <T extends IGuiWidget> List<T> getWidgetFromClass(Class<T> clazz)
    {
        return this.getWidgetFromClass(clazz, false);
    }

    default <T extends IGuiWidget> List<T> getAllWidgetFromClass(Class<T> clazz)
    {
        return this.getWidgetFromClass(clazz, true);
    }

    @SuppressWarnings("unchecked")
    default <T extends IGuiWidget> List<T> getWidgetFromClass(Class<T> clazz, boolean reentrant)
    {
        List<T> ret = new ArrayList<>();
        if (reentrant)
            this.forEachHolder(holder -> ret.addAll(holder.getWidgetFromClass(clazz, true)));
        this.getWidgets().stream().filter(clazz::isInstance).forEach(b -> ret.add((T) b));
        return ret;
    }
}
