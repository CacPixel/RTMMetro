package net.cacpixel.rtmmetro.math;

import jp.ngt.ngtlib.math.LinePosPool;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.StraightLine;

public class StraightLineAdvanced implements ILineAdvanced
{
    public final double startX;
    public final double startY;
    public final double endX;
    public final double endY;
    private final double length;
    private final double slopeAngle;
    private final double slope;
    private final double intercept;

    public StraightLineAdvanced(double p1, double p2, double p3, double p4)
    {
        this.startX = p1;
        this.startY = p2;
        this.endX = p3;
        this.endY = p4;
        double dx = p3 - p1;
        double dy = p4 - p2;
        if (dx == 0.0)
        {
            this.slope = Double.NaN;
            this.intercept = p1;
        }
        else
        {
            this.slope = dy / dx;
            this.intercept = p2 - this.slope * p1;
        }

        this.length = Math.sqrt(dx * dx + dy * dy);
        this.slopeAngle = Math.atan2(dy, dx);
    }

    public ILineAdvanced[] split(int length, int order)
    {
        double[] splitPoint = this.getPoint(length, order);
        StraightLineAdvanced[] result = new StraightLineAdvanced[2];
        result[0] = new StraightLineAdvanced(this.startX, this.startY, splitPoint[0], splitPoint[1]);
        result[1] = new StraightLineAdvanced(splitPoint[0], splitPoint[1], this.endX, this.endY);
        return result;
    }

    public double[] getPoint(int par1, int par2)
    {
        int i0 = par2 < 0 ? 0 : (par2 > par1 ? par1 : par2);
        double d0 = (double) i0 / (double) par1;
        double x = this.startX + (this.endX - this.startX) * d0;
        double y = this.startY + (this.endY - this.startY) * d0;
        return LinePosPool.get(x, y);
    }

    public int getNearlestPoint(int par1, double y, double x)
    {
        double t;
        if (Double.isNaN(this.slope))
        {
            t = (y - this.startY) / (this.endY - this.startY);
            boolean var8 = false;
        }
        else
        {
            double a21 = 1.0 / (this.slope * this.slope + 1.0);
            double x0 = (x + this.slope * y - this.slope * this.intercept) * a21;
            t = (x0 - this.startX) / (this.endX - this.startX);
        }

        return NGTMath.floor(t * (double) par1);
    }

    public double getSlope(int par1, int par2)
    {
        return this.slopeAngle;
    }

    public double getLength()
    {
        return this.length;
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof StraightLine))
        {
            return false;
        }
        else
        {
            StraightLine line = (StraightLine) obj;
            return this.startX == line.startX && this.startY == line.startY && this.endX == line.endX &&
                    this.endY == line.endY;
        }
    }

    public int hashCode()
    {
        return NGTMath.floor(this.length);
    }

    public float getHomogenizedParameter(int n, int par2)
    {
        if (n < 4)
        {
            return 0.0F;
        }
        else if (par2 <= 0)
        {
            return 0.0F;
        }
        else if (par2 >= n)
        {
            return 1.0F;
        }
        else
        {
            return (float) par2 / (float) n;
        }
    }
}
