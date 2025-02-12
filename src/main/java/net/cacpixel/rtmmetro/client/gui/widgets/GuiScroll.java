package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.cacpixel.rtmmetro.util.ModLog;
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
    private int upDownValueCurrent = 0;
    private int leftRightValueCurrent = 0;
    public boolean autoExpandMaxValue = true;
    private float animationTime = 0;
    private float duration;
    private boolean isInAnimation = false;
    private int deltaUpDown = 0;
    private int deltaLeftRight = 0;

    public GuiScroll(GuiScreenAdvanced pScr, int startX, int startY, int endX, int endY, IGuiWidget... widgets)
    {
        super(pScr, widgets);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.duration = ModConfig.guiAnimationDuration * 3;
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
        this.updateAnimation(partialTicks);
    }

    protected void updateAnimation(float partialTicks)
    {
        float dt = partialTicks / 20.0F;
        this.animationTime += dt;
        ScaledResolution res = new ScaledResolution(pScr.mc);
        double scaleW = pScr.mc.displayWidth / res.getScaledWidth_double();
        double scaleH = pScr.mc.displayHeight / res.getScaledHeight_double();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);// 左下角开始
        GL11.glScissor((int) ((pScr.translationX + (startX * pScr.scaleX)) * scaleW),
                (int) (((pScr.height - pScr.translationY) - (endY) * pScr.scaleY) * scaleH),
                // （原始平移量（缩放后的坐标系） + 原始的scroll位置 * scr缩放量） * scaleW/H
                (int) ((endX - startX) * pScr.scaleX * scaleW),
                (int) ((endY - startY) * pScr.scaleY * scaleH));
        if (scrollUpDown)
        {
            float d = this.getAnimationProgress() * deltaUpDown;
            float translation = startY - upDownValueCurrent - d;
            deltaUpDown -= Math.round(d);
            upDownValueCurrent += Math.round(d);
            GlStateManager.translate(0, translation, 0);
        }
        if (scrollLeftRight)
        {
            float d = this.getAnimationProgress() * deltaLeftRight;
            float translation = startX - leftRightValueCurrent - d;
            deltaLeftRight -= Math.round(d);
            leftRightValueCurrent += Math.round(d);
            GlStateManager.translate(translation, 0, 0);
        }
    }

    protected float getAnimationProgress()
    {
        if (!isInAnimation)
        {
            this.animationTime = 0;
            return 1.0f;
        }
        BezierCurveAdvanced curve = CacGuiUtils.guiBezierScroll;
        double point = curve.fromXGetY((int) curve.getLength(), (this.animationTime / this.duration) * CacGuiUtils.X_MAX);
        if (this.animationTime > this.duration)
            this.isInAnimation = false;
        return MathHelper.clamp((float) point, 0f, 1.0f);
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
        this.widgets.stream().filter(w -> w instanceof GuiTextFieldAdvanced)
                .forEach(w -> textFields.add((GuiTextFieldAdvanced) w));
        if (textFields.stream().noneMatch(f -> f.isMouseInside() && f.isFocused())) // focused并且鼠标在内，不允许滚动GuiScroll
        {
            if (scrollUpDown && !GuiScreen.isShiftKeyDown())
            {
                deltaUpDown += MathHelper.clamp(scroll / CacGuiUtils.DEFAULT_SCROLL_VALUE * 30,
                        -upDownValueCurrent - deltaUpDown, upDownMax - upDownValueCurrent - deltaUpDown);
                animationTime = 0;
                isInAnimation = true;
            }
            if (scrollLeftRight && GuiScreen.isShiftKeyDown())
            {
                deltaLeftRight += MathHelper.clamp(scroll / CacGuiUtils.DEFAULT_SCROLL_VALUE * 30,
                        -leftRightValueCurrent - deltaLeftRight, leftRightMax - leftRightValueCurrent - deltaLeftRight);
                animationTime = 0;
                isInAnimation = true;
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
            int width = Arrays.stream(widgets).filter(Objects::nonNull)
                    .max(Comparator.comparingInt(IGuiWidget::getWidth))
                    .map(IGuiWidget::getWidth).orElse(0);
            int height = Arrays.stream(widgets).filter(Objects::nonNull)
                    .max(Comparator.comparingInt(IGuiWidget::getHeight))
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

    public GuiScroll setUpDownValueCurrent(int upDownValueCurrent)
    {
        this.upDownValueCurrent = upDownValueCurrent;
        return this;
    }

    public GuiScroll setLeftRightValueCurrent(int leftRightValueCurrent)
    {
        this.leftRightValueCurrent = leftRightValueCurrent;
        return this;
    }

    public int getUpDownValueCurrent()
    {
        return upDownValueCurrent;
    }

    public int getLeftRightValueCurrent()
    {
        return leftRightValueCurrent;
    }
}
