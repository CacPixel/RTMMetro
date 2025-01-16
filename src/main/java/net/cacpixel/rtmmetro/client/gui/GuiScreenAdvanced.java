package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.gui.GuiTextFieldCustom;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiUnicodeGlyphButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ListIterator;

@SideOnly(Side.CLIENT)
public abstract class GuiScreenAdvanced extends GuiScreenCustom
{
    public GuiScreen pLastScreen;
    public boolean hasValueUpdated;
    private static int NEXT_FIELD_ID;
    private static int NEXT_BUTTON_ID;

    public GuiScreenAdvanced()
    {
        super();
    }

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.textFields.clear();
        this.buttonList.clear();
        NEXT_FIELD_ID = 0;
        NEXT_BUTTON_ID = 0;
    }

    @Override
    protected GuiTextFieldAdvanced setTextField(int xPos, int yPos, int w, int h, String text)
    {
        GuiTextFieldAdvanced field = new GuiTextFieldAdvanced(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos, w, h, this);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(text);
        this.textFields.add(field);
        return field;
    }

    protected GuiTextFieldAdvancedFloat setTextField(int xPos, int yPos, int w, int h, float value, float min, float max, boolean loop)
    {
        GuiTextFieldAdvancedFloat field = new GuiTextFieldAdvancedFloat(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos, w, h, this,
                value).setMinMax(min, max, loop);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        this.textFields.add(field);
        return field;
    }

    protected GuiTextFieldAdvancedInt setTextField(int xPos, int yPos, int w, int h, int value, int min, int max, boolean loop)
    {
        GuiTextFieldAdvancedInt field = new GuiTextFieldAdvancedInt(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos, w, h, this,
                value).setMinMax(min, max, loop);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        this.textFields.add(field);
        return field;
    }

    protected GuiButton addButton(int x, int y, int w, int h, String text)
    {
        GuiButton button = new GuiButton(NEXT_BUTTON_ID++, x, y, w, h, text);
        this.buttonList.add(button);
        return button;
    }

    protected GuiCheckBox addCheckBox(int x, int y, int w, int h, String text, boolean isChecked)
    {
        GuiCheckBox button = new GuiCheckBox(NEXT_BUTTON_ID++, x, y, text, isChecked);
        this.buttonList.add(button);
        return button;
    }

    protected GuiUnicodeGlyphButton addUnicodeGlyphButton(int x, int y, int w, int h, String text, String glyph, float glyphScale)
    {
        GuiUnicodeGlyphButton button = new GuiUnicodeGlyphButton(NEXT_BUTTON_ID++, x, y, w, h, text, glyph, glyphScale);
        this.buttonList.add(button);
        return button;
    }

    protected GuiUnicodeGlyphButton addUnicodeGlyphButton(int x, int y, int w, int h, String glyph, float glyphScale)
    {
        return this.addUnicodeGlyphButton(x, y, w, h, "", glyph, glyphScale);
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
        if (Keyboard.getEventKey() == Keyboard.KEY_RETURN)
        {
            GuiTextFieldCustom field = this.getFocusedTextField();
            if (field != null) field.setFocused(false);
            field = this.getNextTextField(field, true);
            if (field != null) field.setFocused(true);
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
                if (it.hasNext()) return it.next();
                else if (loop) return this.textFields.get(0);
                else return null;
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
        ListIterator<GuiTextFieldCustom> it = this.textFields.listIterator();
        while (it.hasPrevious())
        {
            GuiTextFieldCustom field = it.previous();
            if (field == fieldIn)
            {
                if (it.hasPrevious()) return it.previous();
                else if (loop) return this.textFields.get(this.textFields.size() - 1);
                else return null;
            }
        }
        return null;
    }

    public int getNextFieldIdAndIncrease()
    {
        return NEXT_FIELD_ID++;
    }

    public int getNextButtonIdAndIncrease()
    {
        return NEXT_BUTTON_ID++;
    }
}
