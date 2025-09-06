package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.MouseGrabber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.IntSupplier;

public class GuiUnicodeGlyphButtonAdvanced extends GuiButtonAdvanced
{
    public String glyph;
    public float glyphScale;

    public GuiUnicodeGlyphButtonAdvanced(IWidgetHolder holder, IntSupplier xSupplier, IntSupplier ySupplier,
                                         IntSupplier widthSupplier, IntSupplier heightSupplier, String displayString,
                                         String glyph, float glyphScale)
    {
        super(holder, xSupplier, ySupplier, widthSupplier, heightSupplier);
        this.glyph = glyph;
        this.glyphScale = glyphScale;
        this.setDisplayString(displayString);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial)
    {
        if (this.isVisible())
        {
            this.hovered = this.isMouseInside() && !MouseGrabber.INSTANCE.isGrabbed();
            if (this.getScreen() != mc.currentScreen || getScreen().isInAnimation())
            {
                this.hovered = false;
            }
            int k = this.getHoverState(this.hovered);
            CacGuiUtils.drawContinuousTexturedBox(getButtonTexture(), this.x, this.y, 0, 46 + k * 20, this.width,
                    this.height, 200,
                    20,
                    2, 3, 2, 2, this.zLevel, getScreen());
            int color = 14737632;
            if (!super.isEnabled())
            {
                color = 10526880;
            }
            else if (this.hovered)
            {
//                color = 16777120;
            }
            color |= getScreen().getAlphaInt(0xFF);

            String buttonText = this.displayString;
            int glyphWidth = (int) (mc.fontRenderer.getStringWidth(glyph) * glyphScale);
            int strWidth = mc.fontRenderer.getStringWidth(buttonText);
            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
            int totalWidth = strWidth + glyphWidth;

            if (totalWidth > width - 6 && totalWidth > ellipsisWidth)
                buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";

            strWidth = mc.fontRenderer.getStringWidth(buttonText);
            totalWidth = glyphWidth + strWidth;

            this.getScreen().glPushMatrix();
            GlStateManager.scale(glyphScale, glyphScale, 1.0F);
            CacGuiUtils.drawCenteredString(mc.fontRenderer, glyph,
                    (int) (((this.x + (this.width / 2) - (strWidth / 2)) / glyphScale) -
                            (glyphWidth / (2 * glyphScale)) + 2),
                    (int) (((this.y + ((this.height - 8) / glyphScale) / 2) - 1) / glyphScale), color);
            this.getScreen().glPopMatrix();

            CacGuiUtils.drawCenteredString(mc.fontRenderer, buttonText,
                    (int) (this.x + (this.width / 2) + (glyphWidth / glyphScale)),
                    this.y + (this.height - 8) / 2, color);
        }
    }
}
