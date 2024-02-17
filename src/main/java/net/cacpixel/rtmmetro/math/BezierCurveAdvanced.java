package net.cacpixel.rtmmetro.math;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.ILine;
import jp.ngt.ngtlib.math.LinePosPool;
import jp.ngt.ngtlib.math.NGTMath;
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.rail.util.construct.RailProcessThread;
import net.cacpixel.rtmmetro.rail.util.construct.TaskInitNP;
import net.cacpixel.rtmmetro.util.ModLog;

import java.util.ArrayList;
import java.util.List;

public final class BezierCurveAdvanced implements ILine {
    public static final int QUANTIZE = 32;
    public final double[] sp;
    public final double[] cpS;
    public final double[] cpE;
    public final double[] ep;
    private float[] normalizedParameters;
    private final double length;
    private final int split;
    private final List<TaskInitNP> accelerateTasks = new ArrayList<>();

    public BezierCurveAdvanced(double p1, double p2, double p3, double p4, double p5, double p6, double p7, double p8) {
        this.sp = new double[]{p1, p2};
        this.cpS = new double[]{p3, p4};
        this.cpE = new double[]{p5, p6};
        this.ep = new double[]{p7, p8};
        this.length = this.calcLength();
        this.split = (int) (this.length * 32.0D);
    }

    public double[] getPoint(int par1, int par2) {
        return this.getPointFromParameter((double) this.getHomogenizedParameter(par1, par2));
    }

    private double[] getPointFromParameter(double par1) {
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

    public int getNearlestPoint(int par1, double par2, double par3) {
        int i = 0;
        double d0 = Double.MAX_VALUE;

        for (int j = 0; j < par1; ++j) {
            double[] adouble = this.getPoint(par1, j);
            double d1 = par2 - adouble[1];
            double d2 = par3 - adouble[0];
            double d3 = d1 * d1 + d2 * d2;
            if (d3 < d0) {
                d0 = d3;
                i = j;
            }
        }

        return d0 < Double.MAX_VALUE ? i : -1;
    }

    public double getSlope(int par1, int par2) {
        return this.getSlopeFromParameter((double) this.getHomogenizedParameter(par1, par2));
    }

    private double getSlopeFromParameter(double par1) {
        double d0 = par1 < 0.0D ? 0.0D : (Math.min(par1, 1.0D));
        double d1 = 1.0D - d0;
        double d2 = d0 * d0;
        double d3 = 2.0D * d0 * d1;
        double d4 = d1 * d1;
        double d5 = 3.0D * (d2 * (this.ep[0] - this.cpE[0]) + d3 * (this.cpE[0] - this.cpS[0]) + d4 * (this.cpS[0] - this.sp[0]));
        double d6 = 3.0D * (d2 * (this.ep[1] - this.cpE[1]) + d3 * (this.cpE[1] - this.cpS[1]) + d4 * (this.cpS[1] - this.sp[1]));
        return Math.atan2(d6, d5);
    }

    private float getHomogenizedParameter(int n, int par2) {
        if (n < 4) {
            return 0.0F;
        } else if (par2 <= 0) {
            return 0.0F;
        } else if (par2 >= n) {
            return 1.0F;
        } else {
            if (this.normalizedParameters == null) {
                this.initNP();
            }
            if (this.normalizedParameters.length == 0) {
                return 0.0F;
            }

            int i = NGTMath.floor((float) par2 * (float) this.split / (float) n);
            return this.normalizedParameters[i];
        }
    }

    private void initNP() {
        this.normalizedParameters = new float[this.split];
        float f = 1.0F / (float) this.split;
        float[] afloat = new float[this.split + 1];
        float f1 = 0.0F;
        double[] adouble = this.sp;
        double[] adouble1 = new double[2];
        afloat[0] = 0.0F;

        for (int i = 1; i < this.split + 1; ++i) {
            f1 += f;
            adouble1 = this.getPointFromParameter((double) f1);
            afloat[i] = afloat[i - 1] + (float) this.getDistance(adouble[0], adouble1[0], adouble[1], adouble1[1]);
            adouble = adouble1;
        }

        for (int k = 1; k < this.split + 1; ++k) {
            afloat[k] /= afloat[this.split];
        }

        long start = System.currentTimeMillis();
        if (split > ModConfig.multiThreadBezierCurveInitThreshold) {
            RailProcessThread thread = RailProcessThread.getInstance();
            int step = 100;
            for (int index = 0; index < this.split; index += step) {
                TaskInitNP task = new TaskInitNP(this, afloat, index, (int) step);
                accelerateTasks.add(task);
                thread.addTask(task);
            }
            while (true) {
                boolean shouldQuit = true;
                for (int i = accelerateTasks.size() - 1; i >= 0; i--) {
                    TaskInitNP task = accelerateTasks.get(i);
                    if (!task.hasProcessed()) {
                        shouldQuit = false;
                        break;
                    }
                }
                if (shouldQuit) {
                    break;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ModLog.debug("initNP MultiThread took " + (System.currentTimeMillis() - start) + "ms");
        } else {
            for (int l = 0; l < this.split; ++l) {
                float f2 = (float) l / (float) this.split;
                int j = 0;
                for (j = 0; j < this.split - 1 && (!(afloat[j] <= f2) || !(f2 <= afloat[j + 1])); ++j) {
                }
                float f3 = (f2 - afloat[j]) / (afloat[j + 1] - afloat[j]);
                f3 = ((float) j * (1.0F - f3) + (float) (1 + j) * f3) * (1.0F / (float) this.split);
                this.normalizedParameters[l] = f3;
            }
            ModLog.debug("initNP took " + (System.currentTimeMillis() - start) + "ms");
        }
    }

    public void normalizeParams(float[] afloat, int index, int step) {
        for (int l = index; (l < this.split && l < index + step); ++l) {
            float f2 = (float) l / (float) this.split;
            int j = 0;
            for (j = 0; j < this.split - 1 && (!(afloat[j] <= f2) || !(f2 <= afloat[j + 1])); ++j) {
            }
            float f3 = (f2 - afloat[j]) / (afloat[j + 1] - afloat[j]);
            f3 = ((float) j * (1.0F - f3) + (float) (1 + j) * f3) * (1.0F / (float) this.split);
            this.normalizedParameters[l] = f3;
        }
    }

    public double getLength() {
        return this.length;
    }

    private double calcLength() {
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

        for (int j = 1; j < i + 1; ++j) {
            f1 += f;
            adouble1 = this.getPointFromParameter((double) f1);
            adouble2[j] = adouble2[j - 1] + this.getDistance(adouble[0], adouble1[0], adouble[1], adouble1[1]);
            adouble = adouble1;
        }

        return adouble2[i];
    }

    private double getDistance(double par1, double par2, double par3, double par4) {
        double d0 = Math.abs(par1 - par2);
        double d1 = Math.abs(par3 - par4);
        return Math.sqrt(d0 * d0 + d1 * d1);
    }
}

