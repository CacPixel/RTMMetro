package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiTextFieldCustom;

public class GUIHelper
{
    public static <T> T getFieldValue(GuiTextFieldCustom field, T defaultVal)
    {
        try
        {
            if (defaultVal instanceof Integer)
            {
                return (T)Integer.valueOf(field.getText());
            }
            else if (defaultVal instanceof Float)
            {
                return (T)Float.valueOf(field.getText());
            }
            else if (defaultVal instanceof Double)
            {
                return (T)Double.valueOf(field.getText());
            }
            else
            {
                return defaultVal;
            }
        }
        catch (NumberFormatException e)
        {
            return defaultVal;
        }
    }
}
