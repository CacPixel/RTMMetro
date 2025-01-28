package net.cacpixel.rtmmetro.util;

public class EnumUtils
{
    private static final String TRANSLATE_KEY_PREFIX = "enum";

    public static String getTranslateKey(String prefix, Enum<?> thiz)
    {
        return prefix + "." + thiz.getClass().getSimpleName() + "." + thiz.name();
    }

    public static String getTranslateKey(Enum<?> thiz)
    {
        return getTranslateKey(TRANSLATE_KEY_PREFIX, thiz);
    }
}
