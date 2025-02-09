package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.cacpixel.rtmmetro.client.gui.GuiScreenWindowed;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class GuiScroll extends GuiWidgetBundle
{
    public int startX;
    public int startY;
    public int endX;
    public int endY;
    public boolean scrollUpDown = true;
    public boolean scrollLeftRight = false;
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
        GlStateManager.pushMatrix();
        if (scrollUpDown)
        {
            GlStateManager.translate(0, -upDownValue, 0);
        }
        if (scrollLeftRight)
        {
            GlStateManager.translate(leftRightValue, 0, 0);
        }
    }

    public void drawScrollAfter(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.popMatrix();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.drawScrollBefore(mouseX, mouseY, partialTicks);
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
            if (scrollUpDown && !GuiScreenWindowed.isShiftKeyDown())
            {
                this.upDownValue = MathHelper.clamp(this.upDownValue + scroll / CacGuiUtils.DEFAULT_SCROLL_VALUE * 10, 0, upDownMax);
            }
            if (scrollLeftRight && GuiScreenWindowed.isShiftKeyDown())
            {
                this.leftRightValue = MathHelper.clamp(this.leftRightValue + scroll / CacGuiUtils.DEFAULT_SCROLL_VALUE * 10, 0, leftRightMax);
            }
        }
    }

    @Override
    public GuiWidgetBundle add(IGuiWidget... widgets)
    {
        if (autoExpandMaxValue)
        {
            // todo add upDownMax and leftRightMax automatically
//            this.upDownMax = pScr.height / 2;
        }
        return super.add(widgets);
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
