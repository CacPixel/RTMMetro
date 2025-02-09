package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;

public class GuiTextFieldAdvancedFloat extends GuiTextFieldAdvanced implements IGuiWidget
{
    public float fieldValue;
    public float step = 0.100000000001F;
    public float minValue = Float.MIN_VALUE;
    public float maxValue = Float.MAX_VALUE;
    public boolean loop = false;
    public static final String FORMAT_PATTERN = "0.0##";

    public GuiTextFieldAdvancedFloat(int id, FontRenderer par1, int x, int y, int w, int h,
                                     GuiScreenAdvanced pScr, float fieldValue)
    {
        super(id, par1, x, y, w, h, pScr);
        this.fieldValue = fieldValue;
    }

    public GuiTextFieldAdvancedFloat setMinMax(float min, float max, boolean loop)
    {
        this.minValue = min;
        this.maxValue = max;
        this.loop = loop;
        return this;
    }

    public GuiTextFieldAdvancedFloat setStep(float step)
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
        float step = this.step;
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
        return !(this.fieldValue < this.minValue) && !(this.fieldValue > this.maxValue)
                && !Float.isNaN(this.fieldValue) && !Float.isInfinite(this.fieldValue);
    }

    @Override
    public void checkValueAndSetText()
    {
        super.checkValueAndSetText();
        this.setText(new DecimalFormat(FORMAT_PATTERN).format(this.fieldValue));
        this.pScr.hasValueUpdated = true;
    }

    @Override
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
        if (!loop)
        {
            this.fieldValue = Math.max(this.fieldValue, this.minValue);
            this.fieldValue = Math.min(this.fieldValue, this.maxValue);
        }
        else
        {
            float div = maxValue - minValue;
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
