package net.cacpixel.rtmmetro.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiTextFieldAdvancedFloat extends GuiTextFieldAdvanced
{
    public float fieldValue;
    public float step;

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

    @Override
    public void handleMouseInput()
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        if (this.isMouseInside())
        {
            try
            {
                if (i != 0)
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
                    this.fieldValue += (i > 0) ? step : -step;
                    if (-0.0000001f < this.fieldValue && this.fieldValue < 0.0000001f)
                    {
                        this.fieldValue = 0.0f;
                    }
                    this.setText(String.format("%.3f", this.fieldValue));
                    this.setScrValueUpdated();
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
        this.fieldValue = GUIHelper.getFieldValue(this, this.fieldValue);
    }

    @Override
    public void setScrValueUpdated()
    {
        super.setScrValueUpdated();
    }
}
