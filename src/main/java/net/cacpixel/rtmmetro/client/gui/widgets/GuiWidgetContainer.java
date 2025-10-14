package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;

import java.util.*;
import java.util.function.IntSupplier;

public class GuiWidgetContainer extends GuiWidget implements IWidgetHolder
{
    public GuiLayoutBase layout = new GuiLayoutNone(this);
    public List<GuiWidget> widgets = new ArrayList<>();
    public PriorityQueue<GuiWidget> actionQueue = new PriorityQueue<>(Comparator.comparing(GuiWidget::getLayer).reversed());
    protected int scissorDisableLayers;     // 上次禁用scissor时层数记录用

    public GuiWidgetContainer(IWidgetHolder holder, IntSupplier x, IntSupplier y, IntSupplier width,
                              IntSupplier height)
    {
        super(holder, x, y, width, height);
        getEventClick().setEventPass(true);
        getEventLastClick().setEventPass(true);
        getEventDrag().setEventPass(true);
        getEventRelease().setEventPass(true);
        getEventScroll().setEventPass(true);
    }

    public GuiWidgetContainer(GuiScreenAdvanced pScr)
    {
        this(pScr, ZERO, ZERO, ZERO, ZERO);
    }

    @Override
    public <T extends GuiWidget> T add(T widget)
    {
        T ret = IWidgetHolder.super.add(widget);
        this.widgets.removeIf(w -> w == this); // avoid add itself, it will cause infinity loop
        return ret;
    }

    @Override
    public void onUpdate()
    {
        IWidgetHolder.super.onUpdate();
    }

    public void doScissorBefore()
    {
        ScreenScissorManager screenScissorManager = this.getScreen().getScreenScissorManager();
        ScissorParam param = new ScissorParam(getXOfScreen(), getYOfScreen(), getHolderWidth(), getHolderHeight());
        screenScissorManager.push(param);
        screenScissorManager.apply();
    }

    public void doScissorAfter()
    {
        ScreenScissorManager screenScissorManager = this.getScreen().getScreenScissorManager();
        screenScissorManager.pop();
    }

    @Override
    public void drawBefore(int mouseX, int mouseY, float partialTicks)
    {
        doScissorBefore();
        this.getScreen().glPushMatrix();
        super.drawBefore(mouseX, mouseY, partialTicks);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.translate(x, y, 0);
        drawWidgetList(mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawAfter(int mouseX, int mouseY, float partialTicks)
    {
        super.drawAfter(mouseX, mouseY, partialTicks);
        this.getScreen().glPopMatrix();
        doScissorAfter();
    }

    public List<GuiWidget> getWidgets()
    {
        return this.widgets;
    }

    @Override
    public Queue<GuiWidget> getActionQueue()
    {
        return actionQueue;
    }

    @Override
    public GuiLayoutBase getLayout()
    {
        return layout;
    }

    @Override
    public void setLayout(GuiLayoutBase layout)
    {
        this.layout = layout;
    }

    @Override
    public void onScreenResize()
    {
        super.onScreenResize();
        this.widgets.forEach(GuiWidget::onScreenResize);
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button)
    {
        this.getWidgets().stream()
                .sorted(Comparator.comparingInt(GuiWidget::getLayer).reversed())
                .forEach(w -> w.onClick(mouseX, mouseY, button));
        super.onClick(mouseX, mouseY, button);
    }

    @Override
    public void onClickAndDrag(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick)
    {
        this.getWidgets().stream()
                .sorted(Comparator.comparingInt(GuiWidget::getLayer).reversed())
                .forEach(w -> w.onClickAndDrag(mouseX, mouseY, mouseButton, timeSinceLastClick));
        super.onClickAndDrag(mouseX, mouseY, mouseButton, timeSinceLastClick);
    }

    @Override
    public void onRelease(int mouseX, int mouseY, int state)
    {
        this.getWidgets().stream()
                .sorted(Comparator.comparingInt(GuiWidget::getLayer).reversed())
                .forEach(w -> w.onRelease(mouseX, mouseY, state));
        super.onRelease(mouseX, mouseY, state);
    }

    @Override
    public void onScroll(int mouseX, int mouseY, int scroll)
    {
        this.getWidgets().stream()
                .sorted(Comparator.comparingInt(GuiWidget::getLayer).reversed())
                .forEach(w -> w.onScroll(mouseX, mouseY, scroll));
        super.onScroll(mouseX, mouseY, scroll);
    }
}
