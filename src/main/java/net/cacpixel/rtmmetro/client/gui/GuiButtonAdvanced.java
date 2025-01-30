package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.RTMMetro;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.function.Consumer;

public class GuiButtonAdvanced extends GuiButton // GuiButtonExt
{
    protected static final ResourceLocation RTMMETRO_BUTTON_TEXTURES = new ResourceLocation(RTMMetro.MODID, "textures/gui/widgets.png");
    public GuiScreenAdvanced pScr;
    public Consumer<?> callback;

    public GuiButtonAdvanced(int id, int xPos, int yPos, String displayString,
                             GuiScreenAdvanced pScr, Consumer<?> callback)
    {
        this(id, xPos, yPos, 200, 20, displayString, pScr, callback);
    }

    public GuiButtonAdvanced(int id, int xPos, int yPos, int width, int height, String displayString,
                             GuiScreenAdvanced pScr, Consumer<?> callback)
    {
        super(id, xPos, yPos, width, height, displayString);
        this.pScr = pScr;
        this.callback = callback;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (this.pScr != mc.currentScreen)
            {
                this.hovered = false;
            }
            int k = this.getHoverState(this.hovered);
            GuiUtils.drawContinuousTexturedBox(RTMMETRO_BUTTON_TEXTURES, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20,
                    2, 3, 2, 2, this.zLevel);
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

            String buttonText = this.displayString;
            // GuiButtonExt的缩减字符功能，我更希望能全部显示出所有的字符，省略了反而会不知道末尾显示什么
//            int strWidth = mc.fontRenderer.getStringWidth(buttonText);
//            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
//
//            if (strWidth > width - 6 && strWidth > ellipsisWidth)
//                buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";

            this.drawCenteredString(mc.fontRenderer, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
        }
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
