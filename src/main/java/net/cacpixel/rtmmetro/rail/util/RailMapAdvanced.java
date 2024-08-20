package net.cacpixel.rtmmetro.rail.util;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.math.ILine;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.modelpack.cfg.RailConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.rail.BlockLargeRailBase;
import jp.ngt.rtm.rail.BlockMarker;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import jp.ngt.rtm.rail.util.RailMapBasic;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.cacpixel.rtmmetro.math.ILineAdvanced;
import net.cacpixel.rtmmetro.math.StraightLineAdvanced;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RailMapAdvanced extends RailMapBasic {
    public final static int QUANTIZE = 32;
    private List<int[]> rails = new LinkedList<>();

    public RailMapAdvanced(RailPosition par1, RailPosition par2) {
        super(par1, par2);
    }

    public ILineAdvanced getLineHorizontal() {
        if (this.lineHorizontal instanceof ILineAdvanced) {
            return (ILineAdvanced) this.lineHorizontal;
        } else {
            return null;
        }
    }

    public ILineAdvanced getLineVertical() {
        if (this.lineVertical instanceof ILineAdvanced) {
            return (ILineAdvanced) this.lineVertical;
        } else {
            return null;
        }
    }

    @Override
    protected void createLine() {
        double startX = this.startRP.posX;
        double startY = this.startRP.posY;
        double startZ = this.startRP.posZ;
        double endX = this.endRP.posX;
        double endY = this.endRP.posY;
        double endZ = this.endRP.posZ;
        boolean isOppositeMarker = (this.endRP.direction - this.startRP.direction) % 4 == 0;
        boolean isInSameAxis = startZ == endZ || startX == endX;
        boolean isOpposite45 = Math.abs(startZ - endZ) == Math.abs(startX - endX) && this.startRP.direction % 2 != 0 && this.endRP.direction % 2 != 0;
        boolean lineMoved = (startRP.anchorYaw != NGTMath.wrapAngle(startRP.direction * 45.0F)
                || endRP.anchorYaw != NGTMath.wrapAngle(endRP.direction * 45.0F));
        boolean isValueValid = startRP.anchorLengthHorizontal != 0.0f && endRP.anchorLengthHorizontal != 0.0f;
//            if ((!isOppositeMarker || !isInSameAxis && !isOpposite45) || lineMoved) {
        if (((isOppositeMarker && (isInSameAxis || isOpposite45)) && !lineMoved) || !isValueValid) {
            this.lineHorizontal = new StraightLineAdvanced(startZ, startX, endZ, endX);
        } else {
            double d6 = Math.abs(endZ - startZ);
            double d7 = Math.abs(endX - startX);
            double d9 = Math.max(d6, d7);
            double d11 = Math.min(d6, d7);
            if (this.startRP.anchorLengthHorizontal <= 0.0F) {
                boolean isNot45 = this.startRP.direction % 2 == 0;
                double d13 = isNot45 ? d9 : d11;
                this.startRP.anchorLengthHorizontal = (float) (d13 * (double) 0.5522848F);
            }

            if (this.endRP.anchorLengthHorizontal <= 0.0F) {
                boolean flag5 = this.endRP.direction % 2 == 0;
                double d19 = flag5 ? d9 : d11;
                this.endRP.anchorLengthHorizontal = (float) (d19 * (double) 0.5522848F);
            }

            double d18 = (double) (NGTMath.cos(this.startRP.anchorYaw) * this.startRP.anchorLengthHorizontal);
            double d14 = (double) (NGTMath.sin(this.startRP.anchorYaw) * this.startRP.anchorLengthHorizontal);
            double d15 = (double) (NGTMath.cos(this.endRP.anchorYaw) * this.endRP.anchorLengthHorizontal);
            double d16 = (double) (NGTMath.sin(this.endRP.anchorYaw) * this.endRP.anchorLengthHorizontal);
            this.lineHorizontal = new BezierCurveAdvanced(startZ, startX, startZ + d18, startX + d14, endZ + d15, endX + d16, endZ, endX);
        }

        double d17 = Math.sqrt(NGTMath.pow(endX - startX, 2) + NGTMath.pow(endZ - startZ, 2));
        boolean flag4 = this.startRP.anchorLengthVertical <= 0.0F && this.endRP.anchorLengthVertical <= 0.0F;
        if (flag4) {
            this.lineVertical = new StraightLineAdvanced(0.0D, startY, d17, endY);
        } else {
            double d8 = (double) (NGTMath.cos(this.startRP.anchorPitch) * this.startRP.anchorLengthVertical);
            double d10 = (double) (NGTMath.sin(this.startRP.anchorPitch) * this.startRP.anchorLengthVertical);
            double d12 = (double) (NGTMath.cos(this.endRP.anchorPitch) * this.endRP.anchorLengthVertical);
            double d20 = (double) (NGTMath.sin(this.endRP.anchorPitch) * this.endRP.anchorLengthVertical);
            this.lineVertical = new BezierCurveAdvanced(0.0D, startY, d8, startY + d10, d17 - d12, endY + d20, d17, endY);
        }
    }

    @Override
    protected void createRailList(ResourceStateRail prop) {
        ModelSetRail modelSet = (ModelSetRail) prop.getResourceSet();
        int halfWidth = ((RailConfig) modelSet.getConfig()).ballastWidth >> 1;
        this.rails.clear();
        int split = (int) (this.getLength() * 4.0);
        double halfPi = 1.5707963267948966;

        for (int j = 1; j < split - 1; ++j) {
            double[] point = this.getRailPos(split, j);
            double x = point[1];
            double z = point[0];
            double slope = (double) NGTMath.toRadians(this.getRailYaw(split, j));
            double height = this.getRailHeight(split, j);
            int y = (int) height;

            int x0;
            for (x0 = 0; x0 <= halfWidth; ++x0) {
                double d0 = (double) x0 + 0.25;
                int x1 = NGTMath.floor(x + Math.sin(slope + halfPi) * d0);
                int z1 = NGTMath.floor(z + Math.cos(slope + halfPi) * d0);
                this.addRailBlock(x1, y, z1);
                int x2 = NGTMath.floor(x + Math.sin(slope - halfPi) * d0);
                int z2 = NGTMath.floor(z + Math.cos(slope - halfPi) * d0);
                this.addRailBlock(x2, y, z2);
            }

            x0 = NGTMath.floor(x);
            int z0 = NGTMath.floor(z);
            this.addRailBlock(x0, y, z0);
        }

    }

    @Override
    public List<int[]> getRailBlockList(ResourceStateRail prop, boolean regenerate) {
        if (this.rails.isEmpty() || regenerate) {
            this.createRailList(prop);
        }

        return new ArrayList(this.rails);
    }

    @Override
    protected void addRailBlock(int x, int y, int z) {
        for (int i = 0; i < this.rails.size(); ++i) {
            int[] ia = this.rails.get(i);
            if (ia[0] == x && ia[2] == z) {
                if (ia[1] <= y) {
                    return;
                }

                this.rails.remove(i);
                --i;
            }
        }

        BlockPos pos = new BlockPos(x, y, z);
        if (!pos.equals(this.getStartRP().getNeighborBlockPos()) && !pos.equals(this.getEndRP().getNeighborBlockPos())) {
            this.rails.add(new int[]{x, y, z});
        }
    }

    @Override
    public void setRail(World world, Block block, int x0, int y0, int z0, ResourceStateRail prop) {
        this.createRailList(prop);
        this.setBaseBlock(world, x0, y0, z0);

        for(int i = 0; i < this.rails.size(); ++i) {
            int x = ((int[])this.rails.get(i))[0];
            int y = ((int[])this.rails.get(i))[1];
            int z = ((int[])this.rails.get(i))[2];
            Block block2 = BlockUtil.getBlock(world, x, y, z);
            if (!(block2 instanceof BlockLargeRailBase) || block2 == block) {
                BlockUtil.setBlock(world, x, y, z, block, 0, 2);
                TileEntityLargeRailBase tile = (TileEntityLargeRailBase)BlockUtil.getTileEntity(world, x, y, z);
                tile.setStartPoint(x0, y0, z0);
            }
        }

        this.rails.clear();
    }

    private void setBaseBlock(World world, int x0, int y0, int z0) {
        int split = (int) (this.getLength() * 4.0);
        RailPosition rp = this.getStartRP();
        int minWidth = NGTMath.floor(rp.constLimitWN + 0.5F);
        int maxWidth = NGTMath.floor(rp.constLimitWP + 0.5F);
        int minHeight = NGTMath.floor(rp.constLimitHN);
        int maxHeight = NGTMath.floor(rp.constLimitHP);
        IBlockState[][] template = new IBlockState[maxHeight - minHeight + 1][maxWidth - minWidth + 1];

        for (int k = 0; k < split - 1; ++k) {
            double[] point = this.getRailPos(split, k);
            double x = point[1];
            double z = point[0];
            double y = this.getRailHeight(split, k);
            float yaw = NGTMath.wrapAngle(this.getRailYaw(split, k));

            for (int i = 0; i < template.length; ++i) {
                int h = minHeight + i;

                for (int j = 0; j < template[i].length; ++j) {
                    int w = minWidth + j;
                    Vec3 vec = new Vec3((double) w, (double) h, 0.0);
                    vec = vec.rotateAroundY(yaw);
                    BlockPos pos = new BlockPos(x + vec.getX(), y + vec.getY(), z + vec.getZ());
                    if (k == 0) {
                        IBlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();
                        if (!(block instanceof BlockMarker) && !(block instanceof BlockLargeRailBase)) {
                            template[i][j] = state;
                        }
                    } else if (template[i][j] != null && !(world.getBlockState(pos).getBlock() instanceof BlockLargeRailBase)) {
                        world.setBlockState(pos, template[i][j]);
                    }
                }
            }
        }
    }

    public List<RailMapAdvanced> split(int lengthIn, int orderIn) {
        List<RailMapAdvanced> ret = new ArrayList<>();
        // 同乘QUANTIZE细分方便运算
        int length = lengthIn * QUANTIZE;
        int order = orderIn * QUANTIZE;
        // true则只使用对角坐标
        double startX = this.startRP.posX;
        double startZ = this.startRP.posZ;
        double endX = this.endRP.posX;
        double endZ = this.endRP.posZ;
        boolean isInSameAxis = startZ == endZ || startX == endX;
        boolean isCornerOnly = lineHorizontal instanceof BezierCurveAdvanced; // && isInSameAxis;
        RailPosition[] result = new RailPosition[4];
        result[0] = cloneRP(startRP);
        result[3] = cloneRP(endRP);
        // 获取可用点
        List<double[]> acceptablePoints = this.getAcceptablePoint(lineHorizontal, 0.15, isCornerOnly);
        // point将会是最终的分割点
        double[] point = null;
        double minimumLength = lineHorizontal.getLength();
        // 寻找距离期望分割点最近的可用点
        for (double[] p : acceptablePoints) {
            if (getLength(p, lineHorizontal.getPoint(length, order)) < minimumLength) {
                minimumLength = getLength(p, lineHorizontal.getPoint(length, order));
                point = p;
            }
        }
        if (point == null) {
            ret.add(this);
            return ret;
        }
        // lineHorizontal的坐标数组[0]对应Z，[1]对应X
        // lineVertical的坐标数组[0]对应自0至两RailPositions坐标之间的直线距离中的某一点，[1]对应自startRP起上抬的坐标

        // 计算对面方向需要的block坐标
        double posX = point[1];
        double posZ = point[0];
        int blockX1 = (int) Math.floor(posX);
        int blockZ1 = (int) Math.floor(posZ);
        int direction1 = getRPDirection(blockX1, blockZ1, posX, posZ, isCornerOnly);
        int direction2 = ((direction1 + 4) & 0x07);
        // 实际的方向，随曲线增长方向一致
        int realDir = getRPDirection(lineHorizontal.getPoint(length, order - 2 * QUANTIZE), lineHorizontal.getPoint(length, order + 2 * QUANTIZE), isCornerOnly);
        int blockX2 = blockX1;
        int blockZ2 = blockZ1;
        switch (direction1) {
            case 0: blockZ2--; break;
            case 1: blockX2--; blockZ2--; break;
            case 2: blockX2--; break;
            case 3: blockX2--; blockZ2++; break;
            case 4: blockZ2++; break;
            case 5: blockX2++; blockZ2++; break;
            case 6: blockX2++; break;
            case 7: blockX2++; blockZ2--; break;
            default: break;
        }
        double posY = lineVertical.getPoint(length, order)[1] - 0.0625;
        int blockY = (int) posY;
        double pushUp = posY - Math.floor(posY);
        // 依照实际朝向创建RP并初始化
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

        // 获得分割后曲线
        ILineAdvanced[] horizontalCurves = this.getLineHorizontal().split(length, order);
        ILineAdvanced[] verticalCurves = this.getLineVertical().split(length, order);

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
        // 计算起始和终点两边的anchor长度用的点
        double[] controlPointStartH0;
        double[] controlPointEndH3;
        double[] controlPointStartV0;
        double[] controlPointEndV3;

        if (lineHorizontal instanceof BezierCurveAdvanced) {
            splitPointH1 = point;
            endPointH1 = ((BezierCurveAdvanced) horizontalCurves[0]).cpE.clone();
            splitPointH2 = point;
            endPointH2 = ((BezierCurveAdvanced) horizontalCurves[1]).cpS.clone();
            controlPointStartH0 = ((BezierCurveAdvanced) horizontalCurves[0]).cpS.clone();
            controlPointEndH3 = ((BezierCurveAdvanced) horizontalCurves[1]).cpE.clone();
        } else if (lineHorizontal instanceof StraightLineAdvanced) {
            splitPointH1 = point;
            endPointH1 = splitPointH1;
            splitPointH2 = point;
            endPointH2 = splitPointH2;
            controlPointStartH0 = lineHorizontal.getPoint(length, 0);
            controlPointEndH3 = lineHorizontal.getPoint(length, length);
        } else {
            return null;
        }
        if (lineVertical instanceof BezierCurveAdvanced) {
            splitPointV1 = lineVertical.getPoint(length, order);
            endPointV1 = ((BezierCurveAdvanced) verticalCurves[0]).cpE.clone();
            splitPointV2 = lineVertical.getPoint(length, order);
            endPointV2 = ((BezierCurveAdvanced) verticalCurves[1]).cpS.clone();
            controlPointStartV0 = ((BezierCurveAdvanced) verticalCurves[0]).cpS.clone();
            controlPointEndV3 = ((BezierCurveAdvanced) verticalCurves[1]).cpE.clone();
        } else if (lineVertical instanceof StraightLineAdvanced) {
            splitPointV1 = lineVertical.getPoint(length, order);
            endPointV1 = lineVertical.getPoint(length, order);
            splitPointV2 = lineVertical.getPoint(length, order);
            endPointV2 = lineVertical.getPoint(length, order);
            controlPointStartV0 = lineVertical.getPoint(length, 0);
            controlPointEndV3 = lineVertical.getPoint(length, length);
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
            result[2].anchorPitch = -result[2].anchorPitch;
        }

        result[0].anchorLengthHorizontal = (float) getLength(lineHorizontal.getPoint(length, 0), controlPointStartH0);
        result[0].anchorLengthVertical = (float) getLength(lineVertical.getPoint(length, 0), controlPointStartV0);
        result[3].anchorLengthHorizontal = (float) getLength(lineHorizontal.getPoint(length, length), controlPointEndH3);
        result[3].anchorLengthVertical = (float) getLength(lineVertical.getPoint(length, length), controlPointEndV3);

        ret.add(new RailMapAdvanced(result[0], result[1]));
        ret.add(new RailMapAdvanced(result[2], result[3]));
        return ret;
    }

    public List<double[]> getAcceptablePoint(ILine line, double threshold, boolean isCornerOnly) {
        boolean isStraightLineAdvanced = line instanceof StraightLineAdvanced;
        List<double[]> vecList = new ArrayList<>();
        double[] prevPoint = null;
        for (int i = 0; i < line.getLength() * QUANTIZE; i++) {
            double[] point = line.getPoint((int) Math.floor(line.getLength() * QUANTIZE), i);
            if (isCornerOnly ? isPointAcceptableCorner(point, threshold) : isPointAcceptable(point, threshold)) {
                double posX = point[1];
                double posZ = point[0];
                int blockX1 = (int) Math.floor(posX);
                int blockZ1 = (int) Math.floor(posZ);
                int direction1 = getRPDirection(blockX1, blockZ1, posX, posZ, isCornerOnly);
                int direction2 = ((direction1 + 4) & 0x07);
                int realDir = getRPDirection(prevPoint, point, isCornerOnly);
                if (realDir != -1 && (realDir == direction1 || realDir == direction2)) {
                    vecList.add(point);
                }
            }
            prevPoint = point;
        }
        return vecList;
    }

    public static int getRPDirection(int blockX, int blockZ, double posX, double posZ, boolean isCornerOnly) {
        double xOffset = posX - blockX;
        double zOffset = posZ - blockZ;
        return getRPDirection(new double[]{0.5, 0.5}, new double[]{zOffset, xOffset}, isCornerOnly);
    }

    public static int getRPDirection(double[] start, double[] end, boolean isCornerOnly) {
        if (start == null || end == null || start.length != 2 || end.length != 2) {
            return -1;
        }
        if (isCornerOnly) {
            float angle = (float) NGTMath.normalizeAngle(getAngleD(start, end) + 180.0f);
            return (byte) (Math.floor(angle / 90.0f) * 2 + 1) & 0x7;
        } else {
            float angle = (float) NGTMath.normalizeAngle(getAngleD(start, end) + 180.0f + 22.5f);
            return (byte) Math.floor(angle / 45.0f) & 0x7;
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
        return ((result[0] > 0) && (result[1] > 0));
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
        return ((result[0] > 0) && (result[1] > 0));
    }

    private static double[] swap(double[] par1) {
        return new double[]{par1[1], par1[0]};
    }

    public static double getLength(double[] start, double[] end) {
        if (Arrays.equals(start, end)) {
            return 0.0f;
        } else {
            return Math.sqrt(Math.pow(start[0] - end[0], 2) + Math.pow(start[1] - end[1], 2));
        }
    }

    private static float getAngleD(double[] start, double[] end) {
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
}
