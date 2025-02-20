package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.util.RTMMetroException;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.cacpixel.rtmmetro.util.ModLog;
import net.cacpixel.rtmmetro.util.RTMMetroUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @SuppressWarnings("unchecked")
    default <T extends GuiWidget> T add(Class<T> clazz, int id, IntSupplier x, IntSupplier y,
                                        IntSupplier width, IntSupplier height, Object... args)
    {
        try
        {
            List<Object> params = new ArrayList<>();
            params.add(this);
            params.add(id);
            params.add(x);
            params.add(y);
            params.add(width);
            params.add(height);
            params.addAll(Arrays.asList(args));
            Constructor<?> ctor = Arrays.stream(clazz.getConstructors()).filter(constructor -> {
                Class<?>[] constructorParamTypes = constructor.getParameterTypes();
                if (constructorParamTypes.length == params.size())
                {
                    for (int i = 0; i < constructorParamTypes.length; i++)
                    {
                        Class<?> c = constructorParamTypes[i];
                        c = RTMMetroUtils.getClassByPrimitiveType(c);
                        if (!c.isInstance(params.get(i)))
                        {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }).findFirst().orElseThrow(() -> new RTMMetroException("No constructor paired in this class with parameter type: " +
                    Arrays.toString(params.stream().map(Object::getClass).toArray())));
            List<Object> list = new ArrayList<>();
            list.addAll(Stream.of(this, id, x, y, width, height).collect(Collectors.toList()));
            list.addAll(Arrays.asList(args));
            T widget = (T) ctor.newInstance(list.toArray());
            this.add(widget);
            return widget;
        }
        catch (Throwable e)
        {
            throw new RTMMetroException("IWidgetHolder add widget failed!", e);
        }
    }

    default void onUpdate()
    {
        // perform action
        this.fromActionQueueDoAction();
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

    default int shiftMouseX() {return 0;}

    default int shiftMouseY() {return 0;}

    boolean isMouseInside();

    default void onWidgetAction(GuiWidget w)
    {
        IActionListener<? extends GuiWidget> listener = w.getListener();
        if (listener != null)
        {
            listener.onAction(w);
        }
    }

    default void addWidgetToActionQueue(GuiWidget w)
    {
        this.getActionQueue().offer(w);
    }

    default void fromActionQueueDoAction()
    {
        GuiWidget w = this.getActionQueue().poll();
        if (w == null) return;
        this.onWidgetAction(w);
        while (this.getActionQueue().poll() != null) ;
    }

    Queue<GuiWidget> getActionQueue();
}
