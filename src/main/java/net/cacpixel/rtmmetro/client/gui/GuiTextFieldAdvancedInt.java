package net.cacpixel.rtmmetro.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiTextFieldAdvancedInt extends GuiTextFieldAdvanced
{
    public int fieldValue;
    public int step = 1;
    public int minValue = Integer.MIN_VALUE;
    public int maxValue = Integer.MAX_VALUE;
    public boolean loop = false;

    public GuiTextFieldAdvancedInt(int id, FontRenderer par1, int x, int y, int w, int h,
                                   GuiScreen pScr, int fieldValue)
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
    public void handleMouseInput()
    {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
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
    public void handleKeyboardInput()
    {
        super.handleKeyboardInput();
        if (this.isFocused())
        {
            if (Keyboard.getEventKey() == Keyboard.KEY_UP && Keyboard.isKeyDown(Keyboard.KEY_UP))
            {
                this.incValue(DEFAULT_SCROLL_VALUE);
                this.fieldValue = GuiHelper.getFieldValue(this, this.fieldValue);
                this.checkValueAndSetText();
            }
            else if (Keyboard.getEventKey() == Keyboard.KEY_DOWN && Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            {
                this.incValue(-DEFAULT_SCROLL_VALUE);
                this.fieldValue = GuiHelper.getFieldValue(this, this.fieldValue);
                this.checkValueAndSetText();
            }
        }
    }

    @Override
    public boolean textboxKeyTyped(char word, int code)
    {
        boolean ret = super.textboxKeyTyped(word, code);
        this.fieldValue = GuiHelper.getFieldValue(this, this.fieldValue);
        if (this.isValueValid())
        {
            this.setScrValueUpdated();
        }
        else
        {
            this.checkValue();
        }
        return ret;
    }

    @Override
    public void incValue(int scroll)
    {
        int step = this.step;
        if (GuiScreen.isShiftKeyDown())
        {
            step *= 10;
        }
        else if (GuiScreen.isAltKeyDown())
        {
            step /= 10;
        }
        step = step * scroll / DEFAULT_SCROLL_VALUE;
        this.fieldValue += step;
        this.checkValueAndSetText();
    }

    @Override
    public boolean isValueValid()
    {
        if (this.fieldValue < this.minValue || this.fieldValue > this.maxValue)
        {
            return false;
        }
        return true;
    }

    @Override
    public void checkValueAndSetText()
    {
        super.checkValueAndSetText();
        this.setText(String.valueOf(this.fieldValue));
        this.setScrValueUpdated();
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
