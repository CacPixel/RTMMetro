package net.cacpixel.rtmmetro.client.gui.widgets;

import java.util.function.IntSupplier;

public class WidgetFactory
{

    public static <E extends Enum<E>> GuiOptionButton<E> addOptionButton(IWidgetHolder thiz, int x, int y, int w, int h,
                                                                         String prefix, E[] values, E initVal)
    {
        GuiOptionButton<E> button = new GuiOptionButton<>(thiz, thiz.getScreen().getNextWidgetId(), () -> x, () -> y,
                () -> w, () -> h, prefix, values, initVal);
        thiz.add(button);
        return button;
    }

    public static GuiUnicodeGlyphButtonAdvanced addUnicodeGlyphButton(IWidgetHolder thiz, int x, int y, int w, int h,
                                                                      String glyph, float glyphScale)
    {
        return addUnicodeGlyphButton(thiz, x, y, w, h, "", glyph, glyphScale);
    }

    public static GuiUnicodeGlyphButtonAdvanced addUnicodeGlyphButton(IWidgetHolder thiz, int x, int y, int w, int h,
                                                                      String text, String glyph, float glyphScale)
    {
        GuiUnicodeGlyphButtonAdvanced button = new GuiUnicodeGlyphButtonAdvanced(thiz,
                thiz.getScreen().getNextWidgetId(), () -> x, () -> y, () -> w, () -> h, text, glyph, glyphScale);
        thiz.add(button);
        return button;
    }

    public static GuiCheckBoxAdvanced addCheckBox(IWidgetHolder thiz, int x, int y, String text, boolean isChecked)
    {
        GuiCheckBoxAdvanced button = new GuiCheckBoxAdvanced(thiz, thiz.getScreen().getNextWidgetId(), () -> x, () -> y,
                text, isChecked);
        thiz.add(button);
        return button;
    }

    public static GuiButtonAdvanced addButton(IWidgetHolder thiz, int x, int y, int w, int h, String text)
    {
        GuiButtonAdvanced button = new GuiButtonAdvanced(thiz, thiz.getScreen().getNextWidgetId(), () -> x, () -> y,
                () -> w, () -> h).setDisplayString(text);
        thiz.add(button);
        return button;
    }

    public static GuiTextFieldAdvanced addTextField(IWidgetHolder thiz, int xPos, int yPos, int w, int h, String text)
    {
        GuiTextFieldAdvanced field = new GuiTextFieldAdvanced(thiz, thiz.getScreen().getNextWidgetId(), () -> xPos,
                () -> yPos, () -> w, () -> h);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(text);
        thiz.add(field);
        return field;
    }

    public static GuiTextFieldAdvancedNumber addTextField(IWidgetHolder thiz, int xPos, int yPos, int w, int h,
                                                          float value, float min, float max, boolean loop)
    {
        GuiTextFieldAdvancedNumber field = new GuiTextFieldAdvancedNumber(thiz, thiz.getScreen().getNextWidgetId(),
                () -> xPos, () -> yPos, () -> w, () -> h).setFloatValue(value).setMinMax(min, max, loop);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        thiz.add(field);
        return field;
    }

    public static GuiTextFieldAdvancedNumber addTextField(IWidgetHolder thiz, int xPos, int yPos, int w, int h, int value,
                                                       int min, int max, boolean loop)
    {
        GuiTextFieldAdvancedNumber field = new GuiTextFieldAdvancedNumber(thiz, thiz.getScreen().getNextWidgetId(),
                () -> xPos, () -> yPos, () -> w, () -> h).setIntValue(value).setMinMax(min, max, loop)
                .setFormatPattern(GuiTextFieldAdvancedNumber.INT_PATTERN).setStep(1.0);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        thiz.add(field);
        return field;
    }

    /* ********************* With Supplier ************************ */

    public static <E extends Enum<E>> GuiOptionButton<E> addOptionButton(IWidgetHolder thiz, IntSupplier xSupplier,
                                                                         IntSupplier ySupplier,
                                                                         IntSupplier widthSupplier,
                                                                         IntSupplier heightSupplier, String prefix,
                                                                         E[] values, E initVal)
    {
        GuiOptionButton<E> button = new GuiOptionButton<>(thiz, thiz.getScreen().getNextWidgetId(), xSupplier,
                ySupplier, widthSupplier, heightSupplier, prefix, values, initVal);
        thiz.add(button);
        return button;
    }

    public static GuiUnicodeGlyphButtonAdvanced addUnicodeGlyphButton(IWidgetHolder thiz, IntSupplier xSupplier,
                                                                      IntSupplier ySupplier, IntSupplier widthSupplier,
                                                                      IntSupplier heightSupplier, String glyph,
                                                                      float glyphScale)
    {
        return addUnicodeGlyphButton(thiz, xSupplier, ySupplier, widthSupplier, heightSupplier, "", glyph, glyphScale);
    }

    public static GuiUnicodeGlyphButtonAdvanced addUnicodeGlyphButton(IWidgetHolder thiz, IntSupplier xSupplier,
                                                                      IntSupplier ySupplier, IntSupplier widthSupplier,
                                                                      IntSupplier heightSupplier, String text,
                                                                      String glyph, float glyphScale)
    {
        GuiUnicodeGlyphButtonAdvanced button = new GuiUnicodeGlyphButtonAdvanced(thiz,
                thiz.getScreen().getNextWidgetId(), xSupplier, ySupplier, widthSupplier, heightSupplier, text, glyph,
                glyphScale);
        thiz.add(button);
        return button;
    }

    public static GuiCheckBoxAdvanced addCheckBox(IWidgetHolder thiz, IntSupplier xSupplier, IntSupplier ySupplier,
                                                  String text, boolean isChecked)
    {
        GuiCheckBoxAdvanced button = new GuiCheckBoxAdvanced(thiz, thiz.getScreen().getNextWidgetId(), xSupplier,
                ySupplier, text, isChecked);
        thiz.add(button);
        return button;
    }

    public static GuiButtonAdvanced addButton(IWidgetHolder thiz, IntSupplier xSupplier, IntSupplier ySupplier,
                                              IntSupplier widthSupplier, IntSupplier heightSupplier, String text)
    {
        GuiButtonAdvanced button = new GuiButtonAdvanced(thiz, thiz.getScreen().getNextWidgetId(), xSupplier, ySupplier,
                widthSupplier, heightSupplier).setDisplayString(text);
        thiz.add(button);
        return button;
    }

    public static GuiTextFieldAdvanced addTextField(IWidgetHolder thiz, IntSupplier xSupplier, IntSupplier ySupplier,
                                                    IntSupplier widthSupplier, IntSupplier heightSupplier, String text)
    {
        GuiTextFieldAdvanced field = new GuiTextFieldAdvanced(thiz, thiz.getScreen().getNextWidgetId(), xSupplier,
                ySupplier, widthSupplier, heightSupplier);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(text);
        thiz.add(field);
        return field;
    }

    public static GuiTextFieldAdvancedNumber addTextField(IWidgetHolder thiz, IntSupplier xSupplier,
                                                          IntSupplier ySupplier, IntSupplier widthSupplier,
                                                          IntSupplier heightSupplier, float value, float min, float max,
                                                          boolean loop)
    {
        GuiTextFieldAdvancedNumber field = new GuiTextFieldAdvancedNumber(thiz, thiz.getScreen().getNextWidgetId(),
                xSupplier, ySupplier, widthSupplier, heightSupplier).setFloatValue(value).setMinMax(min, max, loop);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        thiz.add(field);
        return field;
    }

    public static GuiTextFieldAdvancedNumber addTextField(IWidgetHolder thiz, IntSupplier xSupplier, IntSupplier ySupplier,
                                                       IntSupplier widthSupplier, IntSupplier heightSupplier, int value,
                                                       int min, int max, boolean loop)
    {
        GuiTextFieldAdvancedNumber field = new GuiTextFieldAdvancedNumber(thiz, thiz.getScreen().getNextWidgetId(), xSupplier,
                ySupplier, widthSupplier, heightSupplier).setIntValue(value).setMinMax(min, max, loop)
                .setFormatPattern(GuiTextFieldAdvancedNumber.INT_PATTERN).setStep(1.0);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        thiz.add(field);
        return field;
    }
}
