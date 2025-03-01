package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.client.gui.widgets.GuiButtonAdvanced;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiLabelAdvanced;
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
                (IntSupplier) () -> 30, 6,
                (IntSupplier) () -> this.width - 30, 20, 0xE0E0E0);
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
        if (this.isOpening)
        {
            progress = (float) MathHelper.clampedLerp(0.9F, 1.0F, this.getAnimationProgress(CacGuiUtils
                    .guiBezierTranslation));
        }
        else if (this.isClosing)
        {
            progress = (float) MathHelper.clampedLerp(0.9F, 1.0F, 1 - this.getAnimationProgress(CacGuiUtils
                    .guiBezierTranslation));
        }
        // todo: 有上级页面时，0.9变为0.0，alpha不变小
        this.translationX += (this.parentScreen == null) ? 0.0F : (1.0F - progress) * this.width;
        this.translationY += (this.parentScreen == null) ? (progress - 1.0F) * this.height : 0.0F;
        GlStateManager.translate(translationX, translationY, 0.0F);
    }
}
