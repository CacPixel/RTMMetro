package net.cacpixel.rtmmetro.rail.util;

import net.cacpixel.rtmmetro.util.EnumUtils;
import net.cacpixel.rtmmetro.util.ITranslatable;

public enum AnchorEditStatus implements ITranslatable<AnchorEditStatus>
{
    FOLLOW_NEIGHBOR,
    LENGTH_FIXED,
    ANGLE_FIXED,
    FREE_MODE;

    public static final int DEFAULT_ORDINAL = FOLLOW_NEIGHBOR.ordinal();

    public static AnchorEditStatus get(int i)
    {
        return values()[i >= values().length ? DEFAULT_ORDINAL : i];
    }

    @Override
    public String getTranslateKey()
    {
        return EnumUtils.getTranslateKey(this);
    }
}
