package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import java.util.function.IntSupplier;

public class GuiButtonAdvanced extends GuiWidget
{
    // GuiButton Fields BEGIN
    public String displayString = "";
    protected boolean hovered;
    // GuiButton Fields END
    protected static final ResourceLocation RTMMETRO_BUTTON_TEXTURES = new ResourceLocation(RTMMetro.MODID,
            "textures/gui/widgets.png");

    public GuiButtonAdvanced(IWidgetHolder holder, int id, IntSupplier xSupplier, IntSupplier ySupplier)
    {
        this(holder, id, xSupplier, ySupplier, () -> 200, () -> 20);
    }

    public GuiButtonAdvanced(IWidgetHolder holder, int id, IntSupplier xSupplier, IntSupplier ySupplier,
                             IntSupplier widthSupplier, IntSupplier heightSupplier)
    {
        super(holder, id, xSupplier, ySupplier, widthSupplier, heightSupplier);
    }

    protected int getHoverState(boolean mouseOver)
    {
        int i = 1;

        if (!super.isEnabled())
        {
            i = 0;
        }
        else if (mouseOver)
        {
            i = 2;
        }

        return i;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial)
    {
        if (this.isVisible())
        {
            this.hovered = this.isMouseInside();
            if (this.pScr != mc.currentScreen || pScr.isInAnimation())
            {
                this.hovered = false;
            }
            int k = this.getHoverState(this.hovered);
            CacGuiUtils.drawContinuousTexturedBox(RTMMETRO_BUTTON_TEXTURES, this.x, this.y, 0, 46 + k * 20, this.width,
                    this.height, 200,
                    20, 2, 3, 2, 2, this.zLevel, pScr);
            int color = 0xE0E0E0;
            if (!super.isEnabled())
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

            CacGuiUtils.drawCenteredString(mc.fontRenderer, buttonText, this.x + this.width / 2,
                    this.y + (this.height - 8) / 2, color);
        }
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        return !pScr.isInAnimation() && this.isEnabled() && this.isVisible() && this.isMouseInside();
    }

    public boolean isMouseOver()
    {
        return this.hovered;
    }

    public void playPressSound(SoundHandler soundHandlerIn)
    {
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.drawButton(pScr.mc, mouseX, mouseY, partialTicks);
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiWidget> T setDisplayString(String displayString)
    {
        this.displayString = displayString;
        return (T) this;
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY)
    {
        super.onLeftClick(mouseX, mouseY);
        if (this.isEnabled() && this.isVisible() && this.isMouseInside())
        {
            this.playPressSound(this.pScr.mc.getSoundHandler());
        }
    }
}
