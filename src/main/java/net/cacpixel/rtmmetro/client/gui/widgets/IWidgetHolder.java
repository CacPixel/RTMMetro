package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface IWidgetHolder
{
    List<GuiWidget> getWidgets();

    GuiScreenAdvanced getScreen();

    default IWidgetHolder add(GuiWidget... widgets)
    {
//        this.getWidgets().removeIf(w -> Arrays.stream(widgets).anyMatch(widgetIn -> widgetIn.getId() == w.getId()));
        this.getWidgets().addAll(Arrays.asList(widgets));
        return this;
    }

    default void onUpdate()
    {
        // update other holder
        this.getWidgets().forEach(GuiWidget::onWidgetUpdate);
        this.forEachHolder(IWidgetHolder::onUpdate);
    }

    @SuppressWarnings("unchecked")
    default void forEachHolder(Consumer<? super IWidgetHolder> consumer)
    {
        this.getWidgets().stream().filter(w -> w instanceof IWidgetHolder)
                .collect(Collectors.toList()).forEach((Consumer<? super GuiWidget>) consumer);
    }

    default GuiTextFieldAdvanced getCurrentTextField()
    {
        return this.getTextFieldList().stream().filter(GuiTextFieldAdvanced::isFocused).findFirst().orElse(null);
    }

    default List<GuiTextFieldAdvanced> getTextFieldList()
    {
        return this.getAllWidgetFromClass(GuiTextFieldAdvanced.class);
    }

    default List<GuiButtonAdvanced> getButtonList()
    {
        return this.getAllWidgetFromClass(GuiButtonAdvanced.class);
    }

    default List<GuiWidget> getAllWidgets()
    {
        List<GuiWidget> list = new ArrayList<>();
        this.forEachHolder(holder -> list.addAll(holder.getAllWidgets()));
        list.addAll(this.getWidgets());
        return list;
    }

    default <T extends GuiWidget> List<T> getWidgetFromClass(Class<T> clazz)
    {
        return this.getWidgetFromClass(clazz, false);
    }

    default <T extends GuiWidget> List<T> getAllWidgetFromClass(Class<T> clazz)
    {
        return this.getWidgetFromClass(clazz, true);
    }

    @SuppressWarnings("unchecked")
    default <T extends GuiWidget> List<T> getWidgetFromClass(Class<T> clazz, boolean reentrant)
    {
        List<T> ret = new ArrayList<>();
        if (reentrant)
            this.forEachHolder(holder -> ret.addAll(holder.getWidgetFromClass(clazz, true)));
        this.getWidgets().stream().filter(clazz::isInstance).forEach(b -> ret.add((T) b));
        return ret;
    }
}
