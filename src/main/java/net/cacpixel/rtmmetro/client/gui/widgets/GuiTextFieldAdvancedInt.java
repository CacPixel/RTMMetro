package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.util.function.IntSupplier;

public class GuiTextFieldAdvancedInt extends GuiTextFieldAdvanced
{
    public int fieldValue;
    public int step = 1;
    public int minValue = Integer.MIN_VALUE;
    public int maxValue = Integer.MAX_VALUE;
    public boolean loop = false;

    public GuiTextFieldAdvancedInt(IWidgetHolder holder, int id, IntSupplier xSupplier, IntSupplier ySupplier,
                                   IntSupplier widthSupplier, IntSupplier heightSupplier)
    {
        super(holder, id, xSupplier, ySupplier, widthSupplier, heightSupplier);
    }

    public GuiTextFieldAdvancedInt setInitialValue(int val)
    {
        this.fieldValue = val;
        return this;
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

    public boolean isValueValid()
    {
        return this.fieldValue >= this.minValue && this.fieldValue <= this.maxValue;
    }

    @Override
    public void checkValueAndSetText()
    {
        this.checkValue();
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
