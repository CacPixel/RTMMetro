package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;

public class WidgetFactory
{

    public static <E extends Enum<E>> GuiOptionButton<E> addOptionButton(IWidgetHolder thiz, int x, int y, int w, int h, String prefix,
                                                                         E[] values, E initVal,
                                                                         IActionListener callback)
    {
        GuiOptionButton<E> button = new GuiOptionButton<>(GuiScreenAdvanced.getNextWidgetId(), x, y, w, h, prefix, values, initVal,
                thiz.getScreen(), callback);
        thiz.add(button);
        return button;
    }

    public static GuiUnicodeGlyphButtonAdvanced addUnicodeGlyphButton(IWidgetHolder thiz, int x, int y, int w, int h, String glyph,
                                                                      float glyphScale,
                                                                      IActionListener callback)
    {
        return addUnicodeGlyphButton(thiz, x, y, w, h, "", glyph, glyphScale, callback);
    }

    public static GuiUnicodeGlyphButtonAdvanced addUnicodeGlyphButton(IWidgetHolder thiz, int x, int y, int w, int h, String text,
                                                                      String glyph, float glyphScale,
                                                                      IActionListener callback)
    {
        GuiUnicodeGlyphButtonAdvanced button = new GuiUnicodeGlyphButtonAdvanced(GuiScreenAdvanced.getNextWidgetId(), x, y, w, h, text,
                glyph, glyphScale,
                thiz.getScreen(), callback);
        thiz.add(button);
        return button;
    }

    public static GuiCheckBoxAdvanced addCheckBox(IWidgetHolder thiz, int x, int y, int w, int h, String text, boolean isChecked,
                                                  IActionListener callback)
    {
        GuiCheckBoxAdvanced button = new GuiCheckBoxAdvanced(GuiScreenAdvanced.getNextWidgetId(), x, y, text, isChecked, thiz.getScreen(),
                callback);
        thiz.add(button);
        return button;
    }

    public static GuiButtonAdvanced addButton(IWidgetHolder thiz, int x, int y, int w, int h, String text,
                                              IActionListener callback)
    {
        GuiButtonAdvanced button = new GuiButtonAdvanced(GuiScreenAdvanced.getNextWidgetId(), x, y, w, h, text, thiz.getScreen(), callback);
        thiz.add(button);
        return button;
    }

    public static GuiTextFieldAdvanced addTextField(IWidgetHolder thiz, int xPos, int yPos, int w, int h, String text)
    {
        GuiTextFieldAdvanced field = new GuiTextFieldAdvanced(GuiScreenAdvanced.getNextWidgetId(), thiz.getScreen().mc.fontRenderer, xPos,
                yPos, w, h, thiz.getScreen());
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(text);
        thiz.add(field);
        return field;
    }

    public static GuiTextFieldAdvancedFloat addTextField(IWidgetHolder thiz, int xPos, int yPos, int w, int h, float value, float min,
                                                         float max, boolean loop)
    {
        GuiTextFieldAdvancedFloat field = new GuiTextFieldAdvancedFloat(GuiScreenAdvanced.getNextWidgetId(),
                thiz.getScreen().mc.fontRenderer, xPos, yPos, w, h, thiz.getScreen(),
                value).setMinMax(min, max, loop);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        thiz.add(field);
        return field;
    }

    public static GuiTextFieldAdvancedInt addTextField(IWidgetHolder thiz, int xPos, int yPos, int w, int h, int value, int min, int max,
                                                       boolean loop)
    {
        GuiTextFieldAdvancedInt field = new GuiTextFieldAdvancedInt(GuiScreenAdvanced.getNextWidgetId(), thiz.getScreen().mc.fontRenderer,
                xPos, yPos, w, h, thiz.getScreen(),
                value).setMinMax(min, max, loop);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        thiz.add(field);
        return field;
    }
}
