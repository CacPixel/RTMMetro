package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiParam;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.cacpixel.rtmmetro.util.RTMMetroException;
import net.cacpixel.rtmmetro.util.RTMMetroUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
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

    default <T extends GuiWidget> T addWidget(Class<T> clazz, IntSupplier x, IntSupplier y,
                                              IntSupplier width, IntSupplier height, Object... args)
    {
        return this.addWidget(clazz, (Object) x, y, width, height, args);
    }

    @SuppressWarnings("unchecked")
    default <T extends GuiWidget> T addWidget(Class<T> clazz, Object x, Object y,
                                              Object width, Object height, Object... args)
    {
        try
        {
            List<Object> params = new ArrayList<>();
            params.add(this);
            params.add(this.getScreen().getNextWidgetId());
            params.add(x instanceof IntSupplier ? x : fromInt((int) x));
            params.add(y instanceof IntSupplier ? y : fromInt((int) y));
            params.add(width instanceof IntSupplier ? width : fromInt((int) width));
            params.add(height instanceof IntSupplier ? height : fromInt((int) height));
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
            T widget = (T) ctor.newInstance(params.toArray());
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

    default int shiftMouseX() {return this.getScreen().shiftMouseX();}

    default int shiftMouseY() {return this.getScreen().shiftMouseY();}

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
        do
        {
            this.onWidgetAction(w);
        }
        while ((w = this.getActionQueue().poll()) != null);
    }

    Queue<GuiWidget> getActionQueue();

    int getScissorX();

    int getScissorY();

    int getScissorWidth();

    int getScissorHeight();

    default void applyScissor(int xIn, int yIn, int wIn, int hIn)
    {

    }

    int getX();

    int getY();

    int getWidth();

    int getHeight();

    default void applyScissorFullScreen()
    {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glScissor(0, 0, mc.displayWidth, mc.displayHeight);
    }

    default GuiParam fromWidth()
    {
        return this::getWidth;
    }

    default GuiParam fromHeight()
    {
        return this::getHeight;
    }

    default GuiParam fromHalfWidth()
    {
        return fromWidth().thenDivideBy(2);
    }

    default GuiParam fromHalfHeight()
    {
        return fromHeight().thenDivideBy(2);
    }

    default GuiParam fromInt(int x)
    {
        return GuiParam.from(x);
    }
}
