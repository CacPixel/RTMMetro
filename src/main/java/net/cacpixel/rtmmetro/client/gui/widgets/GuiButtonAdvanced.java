package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiButtonAdvanced extends GuiButton implements IGuiWidget
{
    protected static final ResourceLocation RTMMETRO_BUTTON_TEXTURES = new ResourceLocation(RTMMetro.MODID, "textures/gui/widgets.png");
    public GuiScreenAdvanced pScr;
    private IActionListener listener;

    public GuiButtonAdvanced(int id, int xPos, int yPos, String displayString,
                             GuiScreenAdvanced pScr, IActionListener listener)
    {
        this(id, xPos, yPos, 200, 20, displayString, pScr, listener);
    }

    public GuiButtonAdvanced(int id, int xPos, int yPos, int width, int height, String displayString,
                             GuiScreenAdvanced pScr, IActionListener listener)
    {
        super(id, xPos, yPos, width, height, displayString);
        this.pScr = pScr;
        this.listener = listener;
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
            CacGuiUtils.drawContinuousTexturedBox(RTMMETRO_BUTTON_TEXTURES, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200,
                    20, 2, 3, 2, 2, this.zLevel, pScr);
            this.mouseDragged(mc, mouseX, mouseY);
            int color = 0xE0E0E0;

            if (packedFGColour != 0)
            {
                color = packedFGColour;
            }
            else if (!this.enabled)
            {
                color = 0xA0A0A0;
            }
            else if (this.hovered)
            {
//                color = 0xFFFFA0;
            }

            String buttonText = this.displayString;
            color |= pScr.getAlphaInt(0xFF);
            // GuiButtonExt的缩减字符功能，我更希望能全部显示出所有的字符，省略了反而会不知道末尾显示什么
//            int strWidth = mc.fontRenderer.getStringWidth(buttonText);
//            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
//
//            if (strWidth > width - 6 && strWidth > ellipsisWidth)
//                buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";

            this.drawCenteredString(mc.fontRenderer, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        return !pScr.isInAnimation() && this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y &&
                mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    @Override
    public boolean isMouseInside()
    {
        return CacGuiUtils.isMouseInside(x, y, width, height);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.drawButton(pScr.mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isVisible()
    {
        return this.visible;
    }

    @Override
    public boolean isEnabled()
    {
        return this.enabled;
    }

    public IActionListener getListener()
    {
        return listener;
    }

    public void setListener(IActionListener listener)
    {
        this.listener = listener;
    }
}
