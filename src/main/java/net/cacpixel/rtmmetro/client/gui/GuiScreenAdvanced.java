package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.gui.GuiTextFieldCustom;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public abstract class GuiScreenAdvanced extends GuiScreenCustom
{
    public boolean hasValueUpdated;
    private static int NEXT_FIELD_ID;

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.textFields.clear();
        NEXT_FIELD_ID = 0;
    }

    @Override
    protected GuiTextFieldAdvanced setTextField(int xPos, int yPos, int w, int h, String text)
    {
        GuiTextFieldAdvanced field = new GuiTextFieldAdvanced(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos, w, h,
                this);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(text);
        this.textFields.add(field);
        return field;
    }

    protected GuiTextFieldAdvancedFloat setTextField(int xPos, int yPos, int w, int h, float value,
                                                     float min, float max)
    {
        GuiTextFieldAdvancedFloat field = new GuiTextFieldAdvancedFloat(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos,
                w, h, this, value).setMinMax(min, max);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        this.textFields.add(field);
        return field;
    }

    protected GuiTextFieldAdvancedInt setTextField(int xPos, int yPos, int w, int h, int value,
                                                   int min, int max)
    {
        GuiTextFieldAdvancedInt field = new GuiTextFieldAdvancedInt(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos,
                w, h, this, value).setMinMax(min, max);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        this.textFields.add(field);
        return field;
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException
    {
        super.mouseClicked(x, y, button);
        for (GuiTextFieldCustom textField : this.textFields)
        {
            textField.mouseClicked(x, y, button);
            if (textField.isFocused())
            {
                this.currentTextField = textField;
                this.onTextFieldClicked(textField);
            }
        }
    }
}
