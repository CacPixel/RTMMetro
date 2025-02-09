package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class GuiTextFieldAdvancedInt extends GuiTextFieldAdvanced implements IGuiWidget
{
    public int fieldValue;
    public int step = 1;
    public int minValue = Integer.MIN_VALUE;
    public int maxValue = Integer.MAX_VALUE;
    public boolean loop = false;

    public GuiTextFieldAdvancedInt(int id, FontRenderer par1, int x, int y, int w, int h,
                                   GuiScreenAdvanced pScr, int fieldValue)
    {
        super(id, par1, x, y, w, h, pScr);
        this.fieldValue = fieldValue;
    }

    public GuiTextFieldAdvancedInt setMinMax(int min, int max, boolean loop)
    {
        this.minValue = min;
        this.maxValue = max;
        this.loop = loop;
        return this;
    }

    public GuiTextFieldAdvancedInt setStep(int step)
    {
        this.step = step;
        return this;
    }


    @Override
    public void onScroll(int mouseX, int mouseY, int scroll)
    {
        if (this.isMouseInside())
        {
            try
            {
                if (scroll != 0)
                {
                    this.incValue(scroll);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean textboxKeyTyped(char word, int code)
    {
        boolean ret = super.textboxKeyTyped(word, code);
        if (this.isFocused() && this.getVisible() && this.isEnabled())
        {
            if (code == Keyboard.KEY_UP)
            {
                this.incValue(CacGuiUtils.DEFAULT_SCROLL_VALUE);
                this.fieldValue = CacGuiUtils.getFieldValue(this, this.fieldValue);
                this.checkValueAndSetText();
                this.pScr.hasValueUpdated = true;
            }
            else if (code == Keyboard.KEY_DOWN)
            {
                this.incValue(-CacGuiUtils.DEFAULT_SCROLL_VALUE);
                this.fieldValue = CacGuiUtils.getFieldValue(this, this.fieldValue);
                this.checkValueAndSetText();
                this.pScr.hasValueUpdated = true;
            }
            else
            {
                this.fieldValue = CacGuiUtils.getFieldValue(this, this.fieldValue);
                if (this.isValueValid())
                {
                    this.pScr.hasValueUpdated = true;
                }
                else
                {
                    this.checkValue();
                }
            }
        }
        return ret;
    }

    @Override
    public void incValue(int scroll)
    {
        super.incValue(scroll);
        int step = this.step;
        if (GuiScreen.isShiftKeyDown())
        {
            step *= 10;
        }
        else if (GuiScreen.isAltKeyDown())
        {
            step /= 10;
        }
        step = step * scroll / CacGuiUtils.DEFAULT_SCROLL_VALUE;
        this.fieldValue += step;
        this.checkValueAndSetText();
    }

    @Override
    public boolean isValueValid()
    {
        return this.fieldValue >= this.minValue && this.fieldValue <= this.maxValue;
    }

    @Override
    public void checkValueAndSetText()
    {
        super.checkValueAndSetText();
        this.setText(String.valueOf(this.fieldValue));
        this.pScr.hasValueUpdated = true;
    }

    @Override
    public void checkValue()
    {
        if (!loop)
        {
            this.fieldValue = Math.max(this.fieldValue, this.minValue);
            this.fieldValue = Math.min(this.fieldValue, this.maxValue);
        }
        else
        {
            int div = maxValue - minValue;
            fieldValue = fieldValue % div;
            if (fieldValue > maxValue)
            {
                fieldValue -= div;
            }
            if (fieldValue < minValue)
            {
                fieldValue += div;
            }
        }
    }
}
