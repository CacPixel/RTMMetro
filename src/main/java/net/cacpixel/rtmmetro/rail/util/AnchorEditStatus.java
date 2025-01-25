package net.cacpixel.rtmmetro.rail.util;

public enum AnchorEditStatus
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
}
