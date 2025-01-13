package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.gui.GuiTextFieldCustom;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ListIterator;

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
                                                     float min, float max, boolean loop)
    {
        GuiTextFieldAdvancedFloat field = new GuiTextFieldAdvancedFloat(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos,
                w, h, this, value).setMinMax(min, max, loop);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        this.textFields.add(field);
        return field;
    }

    protected GuiTextFieldAdvancedInt setTextField(int xPos, int yPos, int w, int h, int value,
                                                   int min, int max, boolean loop)
    {
        GuiTextFieldAdvancedInt field = new GuiTextFieldAdvancedInt(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos,
                w, h, this, value).setMinMax(min, max, loop);
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

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
        if (Keyboard.getEventKey() == Keyboard.KEY_RETURN || Keyboard.getEventKey() == Keyboard.KEY_DOWN)
        {
            GuiTextFieldCustom field = this.getFocusedTextField();
            if (field != null)
                field.setFocused(false);
            field = this.getNextTextField(field, true);
            if (field != null)
                field.setFocused(true);
        }
        else if (Keyboard.getEventKey() == Keyboard.KEY_UP)
        {
            GuiTextFieldCustom field = this.getFocusedTextField();
            if (field != null)
                field.setFocused(false);
            field = this.getPrevTextField(field, true);
            if (field != null)
                field.setFocused(true);
        }
    }

    public GuiTextFieldCustom getFocusedTextField()
    {
        if (this.textFields == null || this.textFields.isEmpty())
        {
            return null;
        }
        for (GuiTextFieldCustom field : this.textFields)
        {
            if (field.isFocused())
            {
                return field;
            }
        }
        return null;
    }

    public GuiTextFieldCustom getNextTextField(GuiTextFieldCustom fieldIn, boolean loop)
    {
        if (this.textFields == null || this.textFields.isEmpty())
        {
            return null;
        }
        ListIterator<GuiTextFieldCustom> it = this.textFields.listIterator();
        while (it.hasNext())
        {
            GuiTextFieldCustom field = it.next();
            if (field == fieldIn)
            {
                if (it.hasNext())
                    return it.next();
                else if (loop)
                    return this.textFields.get(0);
                else
                    return null;
            }
        }
        return null;
    }

    public GuiTextFieldCustom getPrevTextField(GuiTextFieldCustom fieldIn, boolean loop)
    {
        if (this.textFields == null || this.textFields.isEmpty())
        {
            return null;
        }
        ListIterator<GuiTextFieldCustom> it = this.textFields.listIterator(this.textFields.size());
        while (it.hasPrevious())
        {
            GuiTextFieldCustom field = it.previous();
            if (field == fieldIn)
            {
                if (it.hasPrevious())
                    return it.previous();
                else if (loop)
                    return this.textFields.get(this.textFields.size() - 1);
                else
                    return null;
            }
        }
        return null;
    }
}
