package net.cacpixel.rtmmetro.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.text.DecimalFormat;

public class GuiTextFieldAdvancedFloat extends GuiTextFieldAdvanced
{
    public float fieldValue;
    public float step;
    public float minValue = Float.MIN_VALUE;
    public float maxValue = Float.MAX_VALUE;

    public GuiTextFieldAdvancedFloat(int id, FontRenderer par1, int x, int y, int w, int h,
                                     GuiScreen pScr, float fieldValue)
    {
        this(id, par1, x, y, w, h, pScr, fieldValue, 0.100000000001F);
    }

    public GuiTextFieldAdvancedFloat(int id, FontRenderer par1, int x, int y, int w, int h,
                                     GuiScreen pScr, float fieldValue, float step)
    {
        super(id, par1, x, y, w, h, pScr);
        this.fieldValue = fieldValue;
        this.step = step;
    }

    public GuiTextFieldAdvancedFloat setMinMax(float min, float max)
    {
        this.minValue = min;
        this.maxValue = max;
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
            }
            else if (Keyboard.getEventKey() == Keyboard.KEY_DOWN && Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            {
                this.incValue(-DEFAULT_SCROLL_VALUE);
            }
            this.fieldValue = GUIHelper.getFieldValue(this, this.fieldValue);
        }
    }

    @Override
    public boolean textboxKeyTyped(char word, int code)
    {
        boolean ret = super.textboxKeyTyped(word, code);
        this.fieldValue = GUIHelper.getFieldValue(this, this.fieldValue);
        if (!this.isValueValid())
        {
            this.checkValue();
        }
        return ret;
    }

    @Override
    public void setScrValueUpdated()
    {
        super.setScrValueUpdated();
    }

    public void incValue(int scroll)
    {
        float step = this.step;
        if (GuiScreen.isShiftKeyDown())
        {
            step *= 10;
        }
        else if (GuiScreen.isAltKeyDown())
        {
            step /= 10;
        }
        this.fieldValue += ((scroll > 0) ? step : -step);
        this.checkValue();
    }

    @Override
    public boolean isValueValid()
    {
        if (this.fieldValue < this.minValue || this.fieldValue > this.maxValue
                || Float.isNaN(this.fieldValue) || Float.isInfinite(this.fieldValue))
        {
            return false;
        }
        return true;
    }

    public void checkValue()
    {
        if (Float.isNaN(fieldValue) || Float.isInfinite(fieldValue))
        {
            this.fieldValue = 0.0f;
        }
        if (-0.0000001f < this.fieldValue && this.fieldValue < 0.0000001f)
        {
            this.fieldValue = 0.0f;
        }
        this.fieldValue = Math.max(this.fieldValue, this.minValue);
        this.fieldValue = Math.min(this.fieldValue, this.maxValue);
        this.setText(new DecimalFormat("0.0##").format(this.fieldValue));
        this.setScrValueUpdated();
    }
}
