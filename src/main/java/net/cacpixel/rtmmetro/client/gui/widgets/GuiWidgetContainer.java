package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiLayoutBase;
import net.cacpixel.rtmmetro.client.gui.GuiLayoutNone;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.minecraft.client.renderer.GlStateManager;

import java.util.*;
import java.util.function.IntSupplier;

public class GuiWidgetContainer extends GuiWidget implements IWidgetHolder
{
    public GuiLayoutBase layout = new GuiLayoutNone(this);
    public List<GuiWidget> widgets = new ArrayList<>();
    public PriorityQueue<GuiWidget> actionQueue = new PriorityQueue<>(Comparator.comparing(GuiWidget::getzLevel).reversed());

    public GuiWidgetContainer(IWidgetHolder holder, int id, IntSupplier x, IntSupplier y, IntSupplier width,
                              IntSupplier height)
    {
        super(holder, id, x, y, width, height);
    }

    public GuiWidgetContainer(GuiScreenAdvanced pScr, int id)
    {
        this(pScr, id, ZERO, ZERO, ZERO, ZERO);
    }

    @Override
    public GuiWidgetContainer add(GuiWidget... widgets)
    {
        GuiWidgetContainer ret = (GuiWidgetContainer) IWidgetHolder.super.add(widgets);
        ret.widgets.removeIf(w -> w == this); // avoid add itself, it will cause infinity loop
        return ret;
    }

    @Override
    public void onUpdate()
    {
        IWidgetHolder.super.onUpdate();
    }

    @Override
    public int shiftMouseX()
    {
        return this.isPositionIndependent() ? IWidgetHolder.super.shiftMouseX() :
                this.getX() + IWidgetHolder.super.shiftMouseX();
    }

    @Override
    public int shiftMouseY()
    {
        return this.isPositionIndependent() ? IWidgetHolder.super.shiftMouseY() :
                this.getY() + IWidgetHolder.super.shiftMouseY();
    }

    @Override
    public boolean isMouseInside()
    {
        return this.isPositionIndependent() ? this.widgets.stream().anyMatch(GuiWidget::isMouseInside) :
                super.isMouseInside();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (!this.isVisible()) {return;}
        this.pScr.glPushMatrix();
        if (!this.isPositionIndependent())
            GlStateManager.translate(x, y, 0);
        this.widgets.forEach(x -> x.draw(mouseX, mouseY, partialTicks));
        this.drawCustom(mouseX, mouseY, partialTicks);
        this.pScr.glPopMatrix();
    }

    public void drawCustom(int mouseX, int mouseY, float partialTicks)
    {
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
}
