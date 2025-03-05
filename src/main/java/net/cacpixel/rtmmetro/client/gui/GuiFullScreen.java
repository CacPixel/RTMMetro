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
        buttonClose = this.addWidget(GuiButtonAdvanced.class, this.getNextWidgetId(),
                        () -> 5, () -> 5, () -> 20, () -> 20)
                .setDisplayString(this.parentScreen == null ? "x" : "<").setListener(this::closeButtonCallback);
        labelTitle = this.addWidget(GuiLabelAdvanced.class, this.getNextWidgetId(),
                (IntSupplier) () -> 30, 0,
                (IntSupplier) () -> this.width - 30, 30, 0xE0E0E0).setAlignY(Align.CENTERED);
    }

    @Override
    public void drawDefaultBackground()
    {
        CacGuiUtils.drawRect(0, 0, this.width, 30, 0x101010 | this.getAlphaInt(0xA0));
        CacGuiUtils.drawRect(0, this.height - 40, this.width,
                this.height - (int) this.translationY + 1, 0x101010 | this.getAlphaInt(0xA0));
    }

    @Override
    public void drawDefaultBackgroundBefore()
    {
        CacGuiUtils.drawRect(0, 0, width, height, 0x101010 | this.getAlphaInt(0x80));
    }

    @Override
    protected void updateAnimation(float partialTicks)
    {
        super.updateAnimation(partialTicks);
        float progress = 1.0F;
        float lowerBnd = (this.parentScreen == null) ? 0.9F : -0.05F;
        BezierCurveAdvanced curve = CacGuiUtils.guiBezierTranslation;
        if (this.isOpening)
        {
            progress = (float) MathHelper.clampedLerp(lowerBnd, 1.0F, this.getAnimationProgress(curve));
        }
        else if (this.isClosing)
        {
            progress = (float) MathHelper.clampedLerp(lowerBnd, 1.0F, 1 - this.getAnimationProgress(curve));
        }
        this.translationX += (this.parentScreen == null) ? 0.0F : (1.0F - progress) * this.width;
        this.translationY += (this.parentScreen == null) ? (progress - 1.0F) * this.height : 0.0F;
        if (parentScreen != null)
        {
            parentScreen.translationX += (progress - 1.0f) * parentScreen.width;
        }
        GlStateManager.translate(translationX, translationY, 0.0F);
    }

    @Override
    protected void updateAlpha()
    {
        if (this.parentScreen == null)
        {
            super.updateAlpha();
        }
        else
        {
            if (!this.isInAnimation())
            {
                if (this.closeFlag)
                    this.alpha = 0.02F;
                else
                    this.alpha = 1.0F;
            }
            else
            {
                this.alpha = 1.0F;
            }
        }
    }
}
