package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

public class GuiScroll extends GuiWidgetBundle
{
    public int scrollButtonWidth;
    public int scrollDistance = 60;
    public boolean scrollUpDown = true;
    public boolean scrollLeftRight = true;
    protected int yMax = 0;
    protected int xMax = 0;
    protected int yNow = 0;
    protected int xNow = 0;
    protected int yNowPrev = 0;
    protected int xNowPrev = 0;
    public boolean autoExpandMaxValue = true;
    private float animationTime = 0;
    private float duration;
    private boolean isInAnimation = false;
    private int dy = 0;
    private int dx = 0;
    private int prevScrollDir = 0;
    private ScrollButton xButton;
    private ScrollButton yButton;

    public GuiScroll(IWidgetHolder holder, int id, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height)
    {
        super(holder, id, x, y, width, height);
        this.duration = ModConfig.guiAnimationDuration;
//        this.scrollButtonWidth = (int) (30.0F / new ScaledResolution(this.pScr.mc).getScaleFactor());
        this.scrollButtonWidth = 7;
        // 以下两个button不会进入widget list
        int i = 0;
        this.xButton = new ScrollButton(this, this.getScreen().getNextWidgetId(),
                () -> i,
                () -> this.height - scrollButtonWidth,
                () -> this.width - (this.yButton != null && this.yButton.isVisible() ? scrollButtonWidth : 0) - i,
                () -> scrollButtonWidth - i,
                true).setListener(b -> this.buttonCallback((ScrollButton) b));
        this.yButton = new ScrollButton(this, this.getScreen().getNextWidgetId(),
                () -> this.width - scrollButtonWidth,
                () -> i,
                () -> scrollButtonWidth - i,
                () -> this.height - (this.xButton != null && this.xButton.isVisible() ? scrollButtonWidth : 0) - i,
                false).setListener(b -> this.buttonCallback((ScrollButton) b));
    }

    public void drawBefore(int mouseX, int mouseY, float partialTicks)
    {
        // up
        CacGuiUtils.drawRect(x - 1, y - 1, getEndX() + 1, y, 0x303030 | this.pScr.getAlphaInt(0x80));
        // down
        CacGuiUtils.drawRect(x - 1, getEndY(), getEndX() + 1, getEndY() + 1,
                0x303030 | this.pScr.getAlphaInt(0x80));
        // left
        CacGuiUtils.drawRect(x - 1, y, x, getEndY(), 0x303030 | this.pScr.getAlphaInt(0x80));
        // right
        CacGuiUtils.drawRect(getEndX(), y, getEndX() + 1, getEndY(),
                0x303030 | this.pScr.getAlphaInt(0x80));
//        CacGuiUtils.drawRect(x, y, getEndX(), getEndY(), 0x101010 | this.pScr.getAlphaInt(0x60));
        this.pScr.glPushMatrix();
        if (!this.isPositionIndependent())
            GlStateManager.translate(x, y, 0);
        this.xButton.draw(mouseX, mouseY, partialTicks);
        this.yButton.draw(mouseX, mouseY, partialTicks);
        this.processButtonDrag(mouseX, mouseY, partialTicks);
        this.updateButton();
        this.pScr.glPopMatrix();
        this.pScr.glPushMatrix();
        this.updateAnimation(partialTicks);
    }

    protected void processButtonDrag(int mouseX, int mouseY, float partialTicks)
    {
        if (xButton.barClicked)
        {
            dx = 0;
            float d = (float) (xButton.lastClickedX - mouseX) / (xButton.width - xButton.length) * xMax;
            xNow = (int) MathHelper.clamp(xNowPrev - d, 0, xMax);
        }
        else
        {
            xNowPrev = xNow;
        }

        if (yButton.barClicked)
        {
            dy = 0;
            float d = (float) (yButton.lastClickedY - mouseY) / (yButton.height - yButton.length) * yMax;
            yNow = (int) MathHelper.clamp(yNowPrev - d, 0, yMax);
        }
        else
        {
            yNowPrev = yNow;
        }
    }

    public void buttonCallback(ScrollButton button)
    {
        if (button == xButton)
        {
            if (!button.isDraggingBar())
            {
                int scroll = (CacGuiUtils.getMouseX() < this.x + button.x + button.pos + 0.5 * button.length) ?
                        button.length : -button.length;
                scroll *= 2;
                this.dx = 0;
                this.xNow = MathHelper.clamp(xNow - scroll, 0, xMax);
            }
        }
        if (button == yButton)
        {
            if (!button.isDraggingBar())
            {
                int scroll = (CacGuiUtils.getMouseY() < this.y + button.y + button.pos + 0.5 * button.length) ?
                        button.length : -button.length;
                scroll *= 2;
                this.dy = 0;
                this.yNow = MathHelper.clamp(yNow - scroll, 0, yMax);
            }
        }
    }

    @Override
    public boolean isMouseInside()
    {
        int dx = holder.shiftMouseX();
        int dy = holder.shiftMouseY();
        return this.isPositionIndependent() ? this.widgets.stream().anyMatch(GuiWidget::isMouseInside) :
                CacGuiUtils.isMouseInside(x + dx, y + dy,
                        width - ((yButton.isVisible()) ? scrollButtonWidth : 0),
                        height - ((xButton.isVisible()) ? scrollButtonWidth : 0));
    }

    protected void updateAnimation(float partialTicks)
    {
        float dt = partialTicks / 20.0F;
        this.animationTime += dt;
        ScaledResolution res = new ScaledResolution(pScr.mc);
        double scaleW = pScr.mc.displayWidth / res.getScaledWidth_double();
        double scaleH = pScr.mc.displayHeight / res.getScaledHeight_double();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);// 左下角开始
        int xDiff = this.yButton.isVisible() ? scrollButtonWidth : 0;
        int yDiff = this.xButton.isVisible() ? scrollButtonWidth : 0;
        GL11.glScissor((int) ((pScr.translationX + (x * pScr.scaleX)) * scaleW),
                (int) (((pScr.height - pScr.translationY) - (getEndY() - yDiff) * pScr.scaleY) * scaleH),
                // （原始平移量（缩放后的坐标系） + 原始的scroll位置 * scr缩放量） * scaleW/H
                (int) ((getEndX() - x - xDiff) * pScr.scaleX * scaleW),
                (int) ((getEndY() - y - yDiff) * pScr.scaleY * scaleH));
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
        this.pScr.glPopMatrix();
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
    public void onUpdate()
    {
        this.xButton.onWidgetUpdate();
        this.yButton.onWidgetUpdate();
        super.onUpdate();
    }

    @Override
    public void onScroll(int mouseX, int mouseY, int scroll)
    {
        super.onScroll(mouseX, mouseY, scroll);
        if (!this.isMouseInside()) return;
        boolean leftRightDirection = GuiScreen.isShiftKeyDown() || !this.yButton.isVisible();
        this.scrollPage(scroll, leftRightDirection);
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY)
    {
        super.onLeftClick(mouseX, mouseY);
        Stream.of(xButton, yButton).forEach(w -> {
            w.onLeftClick(mouseX, mouseY);
        });
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int state)
    {
        super.onMouseReleased(mouseX, mouseY, state);
        Stream.of(xButton, yButton).forEach(w -> {
            w.onMouseReleased(mouseX, mouseY, state);
        });
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
                int targetScroll = (int) ((float) -scroll / CacGuiUtils.DEFAULT_SCROLL_VALUE * scrollDistance);
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
                int targetScroll = (int) ((float) -scroll / CacGuiUtils.DEFAULT_SCROLL_VALUE * scrollDistance);
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
        this.updatePosAndSize();
        return this;
    }

    @Override
    public GuiScroll updatePosAndSize()
    {
        super.updatePosAndSize();
        this.xMax = 0;
        this.yMax = 0;
        this.expandMaxValue(this.widgets.toArray(new GuiWidget[0]));
//        this.scrollButtonWidth = (int) (30.0F / new ScaledResolution(this.pScr.mc).getScaleFactor());
        this.updateButton();
        return this;
    }

    public void updateButton()
    {
        int xDiff = this.yButton.isVisible() ? scrollButtonWidth : 0;
        int yDiff = this.xButton.isVisible() ? scrollButtonWidth : 0;
        this.xButton.updatePosAndSize(width - xDiff, xMax, this.getCurrentX());
        this.yButton.updatePosAndSize(height - yDiff, yMax, this.getCurrentY());
    }

    public void expandMaxValue(GuiWidget... widgets)
    {
        this.expandMaxValue(scrollButtonWidth, scrollButtonWidth, widgets);
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
                this.yMax = Math.max(this.yMax, widget.y + widget.height + scrollButtonWidth - this.height);
                this.xMax = Math.max(this.xMax, widget.x + widget.width + scrollButtonWidth - this.width);
                if (this.yMax != 0) this.yMax += yIn;
                if (this.xMax != 0) this.xMax += xIn;
            }
        }
        else
        {
            this.yMax = yIn;
            this.xMax = xIn;
        }
        if (this.xMax == 0) yMax = Math.max(yMax - scrollButtonWidth, 0);
        if (this.yMax == 0) xMax = Math.max(xMax - scrollButtonWidth, 0);
        this.xButton.setVisible(this.xMax != 0);
        this.yButton.setVisible(this.yMax != 0);
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
                (int) (-this.getCurrentX()) + super.shiftMouseX();
    }

    @Override
    public int shiftMouseY()
    {
        return this.isPositionIndependent() ? super.shiftMouseY() :
                (int) (-this.getCurrentY()) + super.shiftMouseY();
    }

    public float getCurrentX()
    {
        return (this.xNow + this.dx * this.getAnimationProgress());
    }

    public float getCurrentY()
    {
        return (this.yNow + this.dy * this.getAnimationProgress());
    }

    public static class ScrollButton extends GuiButtonAdvanced
    {
        int length = 0;
        int pos = 0;
        boolean xScrolling; // false: yScrolling
        boolean barClicked = false;

        public ScrollButton(IWidgetHolder holder, int id, IntSupplier xSupplier, IntSupplier ySupplier,
                            IntSupplier widthSupplier, IntSupplier heightSupplier, boolean xScrolling)
        {
            super(holder, id, xSupplier, ySupplier, widthSupplier, heightSupplier);
            this.xScrolling = xScrolling;
            if (!(holder instanceof GuiScroll))
            {
                throw new ClassCastException("Could not cast " + holder.getClass() + " to " +
                        GuiScroll.class + " in ScrollButton constructor.");
            }
            this.updatePosAndSize();
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial)
        {
            if (this.isVisible())
            {
                this.hovered = this.isMouseInBar();
                if (this.pScr != mc.currentScreen || pScr.isInAnimation())
                {
                    this.hovered = false;
                }
                int k = this.getHoverState(this.hovered);
                // disabled texture
                CacGuiUtils.drawContinuousTexturedBox(RTMMETRO_BUTTON_TEXTURES, this.x, this.y, 0, 46, this.width,
                        this.height, 200,
                        20, 2, 3, 2, 2, this.zLevel, pScr);
                // enabled texture
                int x = xScrolling ? pos : this.x;
                int y = xScrolling ? this.y : pos;
                int w = xScrolling ? length : this.width;
                int h = xScrolling ? this.height : length;
                CacGuiUtils.drawContinuousTexturedBox(RTMMETRO_BUTTON_TEXTURES, x, y, 0, 46 + k * 20,
                        w, h, 200,
                        20, 2, 3, 2, 2, this.zLevel, pScr);
                int color = 0xE0E0E0;
                if (!super.isEnabled())
                {
                    color = 0xA0A0A0;
                }
                else if (this.hovered)
                {
//                color = 0xFFFFA0;
                }

                String buttonText = this.displayString;
                color |= pScr.getAlphaInt(0xFF);
                CacGuiUtils.drawCenteredString(mc.fontRenderer, buttonText, this.x + this.width / 2,
                        this.y + (this.height - 8) / 2, color);
            }
        }

        public boolean isDraggingBar()
        {
            return this.isMouseInBar() && Mouse.isButtonDown(0);
        }

        @Override
        public boolean isMouseInside()
        {
            int dx = ((GuiScroll) holder).x;
            int dy = ((GuiScroll) holder).y;
            return CacGuiUtils.isMouseInside(x + dx, y + dy, width, height);
        }

        public boolean isMouseInBar()
        {
            int x = xScrolling ? pos : this.x;
            int y = xScrolling ? this.y : pos;
            int width = xScrolling ? length : this.width;
            int height = xScrolling ? this.height : length;
            int dx = ((GuiScroll) holder).x;
            int dy = ((GuiScroll) holder).y;
            return CacGuiUtils.isMouseInside(x + dx, y + dy, width, height);
        }

        public void updatePosAndSize(int size, int max, float current)
        {
            this.length = (size == 0) ? 0 : (int) ((float) size / (float) (size + max) * (float) size);
            this.pos = (max == 0) ? 0 : (int) ((size - length) * current / max);
            super.updatePosAndSize();
        }

        @Override
        public void onLeftClick(int mouseX, int mouseY)
        {
            super.onLeftClick(mouseX, mouseY);
            if (this.isEnabled() && this.isVisible() && this.isMouseInBar())
            {
                this.barClicked = true;
            }
        }

        @Override
        public void onMouseReleased(int mouseX, int mouseY, int state)
        {
            super.onMouseReleased(mouseX, mouseY, state);
            if (barClicked)
            {
                this.barClicked = false;
            }
        }

        @Override
        public void playPressSound(SoundHandler soundHandlerIn)
        {
            ;
        }
    }
}
