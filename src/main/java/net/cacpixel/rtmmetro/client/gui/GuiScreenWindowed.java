package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.client.gui.widgets.GuiButtonAdvanced;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiLabelAdvanced;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.function.IntSupplier;

public abstract class GuiScreenWindowed extends GuiScreenAdvanced
{
    protected GuiButtonAdvanced buttonClose;
    protected GuiLabelAdvanced labelTitle;
    protected int windowWidth;
    protected int windowHeight;
    protected Align alignX = Align.CENTERED;
    protected Align alignY = Align.CENTERED;
    protected int lastX, lastY;
    private float blinkCounter = 0;

    public GuiScreenWindowed()
    {
        super();
        this.drawParent = true;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int screenWidth = scaledresolution.getScaledWidth();
        int screenHeight = scaledresolution.getScaledHeight();
        if (this.windowWidth == 0)
            this.windowWidth = screenWidth;
        if (this.windowHeight == 0)
            this.windowHeight = screenHeight;
        this.width = windowWidth;
        this.height = windowHeight;
        buttonClose = this.addWidget(GuiButtonAdvanced.class,
                        () -> 5, () -> 5, () -> 20, () -> 20)
                .setDisplayString("x").setListener(this::closeButtonCallback);
        labelTitle = this.addWidget(GuiLabelAdvanced.class,
                (IntSupplier) () -> 30, 0,
                (IntSupplier) () -> this.width - 30, 30, 0xE0E0E0).setAlignY(Align.CENTERED);
    }

    @Override
    public void screenResize()
    {
        this.width = windowWidth;
        this.height = windowHeight;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int screenWidth = scaledresolution.getScaledWidth();
        int screenHeight = scaledresolution.getScaledHeight();
        switch (this.alignX)
        {
        case LEFT_OR_UP_ALIGNED:
            this.x = 0;
            break;
        case CENTERED:
            this.x = screenWidth / 2 - windowWidth / 2;
            break;
        case RIGHT_OR_DOWN_ALIGNED:
            this.x = screenWidth - windowWidth;
            break;
        case FREE:
            this.x = MathHelper.clamp(lastX, 0, screenWidth - width);
            break;
        default:
            break;
        }
        switch (this.alignY)
        {
        case LEFT_OR_UP_ALIGNED:
            this.y = 0;
            break;
        case CENTERED:
            this.y = screenHeight / 2 - windowHeight / 2;
            break;
        case RIGHT_OR_DOWN_ALIGNED:
            this.y = screenHeight - windowHeight;
            break;
        case FREE:
            this.y = MathHelper.clamp(lastY, 0, screenHeight - height);
            break;
        default:
            break;
        }
        super.screenResize();
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiScreenWindowed> T setWindowSize(int windowWidth, int windowHeight)
    {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        return (T) this;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (this.labelTitle.isDragging())
        {
            x = mouseX - (labelTitle.lastClickedX);
            y = mouseY - (labelTitle.lastClickedY);
            alignX = alignY = Align.FREE;
        }
        else
        {
            lastX = x;
            lastY = y;
        }
        if (blinkCounter > 0)
        {
            if ((int) (blinkCounter / 2) % 2 > 0)
            {
                CacGuiUtils.drawHorizontalLine(0, width, 0, 0xFFFFFF | this.getAlphaInt(0xFF));
                CacGuiUtils.drawHorizontalLine(0, width, height, 0xFFFFFF | this.getAlphaInt(0xFF));
                CacGuiUtils.drawVerticalLine(0, 0, height, 0xFFFFFF | this.getAlphaInt(0xFF));
                CacGuiUtils.drawVerticalLine(width, 0, height, 0xFFFFFF | this.getAlphaInt(0xFF));
            }
            blinkCounter -= partialTicks;
        }
        if (blinkCounter < 0)
            blinkCounter = 0;
        super.draw(mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawDefaultBackground()
    {
        super.drawDefaultBackground();
    }

    @Override
    protected void mouseClicked(int x, int y, int button)
    {
        super.mouseClicked(x, y, button);
        if (!CacGuiUtils.isMouseInside(this.x, this.y, this.width, this.height))
        {
            blinkCounter = 20;
            Toolkit.getDefaultToolkit().beep();
        }
    }

    @Override
    protected void updateAnimation(float partialTicks)
    {
        super.updateAnimation(partialTicks);
        float scale = 1.0F;
        if (this.isOpening())
        {
            scale = (float) MathHelper.clampedLerp(0.9F, 1.0F,
                    this.getAnimationProgress(CacGuiUtils.guiBezierTranslation));
        }
        else if (this.isClosing())
        {
            scale = (float) MathHelper.clampedLerp(0.9F, 1.0F,
                    1 - this.getAnimationProgress(CacGuiUtils.guiBezierTranslation));
        }
        this.translationX += (this.width * (1 - scale)) / 2.0F;
        this.translationY += (this.height * (1 - scale)) / 2.0F;
        this.scaleX = scale;
        this.scaleY = scale;
    }
}
