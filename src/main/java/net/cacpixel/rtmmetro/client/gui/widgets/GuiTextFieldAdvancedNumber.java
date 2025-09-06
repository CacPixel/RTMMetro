package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.MouseGrabber;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;
import java.util.function.IntSupplier;

public class GuiTextFieldAdvancedNumber extends GuiTextFieldAdvanced
{
    private double fieldValue;
    private double step = 0.100000000001;
    private double minValue = Double.MIN_VALUE;
    private double maxValue = Double.MAX_VALUE;
    private boolean loop = false;
    private String formatPattern = FLOAT_PATTERN;
    public static final String INT_PATTERN = "#";
    public static final String FLOAT_PATTERN = "0.0##";

    public GuiTextFieldAdvancedNumber(IWidgetHolder holder, IntSupplier xSupplier, IntSupplier ySupplier,
                                      IntSupplier widthSupplier, IntSupplier heightSupplier)
    {
        super(holder, xSupplier, ySupplier, widthSupplier, heightSupplier);
        canDragEdit = true;
    }

    public GuiTextFieldAdvancedNumber setMinMax(double min, double max, boolean loop)
    {
        this.minValue = min;
        this.maxValue = max;
        this.loop = loop;
        return this;
    }

    public GuiTextFieldAdvancedNumber setStep(double step)
    {
        this.step = step;
        return this;
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY)
    {
        super.onLeftClick(mouseX, mouseY);
    }

    @Override
    public void onLeftClickAndDrag(int mouseX, int mouseY, long timeSinceLastClick)
    {
        super.onLeftClickAndDrag(mouseX, mouseY, timeSinceLastClick);
        int dx = getHolder().shiftMouseX();
        int dy = getHolder().shiftMouseY();
        boolean flag1 = getScreen().isMousePassThrough() &&
                CacGuiUtils.isMouseInside(x + dx, y + dy, width, height, getLastClickedX(), getLastClickedY());
        boolean flag2 = getHolder().isMouseInside() && getHolder().isLastClickInside();
        boolean flag3 = MouseGrabber.INSTANCE.isGrabbed();
        if (isEnabled() && isVisible() && !isFocused() && flag1 && (flag2 || flag3))
        {
            if (isDragging() && !MouseGrabber.INSTANCE.isGrabbed())
            {
                MouseGrabber.INSTANCE.grabMouseCursor();
            }
            if (MouseGrabber.INSTANCE.isGrabbed())
            {
                int step = 3;
                int[] add = MouseGrabber.INSTANCE.step(step);
                if (add[0] != 0 || add[1] != 0)
                {
                    this.incValue((add[0] / step + add[1] / step) * CacGuiUtils.DEFAULT_SCROLL_VALUE);
                }
            }
        }
    }

    @Override
    public void onRelease(int mouseX, int mouseY, int state)
    {
        super.onRelease(mouseX, mouseY, state);
        if (MouseGrabber.INSTANCE.isGrabbed())
        {
            MouseGrabber.INSTANCE.ungrabMouseCursor();
        }
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
                this.fieldValue = this.getFieldValue(this.fieldValue, false);
                this.checkValueAndSetText();
                this.setValueUpdated(true);
            }
            else if (code == Keyboard.KEY_DOWN)
            {
                this.incValue(-CacGuiUtils.DEFAULT_SCROLL_VALUE);
                this.fieldValue = this.getFieldValue(this.fieldValue, false);
                this.checkValueAndSetText();
                this.setValueUpdated(true);
            }
            else if (GuiScreen.isCtrlKeyDown() && code == Keyboard.KEY_RETURN)
            {
                this.fieldValue = this.getFieldValue(this.fieldValue);
                this.checkValueAndSetText();
                if (this.isValueValid())
                {
                    this.setValueUpdated(true);
                }
                else
                {
                    this.checkValue();
                }
            }
            else
            {
                boolean doScript = code != Keyboard.KEY_LSHIFT && code != Keyboard.KEY_LMENU;
                this.fieldValue = this.getFieldValue(this.fieldValue, doScript);
                if (this.isValueValid())
                {
                    this.setValueUpdated(true);
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
        double step = this.step;
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
                && !Double.isNaN(this.fieldValue) && !Double.isInfinite(this.fieldValue);
    }

    @Override
    public void checkValueAndSetText()
    {
        this.checkValue();
        this.setText(new DecimalFormat(formatPattern).format(this.fieldValue));
        this.setValueUpdated(true);
    }

    @Override
    public void checkValue()
    {
        if (Double.isNaN(fieldValue) || Double.isInfinite(fieldValue))
        {
            this.fieldValue = 0.0;
        }
        if (-1e-6 < this.fieldValue && this.fieldValue < 1e-6)
        {
            this.fieldValue = 0.0;
        }
        if (!loop)
        {
            this.fieldValue = Math.max(this.fieldValue, this.minValue);
            this.fieldValue = Math.min(this.fieldValue, this.maxValue);
        }
        else
        {
            double div = maxValue - minValue;
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

    public float getFloatValue()
    {
        return (float) fieldValue;
    }

    public GuiTextFieldAdvancedNumber setFloatValue(float fieldValue)
    {
        this.fieldValue = fieldValue;
        return this;
    }

    public double getDoubleValue()
    {
        return fieldValue;
    }

    public GuiTextFieldAdvancedNumber setDoubleValue(double fieldValue)
    {
        this.fieldValue = fieldValue;
        return this;
    }

    public int getIntValue()
    {
        return (int) fieldValue;
    }

    public GuiTextFieldAdvancedNumber setIntValue(int fieldValue)
    {
        this.fieldValue = fieldValue;
        return this;
    }

    public String getFormatPattern()
    {
        return formatPattern;
    }

    public GuiTextFieldAdvancedNumber setFormatPattern(String formatPattern)
    {
        this.formatPattern = formatPattern;
        return this;
    }
}
