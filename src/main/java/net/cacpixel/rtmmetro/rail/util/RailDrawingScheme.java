package net.cacpixel.rtmmetro.rail.util;

import net.cacpixel.rtmmetro.util.EnumUtils;
import net.cacpixel.rtmmetro.util.ITranslatable;

public enum RailDrawingScheme implements ITranslatable<RailDrawingScheme>
{
    RTM_DEFAULT,
    DRAW_CIRCLE,
    SWITCH_RAIL_OPTIMIZED;
    public static final int DEFAULT_ORDINAL = RTM_DEFAULT.ordinal();

    public static RailDrawingScheme get(int i)
    {
        return values()[i >= values().length ? DEFAULT_ORDINAL : i];
    }

    @Override
    public String getTranslateKey()
    {
        return EnumUtils.getTranslateKey(this);
    }
}
