package net.cacpixel.rtmmetro.math;

import jp.ngt.ngtlib.math.ILine;

public interface ILineAdvanced extends ILine
{
    ILineAdvanced[] split(int length, int order);

    float getHomogenizedParameter(int n, int par2);

}
