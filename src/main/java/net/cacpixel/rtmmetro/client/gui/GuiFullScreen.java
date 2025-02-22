package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.client.gui.widgets.GuiButtonAdvanced;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiLabelAdvanced;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiUnicodeGlyphButtonAdvanced;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiWidget;
import net.minecraft.client.gui.GuiLabel;
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
                        () -> this.width - 23, () -> 5, () -> 18, () -> 18)
                .setDisplayString("x").setListener(this::closeButtonCallback);
        labelTitle = this.addWidget(GuiLabelAdvanced.class, this.getNextWidgetId(),
                (IntSupplier) () -> 0, 6,
                (IntSupplier) () -> this.width, 20, 0xE0E0E0).setCentered();
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
        this.translationX = 0.0F;
        this.translationY = (progress - 1.0F) * this.height;
        GlStateManager.translate(translationX, translationY, 0.0F);
    }
}
