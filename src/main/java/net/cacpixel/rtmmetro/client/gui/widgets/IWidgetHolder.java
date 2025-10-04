package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.*;
import net.cacpixel.rtmmetro.util.ModLog;
import net.cacpixel.rtmmetro.util.RTMMetroException;
import net.cacpixel.rtmmetro.util.RTMMetroUtils;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

public interface IWidgetHolder
{
    List<GuiWidget> getWidgets();

    GuiScreenAdvanced getScreen();

    IWidgetHolder getHolder();

    GuiLayoutBase getLayout();

    void setLayout(GuiLayoutBase layout);

    default <T extends GuiWidget> T add(T widget)
    {
//        this.getWidgets().removeIf(w -> Arrays.stream(widgets).anyMatch(widgetIn -> widgetIn.getId() == w.getId()));
        this.getWidgets().add(widget);
        return widget;
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

    default <T> List<T> getWidgetFromClass(Class<T> clazz)
    {
        return this.getWidgetFromClass(clazz, false);
    }

    default <T> List<T> getAllWidgetFromClass(Class<T> clazz)
    {
        return this.getWidgetFromClass(clazz, true);
    }

    @SuppressWarnings("unchecked")
    default <T> List<T> getWidgetFromClass(Class<T> clazz, boolean reentrant)
    {
        List<T> ret = new ArrayList<>();
        if (reentrant)
            this.forEachHolder(holder -> ret.addAll(holder.getWidgetFromClass(clazz, true)));
        this.getWidgets().stream().filter(clazz::isInstance).forEach(b -> ret.add((T) b));
        return ret;
    }

    default int shiftMouseX() {return this.getScreen().shiftMouseX();}  //todo delete

    default int shiftMouseY() {return this.getScreen().shiftMouseY();}//todo delete

    boolean isMouseInside();

    boolean isLastClickInside();

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
        } while ((w = this.getActionQueue().poll()) != null);
    }

    Queue<GuiWidget> getActionQueue();

    int getX();

    int getY();

    int getWidth();

    int getHeight();

    int getXOfScreen();

    int getYOfScreen();

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

    default void onMakeLayoutStart() {}

    default void onMakeLayoutFinish() {}

    default int getHolderWidth() {return getWidth();}       // 获取实际holder可用面积对应的width

    default int getHolderHeight() {return getHeight();}     // 获取实际holder可用面积对应的height

    default void makeLayout()
    {
        this.getWidgetFromClass(IWidgetHolder.class).forEach(IWidgetHolder::makeLayout);
        this.getLayout().makeLayout();
    }

    default void doAsGuiWidget(Consumer<GuiWidget> consumer)
    {
        if (this instanceof GuiWidget)
        {
            consumer.accept((GuiWidget) this);
        }
    }

    default void drawWidgetList(int mouseX, int mouseY, float partialTicks)
    {
        getWidgets().stream()
                .sorted(Comparator.comparingInt(GuiWidget::getLayer))
                .forEach(x -> {
                    if (x.isVisible())
                    {
                        x.drawBefore(mouseX, mouseY, partialTicks);
                        x.draw(mouseX, mouseY, partialTicks);
                        x.drawCustom(mouseX, mouseY, partialTicks);
                        x.drawAfter(mouseX, mouseY, partialTicks);
                    }
                    x.drawDebugLayer(mouseX, mouseY, partialTicks);
                });
    }

    default void mouseInteractJudge()
    {
        List<GuiWidget> sortedWidgets = this.getWidgets().stream()
                .sorted(Comparator.comparingInt(GuiWidget::getLayer).reversed())
                .collect(Collectors.toList());
        getScreen().getMouseScissorManager().push(new ScissorParam(getXOfScreen(), getYOfScreen(), getWidth(), getHeight()));
        for (GuiWidget widget : sortedWidgets)
        {
            boolean isMouseInside = widget.isMouseInside();
            widget.mouseInteractJudge();
            GuiMouseEvent[] widgetEvents = widget.getGuiMouseEvents();
            GuiMouseEvent[] screenEvents = getScreen().getGuiMouseEvents();
            for (int i = 0; i < widgetEvents.length; i++)
            {
                if (screenEvents[i].canInteract())  // 如果screen设置了可交互
                {
                    // 控件可不可交互取决于鼠标是否在内（layer高的先被轮到此处判断）
                    // 鼠标的范围已经经过Scissor裁切
                    widgetEvents[i].setInteract(isMouseInside);
                    if (isMouseInside && !widgetEvents[i].canEventPass())
                    {
                        // 鼠标在控件内，且不允许穿透，那么后续的控件都不再能响应
                        screenEvents[i].setInteract(false);
                    }
                }
                else
                {
                    // 如果不能交互，说明此前有不允许event pass的控件被允许交互了，接下来的控件全部都禁止交互
                    widgetEvents[i].setInteract(false);
                }
            }
        }
        getScreen().getMouseScissorManager().pop();
    }

    GuiMouseEvent getEventClick();

    GuiMouseEvent getEventLastClick();

    GuiMouseEvent getEventDrag();

    GuiMouseEvent getEventRelease();

    GuiMouseEvent getEventScroll();

    GuiMouseEvent[] getGuiMouseEvents();

    default void printWidgetTree(int depth)
    {
        List<GuiWidget> list = this.getWidgets().stream()
                .sorted(Comparator.comparingInt(GuiWidget::getLayer).reversed())
                .collect(Collectors.toList());
        for (GuiWidget widget : list)
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < depth; i++)
            {
                sb.append("    ");
            }
            sb.append("- ");
            ModLog.debug(sb.toString() + widget.toString());
            if (widget instanceof IWidgetHolder)
            {
                ((IWidgetHolder) widget).printWidgetTree(depth + 1);
            }
        }
    }
}
