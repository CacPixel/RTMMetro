package net.cacpixel.rtmmetro.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public abstract class GuiScreenWindowed extends GuiScreenAdvanced
{
    @Override
    protected void updateAnimation(float partialTicks)
    {
        super.updateAnimation(partialTicks);
        float scale = 1.0F;
        if (this.isOpening)
        {
            scale = (float) MathHelper.clampedLerp(0.9F, 1.0F,
                    this.getAnimationProgress(CacGuiUtils.guiBezierTranslation));
        }
        else if (this.isClosing)
        {
            scale = (float) MathHelper.clampedLerp(0.9F, 1.0F,
                    1 - this.getAnimationProgress(CacGuiUtils.guiBezierTranslation));
        }
        this.translationX = (this.width * (1 - scale)) / 2.0F;
        this.translationY = (this.height * (1 - scale)) / 2.0F;
        this.scaleX = scale;
        this.scaleY = scale;
        GlStateManager.translate((this.width * (1 - scale)) / 2.0F, (this.height * (1 - scale)) / 2.0F, 0.0F);
        GlStateManager.scale(scale, scale, 1.0F);
    }
}
