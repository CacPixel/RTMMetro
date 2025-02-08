package net.cacpixel.rtmmetro.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public abstract class GuiScreenWindowed extends GuiScreenAdvanced
{
    @Override
    protected void updateAnimation(float partialTicks)
    {
        super.updateAnimation(partialTicks);
        if (this.isOpening)
        {
            float scale = (float) MathHelper.clampedLerp(0.9F, 1.0F, this.getAnimationProgress(CacGuiUtils.guiBezierTranslation));
            GlStateManager.translate((this.width * (1 - scale)) / 2.0F, (this.height * (1 - scale)) / 2.0F, 0.0F);
            GlStateManager.scale(scale, scale, 1.0F);
        }
        else if (this.isClosing)
        {
            float scale = (float) MathHelper.clampedLerp(0.9F, 1.0F, 1 - this.getAnimationProgress(CacGuiUtils.guiBezierTranslation));
            GlStateManager.translate((this.width * (1 - scale)) / 2.0F, (this.height * (1 - scale)) / 2.0F, 0.0F);
            GlStateManager.scale(scale, scale, 1.0F);
        }
    }
}
