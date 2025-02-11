package net.cacpixel.rtmmetro.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public abstract class GuiFullScreen extends GuiScreenAdvanced
{
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
