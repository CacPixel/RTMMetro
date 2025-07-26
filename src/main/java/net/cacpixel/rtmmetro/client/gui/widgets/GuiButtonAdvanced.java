package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.*;
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
    public Image icon;
    public Align alignX = Align.CENTERED;
    public Align alignY = Align.CENTERED;
    private boolean wrapString = false;

    public GuiButtonAdvanced(IWidgetHolder holder, IntSupplier xSupplier, IntSupplier ySupplier)
    {
        this(holder, xSupplier, ySupplier, () -> 200, () -> 20);
    }

    public GuiButtonAdvanced(IWidgetHolder holder, IntSupplier xSupplier, IntSupplier ySupplier,
                             IntSupplier widthSupplier, IntSupplier heightSupplier)
    {
        super(holder, xSupplier, ySupplier, widthSupplier, heightSupplier);
    }

    public static ResourceLocation getButtonTexture()
    {
        return GuiTheme.getCurrentResourceLocation("widgets");
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
            this.hovered = this.isMouseInside() && !MouseGrabber.INSTANCE.isGrabbed();
            if (this.screen != mc.currentScreen || screen.isInAnimation())
            {
                this.hovered = false;
            }
            int k = this.getHoverState(this.hovered);
            CacGuiUtils.drawContinuousTexturedBox(getButtonTexture(), this.x, this.y, 0, 46 + k * 20, this.width,
                    this.height, 200,
                    20, 2, 3, 2, 2, this.zLevel, screen);
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
            color |= screen.getAlphaInt(0xFF);
            CacGuiUtils.drawString(buttonText, x, y, width, height, color, alignX, alignY, icon == null ? null :
                    new Image(icon).setColor(icon.color | screen.getAlphaInt(0xFF)), wrapString);
        }
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        return !screen.isInAnimation() && this.isEnabled() && this.isVisible() && this.isMouseInside();
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
        this.drawButton(screen.mc, mouseX, mouseY, partialTicks);
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiWidget> T setDisplayString(String displayString)
    {
        this.displayString = displayString;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiWidget> T setIcon(Image icon)
    {
        this.icon = icon;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiWidget> T setAlignX(Align align)
    {
        this.alignX = align;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiWidget> T setAlignY(Align align)
    {
        this.alignY = align;
        return (T) this;
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY)
    {
        super.onLeftClick(mouseX, mouseY);
        if (this.isEnabled() && this.isVisible() && this.isMouseInside())
        {
            this.playPressSound(this.screen.mc.getSoundHandler());
        }
    }

    public static Image getSaveIcon()
    {
        return new Image(GuiTheme.getCurrentResourceLocation("icon/save"),
                0, 0, 64, 64, 64, 64, 0xFFFFFF);
    }

    public boolean isWrapString()
    {
        return wrapString;
    }

    public GuiButtonAdvanced setWrapString(boolean wrapString)
    {
        this.wrapString = wrapString;
        return this;
    }
}
