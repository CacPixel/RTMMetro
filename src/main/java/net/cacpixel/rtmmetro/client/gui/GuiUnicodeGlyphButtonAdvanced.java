package net.cacpixel.rtmmetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.function.Consumer;

public class GuiUnicodeGlyphButtonAdvanced extends GuiButtonAdvanced // GuiUnicodeGlyphButton
{
    public String glyph;
    public float glyphScale;

    public GuiUnicodeGlyphButtonAdvanced(int id, int xPos, int yPos, int width, int height, String displayString, String glyph,
                                         float glyphScale, GuiScreenAdvanced pScr, Consumer<?> callback)
    {
        super(id, xPos, yPos, width, height, displayString, pScr, callback);
        this.glyph = glyph;
        this.glyphScale = glyphScale;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (this.pScr != mc.currentScreen || pScr.isInAnimation())
            {
                this.hovered = false;
            }
            int k = this.getHoverState(this.hovered);
            CacGuiUtils.drawContinuousTexturedBox(RTMMETRO_BUTTON_TEXTURES, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20,
                    2, 3, 2, 2, this.zLevel, pScr);
            this.mouseDragged(mc, mouseX, mouseY);
            int color = 14737632;

            if (packedFGColour != 0)
            {
                color = packedFGColour;
            }
            else if (!this.enabled)
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
            this.drawCenteredString(mc.fontRenderer, glyph,
                    (int) (((this.x + (this.width / 2) - (strWidth / 2)) / glyphScale) - (glyphWidth / (2 * glyphScale)) + 2),
                    (int) (((this.y + ((this.height - 8) / glyphScale) / 2) - 1) / glyphScale), color);
            GlStateManager.popMatrix();

            this.drawCenteredString(mc.fontRenderer, buttonText, (int) (this.x + (this.width / 2) + (glyphWidth / glyphScale)),
                    this.y + (this.height - 8) / 2, color);
        }
    }
}
