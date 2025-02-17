package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.function.IntSupplier;

public class GuiScroll extends GuiWidgetBundle
{
    public boolean scrollUpDown = true;
    public boolean scrollLeftRight = true;
    protected int yMax = 0;
    protected int xMax = 0;
    protected int yNow = 0;
    protected int xNow = 0;
    public boolean autoExpandMaxValue = true;
    private float animationTime = 0;
    private float duration;
    private boolean isInAnimation = false;
    private int dy = 0;
    private int dx = 0;
    private int prevScrollDir = 0;

    public GuiScroll(IWidgetHolder holder, int id, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height,
                     GuiWidget... widgets)
    {
        super(holder, id, x, y, width, height, widgets);
        this.duration = ModConfig.guiAnimationDuration;
    }

    public void drawBefore(int mouseX, int mouseY, float partialTicks)
    {
        // up
        CacGuiUtils.drawRect(x - 1, y - 1, getEndX() + 1, y + 1, 0x505050 | this.pScr.getAlphaInt(0xFF));
        // down
        CacGuiUtils.drawRect(x - 1, getEndY() - 1, getEndX() + 1, getEndY() + 1,
                0x505050 | this.pScr.getAlphaInt(0xFF));
        // left
        CacGuiUtils.drawRect(x - 1, y - 1, x + 1, getEndY() + 1, 0x505050 | this.pScr.getAlphaInt(0xFF));
        // right
        CacGuiUtils.drawRect(getEndX() - 1, y - 1, getEndX() + 1, getEndY() + 1,
                0x505050 | this.pScr.getAlphaInt(0xFF));
        this.pScr.drawDefaultBackground(x, y, getEndX(), getEndY());
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
        GL11.glScissor((int) ((pScr.translationX + (x * pScr.scaleX)) * scaleW),
                (int) (((pScr.height - pScr.translationY) - (getEndY()) * pScr.scaleY) * scaleH),
                // （原始平移量（缩放后的坐标系） + 原始的scroll位置 * scr缩放量） * scaleW/H
                (int) ((getEndX() - x) * pScr.scaleX * scaleW),
                (int) ((getEndY() - y) * pScr.scaleY * scaleH));
        if (scrollUpDown)
        {
            float d = this.getAnimationProgress() * dy;
            float translation = -yNow - d;
            GlStateManager.translate(0, translation, 0);
        }
        if (scrollLeftRight)
        {
            float d = this.getAnimationProgress() * dx;
            float translation = -xNow - d;
            GlStateManager.translate(translation, 0, 0);
        }
    }

    protected float getAnimationProgress()
    {
        if (!isInAnimation)
        {
            yNow += dy;
            xNow += dx;
            dy = dx = 0;
            this.animationTime = 0;
            prevScrollDir = 0;
            return 1.0f;
        }
        BezierCurveAdvanced curve = CacGuiUtils.guiBezierScroll;
        double point = curve.fromXGetY((int) curve.getLength(),
                (this.animationTime / this.duration) * CacGuiUtils.X_MAX);
        if (this.animationTime > this.duration)
            this.isInAnimation = false;
        return MathHelper.clamp((float) point, 0f, 1.0f);
    }

    public void drawAfter(int mouseX, int mouseY, float partialTicks)
    {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (!this.isVisible()) {return;}
        this.drawBefore(mouseX, mouseY, partialTicks);
        super.draw(mouseX, mouseY, partialTicks);
        this.drawAfter(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onScroll(int mouseX, int mouseY, int scroll)
    {
        super.onScroll(mouseX, mouseY, scroll);
        if (!this.isMouseInside()) return;
        this.scrollPage(scroll, GuiScreen.isShiftKeyDown());
    }

    public void scrollPage(int scroll, boolean leftRightDirection)
    {
        List<GuiTextFieldAdvanced> textFields = new ArrayList<>();
        this.widgets.stream().filter(w -> w instanceof GuiTextFieldAdvanced)
                .forEach(w -> textFields.add((GuiTextFieldAdvanced) w));
        if (textFields.stream().noneMatch(f -> f.isMouseInside() && f.isFocused())) // focused并且鼠标在内，不允许滚动GuiScroll
        {
            if (scrollUpDown && !leftRightDirection)
            {
                if (prevScrollDir == 2)
                {
                    float d = this.getAnimationProgress() * (dx);
                    xNow += Math.round(d);
                    dx = 0;
                }
                int targetScroll = -scroll / CacGuiUtils.DEFAULT_SCROLL_VALUE * 30;
                int clamped = MathHelper.clamp(targetScroll, -yNow - dy,
                        yMax - yNow - dy);
                float d = this.getAnimationProgress() * (dy);
                dy += clamped;
                yNow += Math.round(d);
                dy -= Math.round(d);
                animationTime = 0;
                isInAnimation = true;
                prevScrollDir = 1;
            }
            else if (scrollLeftRight && leftRightDirection)
            {
                if (prevScrollDir == 1)
                {
                    float d = this.getAnimationProgress() * (dy);
                    yNow += Math.round(d);
                    dy = 0;
                }
                int targetScroll = -scroll / CacGuiUtils.DEFAULT_SCROLL_VALUE * 30;
                int clamped = MathHelper.clamp(targetScroll, -xNow - dx,
                        xMax - xNow - dx);
                float d = this.getAnimationProgress() * (dx);
                dx += clamped;
                xNow += Math.round(d);
                dx -= Math.round(d);
                animationTime = 0;
                isInAnimation = true;
                prevScrollDir = 2;
            }
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        super.onKeyTyped(typedChar, keyCode);
        switch (keyCode)
        {
        case Keyboard.KEY_UP:
            this.scrollPage(CacGuiUtils.DEFAULT_SCROLL_VALUE, false);
            break;
        case Keyboard.KEY_DOWN:
            this.scrollPage(-CacGuiUtils.DEFAULT_SCROLL_VALUE, false);
            break;
        case Keyboard.KEY_LEFT:
            this.scrollPage(CacGuiUtils.DEFAULT_SCROLL_VALUE, true);
            break;
        case Keyboard.KEY_RIGHT:
            this.scrollPage(-CacGuiUtils.DEFAULT_SCROLL_VALUE, true);
            break;
        default:
            break;
        }
    }

    @Override
    public GuiScroll add(GuiWidget... widgets)
    {
        super.add(widgets);
        this.expandMaxValue(this.widgets.toArray(new GuiWidget[0]));
        return this;
    }

    @Override
    public GuiScroll updatePosAndSize()
    {
        super.updatePosAndSize();
        this.xMax = 0;
        this.yMax = 0;
        this.expandMaxValue(this.widgets.toArray(new GuiWidget[0]));
        return this;
    }

    public void expandMaxValue(GuiWidget... widgets)
    {
        this.expandMaxValue(0, 0, widgets);
    }

    public void expandMaxValue(int xIn, int yIn, GuiWidget... widgets)
    {
        if (autoExpandMaxValue)
        {
            GuiWidget widget = Arrays.stream(widgets).filter(Objects::nonNull)
                    .max(Comparator.<GuiWidget>comparingInt(w -> w.getX() + w.getWidth())
                            .thenComparingInt(w -> w.getY() + w.getHeight())).orElse(null);
            if (widget != null)
            {
                this.yMax = Math.max(this.yMax, widget.y + widget.height + 10 - this.height);
                this.xMax = Math.max(this.xMax, widget.x + widget.width + 10 - this.width);
                this.yMax += yIn;
                this.xMax += xIn;
            }
        }
        else
        {
            this.yMax = yIn;
            this.xMax = xIn;
        }
        yNow = Math.min(yNow, yMax);
        xNow = Math.min(xNow, xMax);
    }

    public GuiScroll setYMax(int yMax)
    {
        this.yMax = yMax;
        return this;
    }

    public GuiScroll setXMax(int xMax)
    {
        this.xMax = xMax;
        return this;
    }

    public void addYMax(int yMax)
    {
        this.yMax += yMax;
    }

    public void addXMax(int xMax)
    {
        this.xMax += xMax;
    }

    public int getEndX()
    {
        return x + width;
    }

    public int getEndY()
    {
        return y + height;
    }

    @Override
    public int shiftMouseX()
    {
        return this.isPositionIndependent() ? super.shiftMouseX() :
                (int) (-this.xNow - this.dx * this.getAnimationProgress()) + super.shiftMouseX();
    }

    @Override
    public int shiftMouseY()
    {
        return this.isPositionIndependent() ? super.shiftMouseY() :
                (int) (-this.yNow - this.dy * this.getAnimationProgress()) + super.shiftMouseY();
    }
}
