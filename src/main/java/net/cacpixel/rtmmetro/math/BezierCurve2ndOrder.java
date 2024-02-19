package net.cacpixel.rtmmetro.math;

import jp.ngt.ngtlib.math.ILine;
import jp.ngt.ngtlib.math.LinePosPool;

public class BezierCurve2ndOrder {
    public final double[] sp;
    public final double[] cp;
    public final double[] ep;

    public BezierCurve2ndOrder(double spX, double spY, double cpX, double cpY, double epX, double epY) {
        this(new double[]{spX, spY}, new double[]{cpX, cpY}, new double[]{epX, epY});
    }

    public BezierCurve2ndOrder(double[] sp, double[] cp, double[] ep) {
        this.sp = sp.clone();
        this.cp = cp.clone();
        this.ep = ep.clone();
    }

    public double[] getPointFromParameter(double par1) {
        double t = par1 < 0.0D ? 0.0D : (Math.min(par1, 1.0D));
        double tMinus1 = 1.0D - t;
        double epParam = t * t;
        double cpParam = 2.0D * t * tMinus1;
        double spParam = tMinus1 * tMinus1;
        double d6 = epParam * this.ep[0] + cpParam * this.cp[0] + spParam * this.sp[0];
        double d7 = epParam * this.ep[1] + cpParam * this.cp[1] + spParam * this.sp[1];
        return LinePosPool.get(d6, d7);
    }

}
