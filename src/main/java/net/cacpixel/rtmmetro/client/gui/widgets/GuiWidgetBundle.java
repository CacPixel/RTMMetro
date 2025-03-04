package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.function.IntSupplier;

public class GuiWidgetBundle extends GuiWidget implements IWidgetHolder
{
    public List<GuiWidget> widgets = new ArrayList<>();
    public PriorityQueue<GuiWidget> actionQueue = new PriorityQueue<>(Comparator.comparing(GuiWidget::getzLevel).reversed());

    public GuiWidgetBundle(IWidgetHolder holder, int id, IntSupplier x, IntSupplier y, IntSupplier width,
                           IntSupplier height)
    {
        super(holder, id, x, y, width, height);
    }

    public GuiWidgetBundle(GuiScreenAdvanced pScr, int id)
    {
        this(pScr, id, ZERO, ZERO, ZERO, ZERO);
    }

    @Override
    public GuiWidgetBundle add(GuiWidget... widgets)
    {
        GuiWidgetBundle ret = (GuiWidgetBundle) IWidgetHolder.super.add(widgets);
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
    public int getScissorX()
    {
        return 0;
    }

    @Override
    public int getScissorY()
    {
        return 0;
    }

    @Override
    public int getScissorWidth()
    {
        return 0;
    }

    @Override
    public int getScissorHeight()
    {
        return 0;
    }

    @Override
    public void applyScissor(int xIn, int yIn, int wIn, int hIn)
    {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int f = sr.getScaleFactor();
        int screenTX = (int) this.getScreen().translationX;
        int screenTY = (int) this.getScreen().translationY;
        int screenScaleX = (int) this.getScreen().scaleX;
        int screenScaleY = (int) this.getScreen().scaleY;
        int x = f * (screenTX + xIn * screenScaleX);
        int y = f * (sr.getScaledHeight() - screenTY - (yIn + hIn) * screenScaleY);
        int w = f * (wIn * screenScaleX);
        int h = f * (hIn * screenScaleY);
        GL11.glScissor(x, y, w, h);
    }
}
