package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.gui.GuiTextFieldCustom;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiUnicodeGlyphButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public abstract class GuiScreenAdvanced extends GuiScreenCustom
{
    public GuiScreen parentScreen;
    public boolean hasValueUpdated;
    private static int NEXT_FIELD_ID;
    private static int NEXT_BUTTON_ID;
    protected Map<Integer /* button id */, Consumer<? super GuiButton>> buttonCallbackMap;

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
        if (this.buttonCallbackMap != null)
            this.buttonCallbackMap.clear();
        this.buttonCallbackMap = new HashMap<>();
        NEXT_FIELD_ID = 0;
        NEXT_BUTTON_ID = 0;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        this.width = scaledresolution.getScaledWidth();
        this.height = scaledresolution.getScaledHeight();
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        this.buttonCallbackMap.clear();
        this.buttonCallbackMap = null;
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

    protected <T extends GuiButton> void setButtonCallback(T button, Consumer<? super GuiButton> callback)
    {
        buttonCallbackMap.put(button.id, callback);
    }

    protected <T extends GuiButton> T addButton(T buttonIn, Consumer<? super GuiButton> callback)
    {
        T button = super.addButton(buttonIn);
        this.setButtonCallback(button, callback);
        return button;
    }

    protected GuiButton addButton(int x, int y, int w, int h, String text,
                                  Consumer<? super GuiButton> callback)
    {
        GuiButton button = new GuiButtonExt(NEXT_BUTTON_ID++, x, y, w, h, text);
        this.buttonList.add(button);
        this.setButtonCallback(button, callback);
        return button;
    }

    protected GuiCheckBox addCheckBox(int x, int y, int w, int h, String text, boolean isChecked,
                                      Consumer<? super GuiButton> callback)
    {
        GuiCheckBox button = new GuiCheckBox(NEXT_BUTTON_ID++, x, y, text, isChecked);
        this.buttonList.add(button);
        this.setButtonCallback(button, callback);
        return button;
    }

    protected GuiUnicodeGlyphButton addUnicodeGlyphButton(int x, int y, int w, int h, String text, String glyph, float glyphScale,
                                                          Consumer<? super GuiButton> callback)
    {
        GuiUnicodeGlyphButton button = new GuiUnicodeGlyphButton(NEXT_BUTTON_ID++, x, y, w, h, text, glyph, glyphScale);
        this.buttonList.add(button);
        this.setButtonCallback(button, callback);
        return button;
    }

    protected GuiUnicodeGlyphButton addUnicodeGlyphButton(int x, int y, int w, int h, String glyph, float glyphScale,
                                                          Consumer<? super GuiButton> callback)
    {
        return this.addUnicodeGlyphButton(x, y, w, h, "", glyph, glyphScale, callback);
    }

    public void drawRightAlignedString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        this.drawString(fontRendererIn, text, x - fontRendererIn.getStringWidth(text), y, color);
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
            this.onPressingEnter();
            GuiTextFieldCustom field = this.getFocusedTextField();
            if (field != null) field.setFocused(false);
            field = this.getNextTextField(field, true);
            if (field != null) field.setFocused(true);
        }
        else if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE)
        {
            this.onPressingEsc();
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        for (GuiTextFieldCustom field : this.textFields)
        {
            if (field instanceof GuiTextFieldAdvanced && ((GuiTextFieldAdvanced) field).isMouseInside() &&
                    ((GuiTextFieldAdvanced) field).isEnabled() && field.getVisible())
            {
                ((GuiTextFieldAdvanced) field).handleMouseInput();
            }
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        super.handleKeyboardInput();
        for (GuiTextFieldCustom field : this.textFields)
        {
            if (field instanceof GuiTextFieldAdvanced && field.getVisible() && field.isFocused() &&
                    ((GuiTextFieldAdvanced) field).isEnabled())
            {
                ((GuiTextFieldAdvanced) field).handleKeyboardInput();
            }
        }
    }

    protected void onPressingEsc()
    {
    }

    protected void onPressingEnter()
    {
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        Consumer<? super GuiButton> consumer = this.buttonCallbackMap.get(button.id);
        if (consumer != null)
        {
            consumer.accept(button);
        }
        try
        {
            super.actionPerformed(button);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void displayPrevScreen()
    {
        if (parentScreen == null)
            this.mc.displayGuiScreen(null);
        else
            this.mc.displayGuiScreen(this.parentScreen);
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
