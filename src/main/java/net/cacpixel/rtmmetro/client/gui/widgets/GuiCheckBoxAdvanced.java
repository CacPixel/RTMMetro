package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiCheckBoxAdvanced extends GuiButtonAdvanced implements IGuiWidget
{
    private boolean isChecked;
    private final int boxWidth;

    public GuiCheckBoxAdvanced(int id, int xPos, int yPos, String displayString, boolean isChecked,
                               GuiScreenAdvanced pScr, IActionListener<? extends GuiCheckBoxAdvanced> callback)
    {
        super(id, xPos, yPos, displayString, pScr, callback);
        this.isChecked = isChecked;
        this.boxWidth = 13;
        this.height = 13;
        this.width = this.boxWidth + 4 + Minecraft.getMinecraft().fontRenderer.getStringWidth(displayString);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.boxWidth && mouseY < this.y + this.height;
//            GuiUtils.drawContinuousTexturedBox(RTMMETRO_BUTTON_TEXTURES, this.x, this.y, 0, 46, this.boxWidth, this.height, 200, 20, 2,
//            3, 2, 2, this.zLevel);
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
            color |= pScr.getAlphaInt(0xFF);

            mc.getTextureManager().bindTexture(RTMMETRO_BUTTON_TEXTURES);
            float red = (float) (color >> 16 & 255) / 255.0F;
            float blue = (float) (color >> 8 & 255) / 255.0F;
            float green = (float) (color & 255) / 255.0F;
            float alpha = (float) (color >> 24 & 255) / 255.0F;
            GlStateManager.color(red, blue, green, alpha);
            if (this.isChecked)
//                this.drawCenteredString(mc.fontRenderer, "x", this.x + this.boxWidth / 2 + 1, this.y + 1, 14737632);
                GuiUtils.drawTexturedModalRect(this.x, this.y, 0, 206, this.boxWidth, this.height, this.zLevel);
            else
                GuiUtils.drawTexturedModalRect(this.x, this.y, 0, 219, this.boxWidth, this.height, this.zLevel);

            this.drawString(mc.fontRenderer, displayString, this.x + this.boxWidth + 4, this.y + 3, color);
        }
    }

    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton)
    {
        super.onClick(mouseX, mouseY, mouseButton);
        if (this.isMouseInside())
            this.isChecked = !this.isChecked;
    }

    public boolean isChecked()
    {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked)
    {
        this.isChecked = isChecked;
    }
}
