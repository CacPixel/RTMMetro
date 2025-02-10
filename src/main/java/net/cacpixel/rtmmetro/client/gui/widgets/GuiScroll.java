package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class GuiScroll extends GuiWidgetBundle
{
    public int startX;
    public int startY;
    public int endX;
    public int endY;
    public boolean scrollUpDown = true;
    public boolean scrollLeftRight = true;
    private int upDownMax = 0;
    private int leftRightMax = 0;
    private int upDownValue = 0;
    private int leftRightValue = 0;
    public boolean autoExpandMaxValue = true;

    public GuiScroll(GuiScreenAdvanced pScr, int startX, int startY, int endX, int endY, IGuiWidget... widgets)
    {
        super(pScr, widgets);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public void drawScrollBefore(int mouseX, int mouseY, float partialTicks)
    {
        // up
        CacGuiUtils.drawRect(startX - 1, startY - 1, endX + 1, startY + 1, 0x505050 | this.pScr.getAlphaInt(0xFF));
        // down
        CacGuiUtils.drawRect(startX - 1, endY - 1, endX + 1, endY + 1, 0x505050 | this.pScr.getAlphaInt(0xFF));
        // left
        CacGuiUtils.drawRect(startX - 1, startY - 1, startX + 1, endY + 1, 0x505050 | this.pScr.getAlphaInt(0xFF));
        // right
        CacGuiUtils.drawRect(endX - 1, startY - 1, endX + 1, endY + 1, 0x505050 | this.pScr.getAlphaInt(0xFF));
        this.pScr.drawDefaultBackground(startX, startY, endX, endY);
        GlStateManager.pushMatrix();
        ScaledResolution res = new ScaledResolution(pScr.mc);
        double scaleW = pScr.mc.displayWidth / res.getScaledWidth_double();
        double scaleH = pScr.mc.displayHeight / res.getScaledHeight_double();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (startX * scaleW), (int) (pScr.mc.displayHeight - (endY * scaleH)), // 左下角开始
                (int) ((endX - startX) * scaleW), (int) ((endY - startY) * scaleH));
        if (scrollUpDown)
        {
            GlStateManager.translate(0, -upDownValue + startY, 0);
        }
        if (scrollLeftRight)
        {
            GlStateManager.translate(-leftRightValue + startX, 0, 0);
        }
    }

    public void drawScrollAfter(int mouseX, int mouseY, float partialTicks)
    {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
    }

    public void drawCustom(int mouseX, int mouseY, float partialTicks)
    {
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.drawScrollBefore(mouseX, mouseY, partialTicks);
        this.drawCustom(mouseX, mouseY, partialTicks);
        super.draw(mouseX, mouseY, partialTicks);
        this.drawScrollAfter(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isMouseInside()
    {
        // TODO: button draw那边的判断鼠标在内的逻辑也得改，isMousePressed也得改
        // TODO 这个 isMouseInside 也得传入参数 在isMouseInside里面偏移控件的位置（更好）
        // its hard
        return CacGuiUtils.isMouseInside(startX, startY, endX - startX, endY - startY);
    }

    @Override
    public void onScroll(int mouseX, int mouseY, int scroll)
    {
        super.onScroll(mouseX, mouseY, scroll);
        if (!this.isMouseInside()) return;
        List<GuiTextFieldAdvanced> textFields = new ArrayList<>();
        this.widgets.stream().filter(w -> w instanceof GuiTextFieldAdvanced).forEach(w -> textFields.add((GuiTextFieldAdvanced) w));
        if (textFields.stream().noneMatch(f -> f.isMouseInside() && f.isFocused())) // focused并且鼠标在内，不允许滚动GuiScroll
        {
            if (scrollUpDown && !GuiScreen.isShiftKeyDown())
            {
                this.upDownValue = MathHelper.clamp(this.upDownValue + scroll / CacGuiUtils.DEFAULT_SCROLL_VALUE * 10, 0, upDownMax);
            }
            if (scrollLeftRight && GuiScreen.isShiftKeyDown())
            {
                this.leftRightValue = MathHelper.clamp(this.leftRightValue + scroll / CacGuiUtils.DEFAULT_SCROLL_VALUE * 10, 0,
                        leftRightMax);
            }
        }
    }

    @Override
    public GuiScroll add(IGuiWidget... widgets)
    {
        if (autoExpandMaxValue)
        {
            int x = Arrays.stream(widgets).filter(Objects::nonNull).max(Comparator.comparingInt(IGuiWidget::getX))
                    .map(IGuiWidget::getX).orElse(0);
            int y = Arrays.stream(widgets).filter(Objects::nonNull).max(Comparator.comparingInt(IGuiWidget::getY))
                    .map(IGuiWidget::getY).orElse(0);
            int width = Arrays.stream(widgets).filter(Objects::nonNull).max(Comparator.comparingInt(IGuiWidget::getWidth))
                    .map(IGuiWidget::getWidth).orElse(0);
            int height = Arrays.stream(widgets).filter(Objects::nonNull).max(Comparator.comparingInt(IGuiWidget::getHeight))
                    .map(IGuiWidget::getHeight).orElse(0);
            this.upDownMax = Math.max(this.upDownMax, y + height + 10 - (endY - startY));
            this.leftRightMax = Math.max(this.leftRightMax, x + width + 10 - (endX - startX));
        }
        return (GuiScroll) super.add(widgets);
    }

    public GuiScroll setUpDownMax(int upDownMax)
    {
        this.upDownMax = upDownMax;
        return this;
    }

    public GuiScroll setLeftRightMax(int leftRightMax)
    {
        this.leftRightMax = leftRightMax;
        return this;
    }

    public GuiScroll setUpDownValue(int upDownValue)
    {
        this.upDownValue = upDownValue;
        return this;
    }

    public GuiScroll setLeftRightValue(int leftRightValue)
    {
        this.leftRightValue = leftRightValue;
        return this;
    }

    public int getUpDownValue()
    {
        return upDownValue;
    }

    public int getLeftRightValue()
    {
        return leftRightValue;
    }
}
