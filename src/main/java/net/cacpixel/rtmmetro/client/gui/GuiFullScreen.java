package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.client.gui.widgets.GuiButtonAdvanced;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiLabelAdvanced;
import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import java.util.function.IntSupplier;

public abstract class GuiFullScreen extends GuiScreenAdvanced
{
    protected GuiButtonAdvanced buttonClose;
    protected GuiLabelAdvanced labelTitle;

    @Override
    public void initGui()
    {
        super.initGui();
        buttonClose = this.addWidget(GuiButtonAdvanced.class,
                        () -> 5, () -> 5, () -> 20, () -> 20)
                .setDisplayString(this.parentScreen == null ? "x" : "<").setListener(this::closeButtonCallback);
        labelTitle = this.addWidget(GuiLabelAdvanced.class,
                (IntSupplier) () -> 30, 0,
                (IntSupplier) () -> this.width - 30, 30, 0xE0E0E0).setAlignY(Align.CENTERED);
    }

    @Override
    public void drawDefaultBackground()
    {
        CacGuiUtils.drawRect(0, 0, width, height, 0x101010 | this.getAlphaInt(0x80));
        CacGuiUtils.drawRect(0, 0, this.width, 30, 0x101010 | this.getAlphaInt(0xA0));
        CacGuiUtils.drawRect(0, this.height - 40, this.width,
                this.height - (int) this.translationY + 1, 0x101010 | this.getAlphaInt(0xA0));
    }

    @Override
    public void drawDefaultBackgroundBefore()
    {

    }

    @Override
    protected void updateAnimation(float partialTicks)
    {
        super.updateAnimation(partialTicks);
        boolean isLastScreen = this.parentScreen == null && this.mc.currentScreen == this;
        boolean isThisScreen = this.mc.currentScreen == this;
        float progress = 1.0F;
        float lowerBnd = (isLastScreen) ? 0.9F : (isThisScreen) ? -0.02F : 0.5F;
        float upperBnd = 1.0F;
        BezierCurveAdvanced curve = CacGuiUtils.guiBezierTranslation;
        if (this.isOpening())
        {
            progress = (float) MathHelper.clampedLerp(lowerBnd, upperBnd, this.getAnimationProgress(curve));
        }
        else if (this.isClosing())
        {
            progress = (float) MathHelper.clampedLerp(lowerBnd, upperBnd, 1 - this.getAnimationProgress(curve));
        }
        this.translationX +=
                (isLastScreen) ? 0.0F : (isThisScreen) ? (1.0F - progress) * this.width : (progress - 1.0F) / 2.0F * this.width;
        this.translationY += (isLastScreen) ? (progress - 1.0F) * this.height : 0.0F;
        GlStateManager.translate(translationX, translationY, 0.0F);
    }

    @Override
    protected void updateAlpha()
    {
        if (this.parentScreen == null && this.mc.currentScreen == this)
        {
            super.updateAlpha();
        }
        else
        {
            if (this.getAnimationStatus() == AnimationStatus.CLOSED)
                this.alpha = 0.02F;
            else
                this.alpha = 1.0F;
        }
    }
}
