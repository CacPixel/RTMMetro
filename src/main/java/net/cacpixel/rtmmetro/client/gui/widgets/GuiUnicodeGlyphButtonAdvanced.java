package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.IntSupplier;

public class GuiUnicodeGlyphButtonAdvanced extends GuiButtonAdvanced
{
    public String glyph;
    public float glyphScale;

    public GuiUnicodeGlyphButtonAdvanced(GuiScreenAdvanced pScr, int id, IntSupplier xSupplier, IntSupplier ySupplier,
                                         IntSupplier widthSupplier, IntSupplier heightSupplier, String displayString,
                                         String glyph, float glyphScale)
    {
        super(pScr, id, xSupplier, ySupplier, widthSupplier, heightSupplier);
        this.glyph = glyph;
        this.glyphScale = glyphScale;
        this.setDisplayString(displayString);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial)
    {
        if (super.isVisible())
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (this.pScr != mc.currentScreen || pScr.isInAnimation())
            {
                this.hovered = false;
            }
            int k = this.getHoverState(this.hovered);
            CacGuiUtils.drawContinuousTexturedBox(RTMMETRO_BUTTON_TEXTURES, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200,
                    20,
                    2, 3, 2, 2, this.zLevel, pScr);
            int color = 14737632;
            if (!super.isEnabled())
            {
                color = 10526880;
            }
            else if (this.hovered)
            {
//                color = 16777120;
            }
            color |= pScr.getAlphaInt(0xFF);

            String buttonText = this.displayString;
            int glyphWidth = (int) (mc.fontRenderer.getStringWidth(glyph) * glyphScale);
            int strWidth = mc.fontRenderer.getStringWidth(buttonText);
            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
            int totalWidth = strWidth + glyphWidth;

            if (totalWidth > width - 6 && totalWidth > ellipsisWidth)
                buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";

            strWidth = mc.fontRenderer.getStringWidth(buttonText);
            totalWidth = glyphWidth + strWidth;

            GlStateManager.pushMatrix();
            GlStateManager.scale(glyphScale, glyphScale, 1.0F);
            CacGuiUtils.drawCenteredString(mc.fontRenderer, glyph,
                    (int) (((this.x + (this.width / 2) - (strWidth / 2)) / glyphScale) - (glyphWidth / (2 * glyphScale)) + 2),
                    (int) (((this.y + ((this.height - 8) / glyphScale) / 2) - 1) / glyphScale), color);
            GlStateManager.popMatrix();

            CacGuiUtils.drawCenteredString(mc.fontRenderer, buttonText, (int) (this.x + (this.width / 2) + (glyphWidth / glyphScale)),
                    this.y + (this.height - 8) / 2, color);
        }
    }
}
