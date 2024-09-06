package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiTextFieldCustom;

public class GUIHelper
{
    public static float getFieldValueFloat(GuiTextFieldCustom field, float defaultVal)
    {
        try
        {
            return Float.valueOf(field.getText());
        }
        catch (NumberFormatException e)
        {
            return defaultVal;
        }
    }

    public static int getFieldValueInt(GuiTextFieldCustom field, int defaultVal)
    {
        try
        {
            return Integer.valueOf(field.getText());
        }
        catch (NumberFormatException e)
        {
            return defaultVal;
        }
    }
}
