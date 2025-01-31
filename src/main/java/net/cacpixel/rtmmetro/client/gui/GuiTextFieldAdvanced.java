package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiTextFieldAdvanced extends GuiTextField
{
    protected final GuiScreenAdvanced pScr;
    private final List<String> tips = new ArrayList<>();
    public static final int DEFAULT_SCROLL_VALUE = 120;

    public GuiTextFieldAdvanced(int id, FontRenderer par1, int x, int y, int w, int h, GuiScreenAdvanced pScr)
    {
        super(id, par1, x, y, w, h);
        this.pScr = pScr;
    }

    public void handleMouseInput()
    {

    }

    public void handleKeyboardInput()
    {
        this.setScrValueUpdated();
    }

    public boolean isValueValid()
    {
        return true;
    }

    // NoSuchFieldError guiResponder with optifine in dev env
    public boolean isMouseInside()
    {
        Minecraft mc = NGTUtilClient.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int mouseX = Mouse.getX() / sr.getScaleFactor();
        int mouseY = (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor();
        return this.x < mouseX && mouseX < (this.x + this.width)
                && this.y < mouseY && mouseY < (this.y + this.height);
    }

    @Override
    public boolean textboxKeyTyped(char word, int code)
    {
        return super.textboxKeyTyped(word, code);
    }

    public void drawTextBox(int mouseX, int mouseY)
    {
        super.drawTextBox();

        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if (hovered && !this.tips.isEmpty())
        {
            GuiScreenAdvanced.drawHoveringTextS(this.tips, mouseX, mouseY, this.pScr);
        }
    }

    public void setScrValueUpdated()
    {
        if (this.pScr instanceof GuiScreenAdvanced)
        {
            ((GuiScreenAdvanced) this.pScr).hasValueUpdated = true;
        }
    }

    public void checkValueAndSetText()
    {
        this.checkValue();
    }

    public void incValue(int scroll)
    {
        float pitch = GuiScreen.isAltKeyDown() ? 2.0F : GuiScreen.isShiftKeyDown() ? 1.0F : 1.5F;
        this.pScr.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_HAT, pitch));
    }

    public GuiTextFieldAdvanced addTips(String par1)
    {
        this.tips.add(par1);
        return this;
    }

    public boolean isEnabled()
    {
        return this.isEnabled;
    }

    public void checkValue()
    {

    }
}
