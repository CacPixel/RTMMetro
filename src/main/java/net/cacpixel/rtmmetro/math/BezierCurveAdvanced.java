package net.cacpixel.rtmmetro.math;

import jp.ngt.ngtlib.math.ILine;
import jp.ngt.ngtlib.math.LinePosPool;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.StraightLine;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.rail.util.RailMapAdvanced;
import net.cacpixel.rtmmetro.rail.util.construct.RailProcessThread;
import net.cacpixel.rtmmetro.rail.util.construct.TaskInitNP;
import net.cacpixel.rtmmetro.util.ModLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BezierCurveAdvanced implements ILineAdvanced {
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
        this(new double[]{p1, p2}, new double[]{p3, p4}, new double[]{p5, p6}, new double[]{p7, p8});
    }

    public BezierCurveAdvanced(double[] sp, double[] cpS, double[] cpE, double[] ep) {
        this.sp = sp.clone();
        this.cpS = cpS.clone();
        this.cpE = cpE.clone();
        this.ep = ep.clone();
        this.length = this.calcLength();
        this.split = (int) (this.length * 32.0D);
    }

    public double[] getPoint(int par1, int par2) {
        return this.getPointFromParameter((double) this.getHomogenizedParameter(par1, par2));
    }

    public static ILine[] splitCurve(ILine line, int length, int order) {
        if (line instanceof BezierCurveAdvanced) {
            BezierCurveAdvanced curve = (BezierCurveAdvanced) line;
            float t = curve.getHomogenizedParameter(length, order);
            BezierCurveAdvanced[] result = new BezierCurveAdvanced[2];
            BezierCurve2ndOrder helperCurve1 = new BezierCurve2ndOrder(curve.sp, curve.cpS, curve.cpE);
            BezierCurve2ndOrder helperCurve2 = new BezierCurve2ndOrder(curve.cpS, curve.cpE, curve.ep);
            result[0] = new BezierCurveAdvanced(
                    curve.sp,
                    new double[]{(curve.cpS[0] - curve.sp[0]) * t, (curve.cpS[1] - curve.sp[1]) * t},
                    helperCurve1.getPointFromParameter(t),
                    curve.getPointFromParameter(t));
            result[1] = new BezierCurveAdvanced(
                    curve.getPointFromParameter(t),
                    helperCurve2.getPointFromParameter(t),
                    new double[]{(curve.ep[0] - curve.cpE[0]) * t, (curve.ep[1] - curve.cpE[1]) * t},
                    curve.ep);
            return result;
        } else if (line instanceof StraightLine) {
            StraightLine sLine = (StraightLine) line;
            double[] splitPoint = sLine.getPoint(length, order);
            StraightLine[] result = new StraightLine[2];
            result[0] = new StraightLine(sLine.startX, sLine.startY, splitPoint[0], splitPoint[1]);
            result[1] = new StraightLine(splitPoint[0], splitPoint[1], sLine.endX, sLine.endY);
            return result;
        } else {
            return null;
        }
    }

    public static RailPosition[] getSplitCurveRP(RailMapAdvanced rm, int length, int order) {
        return getSplitCurveRP(rm.getLineHorizontal(), rm.getLineVertical(), rm.getStartRP(), rm.getEndRP(), length, order);
    }

    /**
     * 通过给定的单条轨道的两条曲线，求得切断后两条轨道的两对RailPosition
     *
     * @param lineHorizontal 水平方向曲线
     * @param lineVertical   垂直方向曲线
     * @param startRP        起点RP
     * @param endRP          终点RP
     * @param length         铁轨总长
     * @param order          要切断的位置
     * @return RailPosition[0~1] 前一段轨道的RP， [2~3] 后一段轨道的RP
     */
    public static RailPosition[] getSplitCurveRP(ILine lineHorizontal, ILine lineVertical,
                                                 RailPosition startRP, RailPosition endRP, int length, int order) {
        length *= QUANTIZE;
        order *= QUANTIZE;
        boolean isCornerOnly = lineHorizontal instanceof BezierCurveAdvanced;
        RailPosition[] result = new RailPosition[4];
        result[0] = cloneRP(startRP);
        result[0].anchorLengthHorizontal *= ((float) order / (float) length);
        result[0].anchorLengthVertical *= ((float) order / (float) length);
        result[3] = cloneRP(endRP);
        result[3].anchorLengthHorizontal *= ((float) (length - order) / (float) length);
        result[3].anchorLengthVertical *= ((float) (length - order) / (float) length);

        ILine[] horizontalCurves = splitCurve(lineHorizontal, length, order);
        ILine[] verticalCurves = splitCurve(lineVertical, length, order);

        double px = lineHorizontal.getPoint(length, order)[1];
        double py = lineVertical.getPoint(length, order)[1];
        double pz = lineHorizontal.getPoint(length, order)[0];
        List<double[]> acceptablePoints = getAcceptablePoint(lineHorizontal, 0.1);
//        if (acceptablePoints.isEmpty()) {
//            return new RailPosition[]{startRP, startRP, startRP, endRP};
//        }
        double[] point = null;
        double minimumLength = lineHorizontal.getLength();
        for (double[] p : acceptablePoints) {
            if (getLength(p, lineHorizontal.getPoint(length, order)) > minimumLength) {
                break;
            }
            point = p;
            minimumLength = getLength(p, lineHorizontal.getPoint(length, order));
        }
        double[] point2 = lineHorizontal.getPoint(length, order);
        if (point == null) {
            return new RailPosition[]{startRP, startRP, startRP, endRP};
        }
        // lineHorizontal的坐标数组[0]对应Z，[1]对应X
        // lineVertical的坐标数组[0]对应自0至两RailPositions坐标之间的直线距离中的某一点，[1]对应自startRP起上抬的坐标
        int realDir = getRPDirection(lineHorizontal.getPoint(length, order - 2 * QUANTIZE), lineHorizontal.getPoint(length, order + 2 * QUANTIZE), isCornerOnly);
        double posX = point[1];
        double posZ = point[0];
        int blockX1 = (int) Math.floor(posX);
        int blockZ1 = (int) Math.floor(posZ);
        int direction1 = getRPDirection(blockX1, blockZ1, posX, posZ, isCornerOnly);
        int direction2 = ((direction1 + 4) & 0x07);
        int blockX2 = blockX1;
        int blockZ2 = blockZ1;
        switch (direction1) {
        case 0:
            blockZ2 -= 1;
            break;
        case 1:
            blockX2 -= 1;
            blockZ2 -= 1;
            break;
        case 2:
            blockX2 -= 1;
            break;
        case 3:
            blockX2 -= 1;
            blockZ2 += 1;
            break;
        case 4:
            blockZ2 += 1;
            break;
        case 5:
            blockX2 += 1;
            blockZ2 += 1;
            break;
        case 6:
            blockX2 += 1;
            break;
        case 7:
            blockX2 += 1;
            blockZ2 -= 1;
            break;
        }
        double posY = lineVertical.getPoint(length, order)[1] - 0.0625;
        int blockY = (int) posY;
        double pushUp = posY - Math.floor(posY);
        if (realDir == direction1) {
            result[1] = new RailPosition(blockX1, blockY, blockZ1, direction1, 0);
            result[2] = new RailPosition(blockX2, blockY, blockZ2, direction2, 0);
        } else {
            result[2] = new RailPosition(blockX1, blockY, blockZ1, direction1, 0);
            result[1] = new RailPosition(blockX2, blockY, blockZ2, direction2, 0);
        }
        result[1].addHeight(pushUp - Math.floor(pushUp));
        result[2].addHeight(pushUp - Math.floor(pushUp));
        result[1].init();
        result[2].init();
        // splitPoint 被切开的点
        // endPoint 锚线，对应的贝塞尔曲线控制点，和splitPoint相同则为直线
        double[] splitPointH1;
        double[] endPointH1;
        double[] splitPointV1;
        double[] endPointV1;
        double[] splitPointH2;
        double[] endPointH2;
        double[] splitPointV2;
        double[] endPointV2;

        if (lineHorizontal instanceof BezierCurveAdvanced) {
            splitPointH1 = point;
            endPointH1 = ((BezierCurveAdvanced) horizontalCurves[0]).cpE.clone();
            splitPointH2 = point;
            endPointH2 = ((BezierCurveAdvanced) horizontalCurves[1]).cpS.clone();
        } else if (lineHorizontal instanceof StraightLine) {
            splitPointH1 = point;
            endPointH1 = splitPointH1;
            splitPointH2 = point;
            endPointH2 = splitPointH2;
        } else {
            return null;
        }
        if (lineVertical instanceof BezierCurveAdvanced) {
            splitPointV1 = lineVertical.getPoint(length, order);
            endPointV1 = ((BezierCurveAdvanced) verticalCurves[0]).cpE.clone();
            splitPointV2 = lineVertical.getPoint(length, order);
            endPointV2 = ((BezierCurveAdvanced) verticalCurves[1]).cpS.clone();
        } else if (lineVertical instanceof StraightLine) {
            splitPointV1 = lineVertical.getPoint(length, order);
            endPointV1 = lineVertical.getPoint(length, order);
            splitPointV2 = lineVertical.getPoint(length, order);
            endPointV2 = lineVertical.getPoint(length, order);
        } else {
            return null;
        }

        result[1].anchorLengthHorizontal = (float) getLength(splitPointH1, endPointH1);
        result[1].anchorYaw = getAngleD((splitPointH1), (endPointH1));

        result[1].anchorLengthVertical = (float) getLength(splitPointV1, endPointV1);
        result[1].anchorPitch = Math.abs(getAngleD((splitPointV1), (endPointV1)));
        if (endPointV1[1] < splitPointV1[1]) {
            result[1].anchorPitch = -result[1].anchorPitch;
        }

        result[2].anchorLengthHorizontal = (float) getLength(splitPointH2, endPointH2);
        result[2].anchorYaw = getAngleD((splitPointH2), (endPointH2));

        result[2].anchorLengthVertical = (float) getLength(splitPointV2, endPointV2);
        result[2].anchorPitch = Math.abs(getAngleD((splitPointV2), (endPointV2)));
        if (endPointV2[1] < splitPointV2[1]) {
            result[1].anchorPitch = -result[1].anchorPitch;
        }
        return result;
    }

    public static List<double[]> getAcceptablePoint(ILine line, double threshold) {
        boolean isStraightLine = line instanceof StraightLine;
        List<double[]> vecList = new ArrayList<>();
        for (int i = 0; i < line.getLength() * QUANTIZE; i++) {
            double[] point = line.getPoint((int) Math.floor(line.getLength() * QUANTIZE), i);
            if (isStraightLine ? isPointAcceptable(point, threshold) : isPointAcceptableCorner(point, threshold)) {
                vecList.add(point);
            }
        }
        return vecList;
    }

    public static byte getRPDirection(int blockX, int blockZ, double posX, double posZ, boolean isCornerOnly) {
        double xOffset = posX - blockX;
        double zOffset = posZ - blockZ;
        return getRPDirection(new double[]{0.5, 0.5}, new double[]{zOffset, xOffset}, isCornerOnly);
    }

    public static byte getRPDirection(double[] start, double[] end, boolean isCornerOnly) {
        if (isCornerOnly) {
            float angle = (float) NGTMath.normalizeAngle(getAngleD(start, end) + 180.0f);
            return (byte) (Math.floor(angle / 90.0f) * 2 + 1);
        } else {
            float angle = (float) NGTMath.normalizeAngle(getAngleD(start, end) + 180.0f + 22.5f);
            return (byte) Math.floor(angle / 45.0f);
        }
    }

    public static boolean isPointAcceptable(double[] in, double threshold) {
        double[] decimal = new double[]{in[0] - Math.floor(in[0]), in[1] - Math.floor(in[1])};
        int[] result = new int[]{0, 0};
        // 0 不支持
        // 1 趋向于0
        // 2 趋向于0.5
        // 3 趋向于1
        for (int i = 0; i < 2; i++) {
            if (0.0 <= decimal[i] && decimal[i] <= threshold) {
                result[i] = 1;
            } else if (1.0 - threshold <= decimal[i] && decimal[i] <= 1.0) {
                result[i] = 3;
            } else if (0.5 - threshold <= decimal[i] && decimal[i] <= 0.5 + threshold) {
                result[i] = 2;
            }
        }
        if (result[0] == result[1] && result[0] == 2) {
            return false; // 排除处在方块中心的情况
        }
        return (result[0] > 0 && result[1] > 0);
    }

    public static boolean isPointAcceptableCorner(double[] in, double threshold) {
        double[] decimal = new double[]{in[0] - Math.floor(in[0]), in[1] - Math.floor(in[1])};
        int[] result = new int[]{0, 0};
        // 0 不支持
        // 1 趋向于0
        // 2 趋向于0.5
        // 3 趋向于1
        for (int i = 0; i < 2; i++) {
            if (0.0 <= decimal[i] && decimal[i] <= threshold) {
                result[i] = 1;
            } else if (1.0 - threshold <= decimal[i] && decimal[i] <= 1.0) {
                result[i] = 3;
            }
        }
        return (result[0] > 0 && result[1] > 0);
    }

    public static double[] swap(double[] par1) {
        return new double[]{par1[1], par1[0]};
    }

    public static double getLength(double[] start, double[] end) {
        if (Arrays.equals(start, end)) {
            return 0.0f;
        } else {
            return Math.sqrt(Math.pow(start[0] - end[0], 2) + Math.pow(start[1] - end[1], 2));
        }
    }

    public static float getAngleD(double[] start, double[] end) {
        if (start[0] == end[0]) {
            if (start[1] < end[1]) return 90.0f;
            else if (start[1] > end[1]) return -90.0f;
            else return 0.0f;
        } else {
            return (float) NGTMath.toDegrees(Math.atan2(end[1] - start[1], end[0] - start[0]));
        }
    }


    public static RailPosition cloneRP(RailPosition in) {
        RailPosition out = new RailPosition(in.blockX, in.blockY, in.blockZ, in.direction, in.switchType);
        out.height = in.height;
        out.anchorYaw = in.anchorYaw;
        out.anchorPitch = in.anchorPitch;
        out.anchorLengthHorizontal = in.anchorLengthHorizontal;
        out.anchorLengthVertical = in.anchorLengthVertical;
        out.cantCenter = in.cantCenter;
        out.cantEdge = in.cantEdge;
        out.cantRandom = in.cantRandom;
        out.constLimitHP = in.constLimitHP;
        out.constLimitHN = in.constLimitHN;
        out.constLimitWP = in.constLimitWP;
        out.constLimitWN = in.constLimitWN;
        out.scriptName = in.scriptName;
        out.scriptArgs = in.scriptArgs;
        out.init();
        return out;
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

    public float getHomogenizedParameter(int n, int par2) {
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
        float[] dd = new float[this.split + 1];
        float f1 = 0.0F;
        double[] adouble = this.sp;
        double[] adouble1 = new double[2];
        dd[0] = 0.0F;

        for (int i = 1; i < this.split + 1; ++i) {
            f1 += f;
            adouble1 = this.getPointFromParameter((double) f1);
            dd[i] = dd[i - 1] + (float) this.getDistance(adouble[0], adouble1[0], adouble[1], adouble1[1]);
            adouble = adouble1;
        }

        for (int k = 1; k < this.split + 1; ++k) {
            dd[k] /= dd[this.split];
        }

        long start = System.currentTimeMillis();
        // 算法已经优化过了，暂时分一个线程去计算
        if (split > ModConfig.multiThreadBezierCurveInitThreshold * 100) {
            RailProcessThread thread = RailProcessThread.getInstance();
            int step = 100;
            for (int index = 0; index < this.split; index += step) {
                TaskInitNP task = new TaskInitNP(this, dd, index, (int) step);
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
            for (int i = 0; i < this.split; ++i) {
                float t = (float) i / (float) this.split;
                int k = 0;
                int searchMin = 0, searchMax = split - 1;
                int loopTimes = 0;
                // 二分法搜索
                while (!(dd[k] <= t && t <= dd[k + 1])) {
                    k = (searchMax - searchMin) / 2 + searchMin;
                    // k in [0~half]
                    if(dd[searchMin] <= t && t <= dd[k + 1]){
                        searchMax = k;
                    } else /* dd[k] <= t && t <= dd[searchMax] */ {
                        searchMin = k;
                    }

                    if(++loopTimes >= split - 1)
                        break;
                }

//                for (k = 0; k < split - 1; ++k) {
//                    if (dd[k] <= t && t <= dd[k + 1]) break;
//                }

                float x = (t - dd[k]) / (dd[k + 1] - dd[k]);
                x = ((float) k * (1.0F - x) + (float) (1 + k) * x) * (1.0F / (float) this.split);
                this.normalizedParameters[i] = x;
            }
            ModLog.debug("initNP took " + (System.currentTimeMillis() - start) + "ms");
        }
    }

    public void normalizeParams(float[] dd, int index, int step) {
        for (int i = index; (i < this.split && i < index + step); ++i) {
            float t = (float) i / (float) this.split;
            int k = 0;
            int searchMin = 0, searchMax = split - 1;
            int loopTimes = 0;
            while (!(dd[k] <= t && t <= dd[k + 1])) {
                k = (searchMax - searchMin) / 2 + searchMin;
                // k in [0~half]
                if (dd[searchMin] <= t && t <= dd[k + 1]) {
                    searchMax = k;
                } else /* dd[k] <= t && t <= dd[searchMax] */ {
                    searchMin = k;
                }

                if (++loopTimes >= split - 1)
                    break;
            }

//            for (k = 0; k < split - 1; ++k) {
//                if (dd[k] <= t && t <= dd[k + 1]) break;
//            }

            float x = (t - dd[k]) / (dd[k + 1] - dd[k]);
            x = ((float) k * (1.0F - x) + (float) (1 + k) * x) * (1.0F / (float) this.split);
            this.normalizedParameters[i] = x;
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

