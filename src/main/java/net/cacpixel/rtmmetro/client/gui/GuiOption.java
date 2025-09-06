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
            return ((ITranslatable<?>) this.obj).getTranslateKey();
        }
        else
        {
            return this.str;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof GuiOption))
            return false;
        else
            return this.obj.equals(((GuiOption<?>) obj).obj);
    }
}
