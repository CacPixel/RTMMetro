package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;
import java.util.function.IntSupplier;

public class GuiTextFieldAdvancedFloat extends GuiTextFieldAdvanced
{
    public float fieldValue;
    public float step = 0.100000000001F;
    public float minValue = Float.MIN_VALUE;
    public float maxValue = Float.MAX_VALUE;
    public boolean loop = false;
    public static final String FORMAT_PATTERN = "0.0##";

    public GuiTextFieldAdvancedFloat(GuiScreenAdvanced pScr, int id, IntSupplier xSupplier, IntSupplier ySupplier,
                                     IntSupplier widthSupplier, IntSupplier heightSupplier)
    {
        super(pScr, id, xSupplier, ySupplier, widthSupplier, heightSupplier);
    }

    public GuiTextFieldAdvancedFloat setInitialValue(float val)
    {
        this.fieldValue = val;
        return this;
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
        if (this.isMouseInside() && this.isFocused)
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
        if (this.isFocused() && this.isVisible() && this.isEnabled())
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

    public boolean isValueValid()
    {
        return !(this.fieldValue < this.minValue) && !(this.fieldValue > this.maxValue)
                && !Float.isNaN(this.fieldValue) && !Float.isInfinite(this.fieldValue);
    }

    @Override
    public void checkValueAndSetText()
    {
        this.checkValue();
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
