package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.MouseGrabber;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.function.IntSupplier;

public class GuiCheckBoxAdvanced extends GuiButtonAdvanced
{
    private boolean isChecked;
    private final int boxWidth;

    public GuiCheckBoxAdvanced(IWidgetHolder holder, IntSupplier xSupplier, IntSupplier ySupplier,
                               IntSupplier widthSupplier, IntSupplier heightSupplier,
                               String displayString, boolean isChecked)
    {
        super(holder, xSupplier, ySupplier, () -> 200, () -> 13);
        this.isChecked = isChecked;
        this.boxWidth = 13;
        this.height = 13;
        this.widthSupplier = () -> this.boxWidth + 4 +
                Minecraft.getMinecraft().fontRenderer.getStringWidth(displayString);
        this.width = this.widthSupplier.getAsInt();
        this.setDisplayString(displayString);
    }

    public GuiCheckBoxAdvanced(IWidgetHolder holder, IntSupplier xSupplier, IntSupplier ySupplier,
                               String displayString, boolean isChecked)
    {
        this(holder, xSupplier, ySupplier, null, null, displayString, isChecked);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial)
    {
        if (this.isVisible())
        {
            this.hovered = this.getEventClick().canInteract() && !MouseGrabber.INSTANCE.isGrabbed();
//            GuiUtils.drawContinuousTexturedBox(RTMMETRO_BUTTON_TEXTURES, this.x, this.y, 0, 46, this.boxWidth, this
//            .height, 200, 20, 2,
//            3, 2, 2, this.zLevel);
            int color = 14737632;
            if (!super.isEnabled())
            {
                color = 10526880;
            }
            color |= getScreen().getAlphaInt(0xFF);

            mc.getTextureManager().bindTexture(getButtonTexture());
            CacGuiUtils.glColor(color);
            if (this.isChecked)
//                this.drawCenteredString(mc.fontRenderer, "x", this.x + this.boxWidth / 2 + 1, this.y + 1, 14737632);
                GuiUtils.drawTexturedModalRect(this.x, this.y, 0, 206, this.boxWidth, this.height, this.zLevel);
            else
                GuiUtils.drawTexturedModalRect(this.x, this.y, 0, 219, this.boxWidth, this.height, this.zLevel);

            CacGuiUtils.drawString(mc.fontRenderer, displayString, this.x + this.boxWidth + 4, this.y + 3, color);
        }
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY)
    {
        super.onLeftClick(mouseX, mouseY);
        if (this.isEnabled() && this.isVisible() && this.getEventClick().canInteract())
        {
            this.isChecked = !this.isChecked;
        }
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
