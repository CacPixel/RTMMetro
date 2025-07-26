package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.util.ITranslatable;

public class GuiOption<R>
{
    public String str;
    public R obj;

    public GuiOption(String str, R obj)
    {
        this.str = str;
        this.obj = obj;
    }

    public String getTranslationKey()
    {
        if (this.obj instanceof ITranslatable)
        {
            this.str = ((ITranslatable<?>) this.obj).getTranslateKey();
        }
        return this.str;
    }
}
