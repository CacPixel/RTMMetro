package net.cacpixel.rtmmetro.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public abstract class GuiFullScreen extends GuiScreenAdvanced
{
    @Override
    protected void updateAnimation(float partialTicks)
    {
        super.updateAnimation(partialTicks);
        if (this.isOpening)
        {
            float progress = (float) MathHelper.clampedLerp(0.9F, 1.0F, this.getAnimationProgress(CacGuiUtils.guiBezierTranslation));
            GlStateManager.translate(0.0F, (progress - 1.0F) * this.height, 0.0F);
        }
        else if (this.isClosing)
        {
            float progress = (float) MathHelper.clampedLerp(0.9F, 1.0F, 1 - this.getAnimationProgress(CacGuiUtils.guiBezierTranslation));
            GlStateManager.translate(0.0F, (progress - 1.0F) * this.height, 0.0F);
        }
    }
}
