package net.cacpixel.rtmmetro.math;

import jp.ngt.ngtlib.math.LinePosPool;
import jp.ngt.ngtlib.math.NGTMath;
import net.minecraft.util.math.MathHelper;

public final class BezierCurveAdvanced implements ILineAdvanced
{
    public static final int QUANTIZE = 32;
    public final double[] sp;
    public final double[] cpS;
    public final double[] cpE;
    public final double[] ep;
    private float[] normalizedParameters;
    private final double length;
    private final int split;

    public BezierCurveAdvanced(double spX, double spY, double cpSX, double cpSY, double cpEX, double cpEY, double epX, double epY)
    {
        this(new double[]{spX, spY}, new double[]{cpSX, cpSY}, new double[]{cpEX, cpEY}, new double[]{epX, epY});
    }

    public BezierCurveAdvanced(double[] sp, double[] cpS, double[] cpE, double[] ep)
    {
        this.sp = sp.clone();
        this.cpS = cpS.clone();
        this.cpE = cpE.clone();
        this.ep = ep.clone();
        this.length = this.calcLength();
        this.split = (int) (this.length * 32.0D);
    }

    @Override
    public ILineAdvanced[] split(int length, int order)
    {
        float t = this.getHomogenizedParameter(length, order);
        BezierCurveAdvanced[] result = new BezierCurveAdvanced[2];
        BezierCurve2ndOrder helperCurve1 = new BezierCurve2ndOrder(this.sp, this.cpS, this.cpE);
        BezierCurve2ndOrder helperCurve2 = new BezierCurve2ndOrder(this.cpS, this.cpE, this.ep);
        result[0] = new BezierCurveAdvanced(
                this.sp,
                new double[]{(this.cpS[0] - this.sp[0]) * t + this.sp[0], (this.cpS[1] - this.sp[1]) * t + this.sp[1]},
                helperCurve1.getPointFromParameter(t),
                this.getPointFromParameter(t));
        result[1] = new BezierCurveAdvanced(
                this.getPointFromParameter(t),
                helperCurve2.getPointFromParameter(t),
                new double[]{(this.ep[0] - this.cpE[0]) * t + this.cpE[0],
                        (this.ep[1] - this.cpE[1]) * t + this.cpE[1]},
                this.ep);
        return result;
    }

    public double[] getPoint(int length, int order)
    {
        return this.getPointFromParameter(this.getHomogenizedParameter(length, order));
    }

    public double[] getPointFromParameter(double par1)
    {
        double d0 = par1 < 0.0D ? 0.0D : (Math.min(par1, 1.0D));
        double d1 = 1.0D - d0;
        double d2 = d0 * d0 * d0;
        double d3 = 3.0D * d0 * d0 * d1;
        double d4 = 3.0D * d0 * d1 * d1;
        double d5 = d1 * d1 * d1;
        double d6 = d2 * this.ep[0] + d3 * this.cpE[0] + d4 * this.cpS[0] + d5 * this.sp[0];
        double d7 = d2 * this.ep[1] + d3 * this.cpE[1] + d4 * this.cpS[1] + d5 * this.sp[1];
        return LinePosPool.get(d6, d7);
    }

    public int getNearlestPoint(int length, double y, double x)
    {
        int ret = 0;
        double pd = Double.MAX_VALUE;
        for (int j = 0; j < length; ++j)
        {
            double[] point = this.getPoint(length, j);
            double dy = y - point[1];
            double dx = x - point[0];
            double distance = (dy * dy) + (dx * dx);
            if (distance < pd)
            {
                pd = distance;
                ret = j;
            }
        }
        return pd < Double.MAX_VALUE ? ret : -1;
    }

    /* warning: not accurate on multiple results */
    public int getNearestPointFromX(int length, double x)
    {
        int ret = 0;
        double pd = Double.MAX_VALUE;
        for (int j = 0; j < length; ++j)
        {
            double[] point = this.getPoint(length, j);
            double dx = x - point[0];
            double distance = Math.abs(dx);
            if (distance < pd)
            {
                pd = distance;
                ret = j;
            }
        }
        return pd < Double.MAX_VALUE ? ret : -1;
    }

    /* warning: not accurate on multiple results */
    public int getNearestPointFromY(int length, double y)
    {
        int ret = 0;
        double pd = Double.MAX_VALUE;
        for (int j = 0; j < length; ++j)
        {
            double[] point = this.getPoint(length, j);
            double dy = y - point[1];
            double distance = Math.abs(dy);
            if (distance < pd)
            {
                pd = distance;
                ret = j;
            }
        }
        return pd < Double.MAX_VALUE ? ret : -1;
    }

    /* warning: not accurate on multiple results */
    public double fromXGetY(int length, double x)
    {
        return this.getPoint(length, this.getNearestPointFromX(length, x))[1];
    }

    /* warning: not accurate on multiple results */
    public double fromYGetX(int length, double y)
    {
        return this.getPoint(length, this.getNearestPointFromY(length, y))[0];
    }

    public double getSlope(int par1, int par2)
    {
        return this.getSlopeFromParameter(this.getHomogenizedParameter(par1, par2));
    }

    public double getSlopeD(int par1, int par2)
    {
        return NGTMath.toDegrees(this.getSlope(par1, par2));
    }

    public double getWrappedSlopeD(int par1, int par2)
    {
        return MathHelper.wrapDegrees(this.getSlopeD(par1, par2));
    }


    private double getSlopeFromParameter(double par1)
    {
        double d0 = par1 < 1.0E-12 ? 1.0E-12 : (Math.min(par1, 0.999999999999D));
        double d1 = 1.0D - d0;
        double d2 = d0 * d0;
        double d3 = 2.0D * d0 * d1;
        double d4 = d1 * d1;
        double d5 = 3.0D *
                (d2 * (this.ep[0] - this.cpE[0]) + d3 * (this.cpE[0] - this.cpS[0]) + d4 * (this.cpS[0] - this.sp[0]));
        double d6 = 3.0D *
                (d2 * (this.ep[1] - this.cpE[1]) + d3 * (this.cpE[1] - this.cpS[1]) + d4 * (this.cpS[1] - this.sp[1]));
        return Math.atan2(d6, d5);
    }

    public float getHomogenizedParameter(int length, int order)
    {
        if (length < 4)
        {
            return 0.0F;
        }
        else if (order <= 0)
        {
            return 0.0F;
        }
        else if (order >= length)
        {
            return 1.0F;
        }
        else
        {
            if (this.normalizedParameters == null)
            {
                this.initNP();
            }
            if (this.normalizedParameters.length == 0)
            {
                return 0.0F;
            }

            int i = NGTMath.floor((float) order * (float) this.split / (float) length);
            return this.normalizedParameters[i];
        }
    }

    public void initNP()
    {
        this.normalizedParameters = new float[this.split];
        float f = 1.0F / (float) this.split;
        float[] dd = new float[this.split + 1];
        float f1 = 0.0F;
        double[] adouble = this.sp;
        double[] adouble1 = new double[2];
        dd[0] = 0.0F;

        for (int i = 1; i < this.split + 1; ++i)
        {
            f1 += f;
            adouble1 = this.getPointFromParameter(f1);
            dd[i] = dd[i - 1] + (float) this.getDistance(adouble[0], adouble1[0], adouble[1], adouble1[1]);
            adouble = adouble1;
        }

        for (int k = 1; k < this.split + 1; ++k)
        {
            dd[k] /= dd[this.split];
        }

        this.normalizeParams(dd, 0, this.split);

    }

    public void normalizeParams(float[] dd, int index, int step)
    {
        for (int i = index; (i < this.split && i < index + step); ++i)
        {
            float t = (float) i / (float) this.split;
            int k = 0;
            int searchMin = 0, searchMax = split - 1;
            int loopTimes = 0;
            while (!(dd[k] <= t && t <= dd[k + 1]))
            {
                k = (searchMax - searchMin) / 2 + searchMin;
                // k in [0~half]
                if (dd[searchMin] <= t && t <= dd[k + 1])
                {
                    searchMax = k;
                }
                else /* dd[k] <= t && t <= dd[searchMax] */
                {
                    searchMin = k;
                }

                if (++loopTimes >= split - 1)
                    break;
            }

            float x = (t - dd[k]) / (dd[k + 1] - dd[k]);
            x = ((float) k * (1.0F - x) + (float) (1 + k) * x) * (1.0F / (float) this.split);
            this.normalizedParameters[i] = x;
        }
    }

    public double getLength()
    {
        return this.length;
    }

    private double calcLength()
    {
        double d0 = this.sp[0] - this.ep[0];
        double d1 = this.sp[1] - this.ep[1];
        double d2 = Math.sqrt(d0 * d0 + d1 * d1);
        int i = NGTMath.floor(d2 * 2.0D);
        float f = 1.0F / (float) i;
        float f1 = 0.0F;
        double[] adouble = this.sp;
        double[] adouble1 = new double[2];
        double[] adouble2 = new double[i + 1];
        adouble2[0] = 0.0D;

        for (int j = 1; j < i + 1; ++j)
        {
            f1 += f;
            adouble1 = this.getPointFromParameter(f1);
            adouble2[j] = adouble2[j - 1] + this.getDistance(adouble[0], adouble1[0], adouble[1], adouble1[1]);
            adouble = adouble1;
        }

        return adouble2[i];
    }

    private double getDistance(double par1, double par2, double par3, double par4)
    {
        double d0 = Math.abs(par1 - par2);
        double d1 = Math.abs(par3 - par4);
        return Math.sqrt(d0 * d0 + d1 * d1);
    }
}

