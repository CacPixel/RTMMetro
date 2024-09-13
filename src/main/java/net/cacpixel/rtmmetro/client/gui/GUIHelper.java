package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiTextFieldCustom;
import net.cacpixel.rtmmetro.util.ModLog;

public class GUIHelper
{
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(GuiTextFieldCustom field, T defaultVal)
    {
        try
        {
            if (defaultVal instanceof Byte)
            {
                return (T) Byte.valueOf(field.getText());
            }
            else if (defaultVal instanceof Integer)
            {
                return (T) Integer.valueOf(field.getText());
            }
            else if (defaultVal instanceof Float)
            {
                return (T) Float.valueOf(field.getText());
            }
            else if (defaultVal instanceof Double)
            {
                return (T) Double.valueOf(field.getText());
            }
            else
            {
                ModLog.debug("GUIHelper.getFieldValue : Type not supported : %s", defaultVal.getClass().toString());
                return defaultVal;
            }
        }
        catch (NumberFormatException e)
        {
            return defaultVal;
        }
    }
}
