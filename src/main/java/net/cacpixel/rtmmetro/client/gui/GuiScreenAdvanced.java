package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.gui.GuiSlotCustom;
import jp.ngt.ngtlib.gui.GuiTextFieldCustom;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    protected GuiTextFieldAdvancedFloat setTextField(int xPos, int yPos, int w, int h, float value)
    {
        GuiTextFieldAdvancedFloat field = new GuiTextFieldAdvancedFloat(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos, w, h,
                this, value);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        this.textFields.add(field);
        return field;
    }
}
