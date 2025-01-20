package net.cacpixel.rtmmetro.rail.util;

import jp.ngt.ngtlib.block.BlockUtil;
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
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.math.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RailMapAdvanced extends RailMapBasic
{
    public final static int QUANTIZE = 32;

    public RailMapAdvanced(RailPosition par1, RailPosition par2)
    {
        super(par1, par2);
        if (startRP.cantCenter > 0 && startRP.cantEdge < 0 || startRP.cantCenter < 0 && startRP.cantEdge > 0)
        {
            startRP.cantCenter = -startRP.cantCenter;
            endRP.cantCenter = startRP.cantCenter;
        }
    }

    public ILineAdvanced getLineHorizontal()
    {
        if (this.lineHorizontal instanceof ILineAdvanced)
        {
            return (ILineAdvanced) this.lineHorizontal;
        }
        else
        {
            return null;
        }
    }

    public ILineAdvanced getLineVertical()
    {
        if (this.lineVertical instanceof ILineAdvanced)
        {
            return (ILineAdvanced) this.lineVertical;
        }
        else
        {
            return null;
        }
    }

    @Override
    protected void createLine()
    {
        double startX = this.startRP.posX;
        double startY = this.startRP.posY;
        double startZ = this.startRP.posZ;
        double endX = this.endRP.posX;
        double endY = this.endRP.posY;
        double endZ = this.endRP.posZ;
        boolean isOppositeMarker = Math.abs(this.endRP.direction - this.startRP.direction) == 4;
        boolean isInSameAxis = startZ == endZ || startX == endX;
        boolean isOpposite45 = Math.abs(startZ - endZ) == Math.abs(startX - endX) && this.startRP.direction % 2 != 0 &&
                this.endRP.direction % 2 != 0;
        boolean isFaceToFace = (isOppositeMarker && (isInSameAxis || isOpposite45));
        boolean lineMoved = (startRP.anchorYaw != NGTMath.wrapAngle(startRP.direction * 45.0F)
                || endRP.anchorYaw != NGTMath.wrapAngle(endRP.direction * 45.0F));
        boolean isHorizontalLengthZero = startRP.anchorLengthHorizontal <= 0.0f && endRP.anchorLengthHorizontal <= 0.0f
                && startRP.anchorLengthHorizontal != -1.0f && endRP.anchorLengthHorizontal != -1.0f;
        double angleWhileStraight = NGTMath.toDegrees(Math.atan2(endX - startX, endZ - startZ));
        double angleWhileStraightV = NGTMath.toDegrees(
                Math.atan2(endY - startY, CacMath.getLength(new double[]{startX, startZ}, new double[]{endX, endZ})));
        boolean yawApproachingStraight = (Math.abs(angleWhileStraight - this.startRP.anchorYaw) < 0.0001F
                && Math.abs(MathHelper.wrapDegrees(angleWhileStraight + 180.0) - this.endRP.anchorYaw) < 0.0001F);
        boolean pitchApproachingStraight = (Math.abs(angleWhileStraightV - this.startRP.anchorPitch) < 0.0001F &&
                Math.abs(-angleWhileStraightV - this.endRP.anchorPitch) < 0.0001F);
        boolean isSwitch = this.startRP.switchType == 1 || this.endRP.switchType == 1;

        boolean shouldCreateStraightLine = !lineMoved && isFaceToFace;
        if (isHorizontalLengthZero || yawApproachingStraight)
            shouldCreateStraightLine = true;
        if (isSwitch)
            shouldCreateStraightLine = false;

        if (shouldCreateStraightLine)
        {
            this.lineHorizontal = new StraightLineAdvanced(startZ, startX, endZ, endX);
            this.startRP.anchorYaw = (float) MathHelper.wrapDegrees(NGTMath.toDegrees(this.lineHorizontal.getSlope(0, 0)));
            this.endRP.anchorYaw = (float) MathHelper.wrapDegrees(180.0 + NGTMath.toDegrees(this.lineHorizontal.getSlope(0, 0)));
            this.startRP.anchorLengthHorizontal = this.endRP.anchorLengthHorizontal = -0.75F;
        }
        else
        {
            double d6 = Math.abs(endZ - startZ);
            double d7 = Math.abs(endX - startX);
            double d9 = Math.max(d6, d7);
            double d11 = Math.min(d6, d7);
            if (this.startRP.anchorLengthHorizontal < 0.0F)
            {
                boolean isNot45 = this.startRP.direction % 2 == 0;
                double d13 = isNot45 ? d9 : d11;
                this.startRP.anchorLengthHorizontal = (float) (d13 *
                        (double) 0.5522848F); // 0.5522848F 用来计算绘制圆形贝塞尔曲线控制点的位置的常数
            }

            if (this.endRP.anchorLengthHorizontal < 0.0F)
            {
                boolean flag5 = this.endRP.direction % 2 == 0;
                double d19 = flag5 ? d9 : d11;
                this.endRP.anchorLengthHorizontal = (float) (d19 * (double) 0.5522848F);
            }

            double d18 = (double) (NGTMath.cos(this.startRP.anchorYaw) * this.startRP.anchorLengthHorizontal);
            double d14 = (double) (NGTMath.sin(this.startRP.anchorYaw) * this.startRP.anchorLengthHorizontal);
            double d15 = (double) (NGTMath.cos(this.endRP.anchorYaw) * this.endRP.anchorLengthHorizontal);
            double d16 = (double) (NGTMath.sin(this.endRP.anchorYaw) * this.endRP.anchorLengthHorizontal);
            this.lineHorizontal = new BezierCurveAdvanced(startZ, startX, startZ + d18, startX + d14, endZ + d15,
                    endX + d16, endZ, endX);
        }

        double d17 = Math.sqrt(NGTMath.pow(endX - startX, 2) + NGTMath.pow(endZ - startZ, 2));
        boolean flag4 = this.startRP.anchorLengthVertical <= 0.0F && this.endRP.anchorLengthVertical <= 0.0F;
        if (flag4 || pitchApproachingStraight || isSwitch)
        {
            this.lineVertical = new StraightLineAdvanced(0.0D, startY, d17, endY);
            this.startRP.anchorPitch = (float) MathHelper.wrapDegrees(NGTMath.toDegrees(this.lineVertical.getSlope(0, 0)));
            this.endRP.anchorPitch = (float) MathHelper.wrapDegrees(-NGTMath.toDegrees(this.lineVertical.getSlope(0, 0)));
            if (!isSwitch)
                this.startRP.anchorLengthVertical = this.endRP.anchorLengthVertical = -1.5F;
            else
                this.startRP.anchorLengthVertical = this.endRP.anchorLengthVertical = 0;
        }
        else
        {
            double d8 = (double) (NGTMath.cos(this.startRP.anchorPitch) * this.startRP.anchorLengthVertical);
            double d10 = (double) (NGTMath.sin(this.startRP.anchorPitch) * this.startRP.anchorLengthVertical);
            double d12 = (double) (NGTMath.cos(this.endRP.anchorPitch) * this.endRP.anchorLengthVertical);
            double d20 = (double) (NGTMath.sin(this.endRP.anchorPitch) * this.endRP.anchorLengthVertical);
            this.lineVertical = new BezierCurveAdvanced(0.0D, startY, d8, startY + d10, d17 - d12, endY + d20, d17,
                    endY);
        }

        if (this.startRP.anchorLengthHorizontal <= 0.0F)
        {
            this.startRP.anchorYaw = (float) MathHelper.wrapDegrees(NGTMath.toDegrees(this.lineHorizontal.getSlope(4, 0)));
        }
        if (this.endRP.anchorLengthHorizontal == 0.0F)
        {
            this.endRP.anchorYaw = (float) MathHelper.wrapDegrees(180.0 + NGTMath.toDegrees(this.lineHorizontal.getSlope(4, 4)));
        }
        this.startRP.anchorPitch = Math.min(this.startRP.anchorPitch, 89.9F);
        this.startRP.anchorPitch = Math.max(this.startRP.anchorPitch, -89.9F);
        this.endRP.anchorPitch = Math.min(this.endRP.anchorPitch, 89.9F);
        this.endRP.anchorPitch = Math.max(this.endRP.anchorPitch, -89.9F);
    }

    @Override
    protected void createRailList(ResourceStateRail prop)
    {
        ModelSetRail modelSet = (ModelSetRail) prop.getResourceSet();
        int halfWidth = ((RailConfig) modelSet.getConfig()).ballastWidth >> 1;
        this.rails.clear();
        int split = (int) (this.getLength() * 4.0);
        double halfPi = 1.5707963267948966;

        for (int j = 1; j < split - 1; ++j)
        {
            double[] point = this.getRailPos(split, j);
            double x = point[1];
            double z = point[0];
            double slope = (double) NGTMath.toRadians(this.getRailYaw(split, j));
            double height = this.getRailHeight(split, j);
            int y = (int) height;

            int x0;
            for (x0 = 0; x0 <= halfWidth; ++x0)
            {
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
    public List<int[]> getRailBlockList(ResourceStateRail prop, boolean regenerate)
    {
        if (this.rails.isEmpty() || regenerate)
        {
            this.createRailList(prop);
        }

        return new ArrayList(this.rails);
    }

    @Override
    protected void addRailBlock(int x, int y, int z)
    {
        int itrMax = 30;
        for (int i = this.rails.size() - 1; (i >= 0) && (this.rails.size() - i <= itrMax); i--)
        {
            int[] ia = this.rails.get(i);
            if (ia[0] == x && ia[2] == z)
            {
                if (ia[1] <= y)
                {
                    return;
                }
                this.rails.remove(i);
//                --i;
                break;
            }
        }

        BlockPos pos = new BlockPos(x, y, z);
        if (!pos.equals(this.getStartRP().getNeighborBlockPos()) && !pos.equals(this.getEndRP().getNeighborBlockPos()))
        {
            this.rails.add(new int[]{x, y, z});
        }
    }

    @Override
    public void setRail(World world, Block block, int x0, int y0, int z0, ResourceStateRail prop)
    {
        this.createRailList(prop);
        this.setBaseBlock(world, x0, y0, z0);

        for (int i = 0; i < this.rails.size(); ++i)
        {
            int x = ((int[]) this.rails.get(i))[0];
            int y = ((int[]) this.rails.get(i))[1];
            int z = ((int[]) this.rails.get(i))[2];
            Block block2 = BlockUtil.getBlock(world, x, y, z);
            if (!(block2 instanceof BlockLargeRailBase) || block2 == block)
            {
                BlockUtil.setBlock(world, x, y, z, block, 0, 2);
                TileEntityLargeRailBase tile = (TileEntityLargeRailBase) BlockUtil.getTileEntity(world, x, y, z);
                tile.setStartPoint(x0, y0, z0);
            }
        }

        this.rails.clear();
    }

    private void setBaseBlock(World world, int x0, int y0, int z0)
    {
        int split = (int) (this.getLength() * 4.0);
        RailPosition rp = this.getStartRP();
        int minWidth = NGTMath.floor(rp.constLimitWN + 0.5F);
        int maxWidth = NGTMath.floor(rp.constLimitWP + 0.5F);
        int minHeight = NGTMath.floor(rp.constLimitHN);
        int maxHeight = NGTMath.floor(rp.constLimitHP);
        IBlockState[][] template = new IBlockState[maxHeight - minHeight + 1][maxWidth - minWidth + 1];

        for (int k = 0; k < split - 1; ++k)
        {
            double[] point = this.getRailPos(split, k);
            double x = point[1];
            double z = point[0];
            double y = this.getRailHeight(split, k);
            float yaw = NGTMath.wrapAngle(this.getRailYaw(split, k));

            for (int i = 0; i < template.length; ++i)
            {
                int h = minHeight + i;

                for (int j = 0; j < template[i].length; ++j)
                {
                    int w = minWidth + j;
                    Vec3 vec = new Vec3((double) w, (double) h, 0.0);
                    vec = vec.rotateAroundY(yaw);
                    BlockPos pos = new BlockPos(x + vec.getX(), y + vec.getY(), z + vec.getZ());
                    if (k == 0)
                    {
                        IBlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();
                        if (!(block instanceof BlockMarker) && !(block instanceof BlockLargeRailBase))
                        {
                            template[i][j] = state;
                        }
                    }
                    else if (template[i][j] != null &&
                            !(world.getBlockState(pos).getBlock() instanceof BlockLargeRailBase))
                    {
                        world.setBlockState(pos, template[i][j]);
                    }
                }
            }
        }
    }

    public List<RailMapAdvanced> split(LinePointWithOrder targetPoint)
    {
        List<RailMapAdvanced> ret = new ArrayList<>();
        label_success:
        {
            if (targetPoint == null)
            {
                break label_success;
            }
            int targetLength = (int) Math.floor(this.getLineHorizontal().getLength() * QUANTIZE / 2);
            // true则只使用对角坐标
            boolean isBezier = lineHorizontal instanceof BezierCurveAdvanced;
            RailPosition[] result = new RailPosition[4];
            result[0] = cloneRP(startRP);
            result[3] = cloneRP(endRP);
            int targetOrder = targetPoint.getOrder();
            // lineHorizontal的坐标数组[0]对应Z，[1]对应X
            // lineVertical的坐标数组[0]对应自0至两RailPositions坐标之间的直线距离中的某一点，[1]对应自startRP起上抬的坐标

            // 计算对面方向需要的block坐标
            double posX = targetPoint.getPoint()[1];
            double posZ = targetPoint.getPoint()[0];
            int blockX1 = (int) Math.floor(posX);
            int blockZ1 = (int) Math.floor(posZ);
            int direction1 = CacMath.getRPDirection(blockX1, blockZ1, posX, posZ, isBezier);
            int direction2 = ((direction1 + 4) & 0x07);
            // 实际的方向，随曲线增长方向一致
            int realDir = CacMath.getRPDirection(lineHorizontal.getPoint(targetLength, targetOrder - 2 * QUANTIZE),
                    lineHorizontal.getPoint(targetLength, targetOrder + 2 * QUANTIZE), isBezier);
            int blockX2 = blockX1;
            int blockZ2 = blockZ1;
            switch (direction1)
            {
            case 0:
                blockZ2--;
                break;
            case 1:
                blockX2--;
                blockZ2--;
                break;
            case 2:
                blockX2--;
                break;
            case 3:
                blockX2--;
                blockZ2++;
                break;
            case 4:
                blockZ2++;
                break;
            case 5:
                blockX2++;
                blockZ2++;
                break;
            case 6:
                blockX2++;
                break;
            case 7:
                blockX2++;
                blockZ2--;
                break;
            default:
                break;
            }
            double posY = lineVertical.getPoint(targetLength, targetOrder)[1] - 0.0625;
            int blockY = (int) posY;
            double pushUp = posY - Math.floor(posY);
            // 依照实际朝向创建RP并初始化
            if (realDir == direction1)
            {
                result[1] = new RailPosition(blockX1, blockY, blockZ1, direction1, 0);
                result[2] = new RailPosition(blockX2, blockY, blockZ2, direction2, 0);
            }
            else
            {
                result[2] = new RailPosition(blockX1, blockY, blockZ1, direction1, 0);
                result[1] = new RailPosition(blockX2, blockY, blockZ2, direction2, 0);
            }
            result[1].addHeight(pushUp - Math.floor(pushUp));
            result[2].addHeight(pushUp - Math.floor(pushUp));
            result[1].init();
            result[2].init();

            // 获得分割后曲线
            ILineAdvanced[] horizontalCurves = this.getLineHorizontal().split(targetLength, targetOrder);
            ILineAdvanced[] verticalCurves = this.getLineVertical().split(targetLength, targetOrder);

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

            if (lineHorizontal instanceof BezierCurveAdvanced)
            {
                splitPointH1 = targetPoint.getPoint();
                endPointH1 = ((BezierCurveAdvanced) horizontalCurves[0]).cpE.clone();
                splitPointH2 = targetPoint.getPoint();
                endPointH2 = ((BezierCurveAdvanced) horizontalCurves[1]).cpS.clone();
                controlPointStartH0 = ((BezierCurveAdvanced) horizontalCurves[0]).cpS.clone();
                controlPointEndH3 = ((BezierCurveAdvanced) horizontalCurves[1]).cpE.clone();
            }
            else if (lineHorizontal instanceof StraightLineAdvanced)
            {
                splitPointH1 = targetPoint.getPoint();
                endPointH1 = splitPointH1;
                splitPointH2 = targetPoint.getPoint();
                endPointH2 = splitPointH2;
                controlPointStartH0 = lineHorizontal.getPoint(targetLength, 0);
                controlPointEndH3 = lineHorizontal.getPoint(targetLength, targetOrder);
            }
            else
            {
                break label_success;
            }
            if (lineVertical instanceof BezierCurveAdvanced)
            {
                splitPointV1 = lineVertical.getPoint(targetLength, targetOrder);
                endPointV1 = ((BezierCurveAdvanced) verticalCurves[0]).cpE.clone();
                splitPointV2 = lineVertical.getPoint(targetLength, targetOrder);
                endPointV2 = ((BezierCurveAdvanced) verticalCurves[1]).cpS.clone();
                controlPointStartV0 = ((BezierCurveAdvanced) verticalCurves[0]).cpS.clone();
                controlPointEndV3 = ((BezierCurveAdvanced) verticalCurves[1]).cpE.clone();
            }
            else if (lineVertical instanceof StraightLineAdvanced)
            {
                splitPointV1 = lineVertical.getPoint(targetLength, targetOrder);
                endPointV1 = lineVertical.getPoint(targetLength, targetOrder);
                splitPointV2 = lineVertical.getPoint(targetLength, targetOrder);
                endPointV2 = lineVertical.getPoint(targetLength, targetOrder);
                controlPointStartV0 = lineVertical.getPoint(targetLength, 0);
                controlPointEndV3 = lineVertical.getPoint(targetLength, targetOrder);
            }
            else
            {
                break label_success;
            }

            result[1].anchorLengthHorizontal = (float) CacMath.getLength(splitPointH1, endPointH1);
            if (lineHorizontal instanceof BezierCurveAdvanced)
            {
                result[1].anchorYaw = (float) CacMath.getWrappedAngleAndReverse(this.getLineHorizontal()
                        .getSlopeD(targetLength, targetOrder));//getAngleD((splitPointH1), (endPointH1));
            }

            result[1].anchorLengthVertical = (float) CacMath.getLength(splitPointV1, endPointV1);
            result[1].anchorPitch = MathHelper.wrapDegrees(CacMath.getAngleD((splitPointV1), (endPointV1)));
            if (result[1].anchorPitch > 90.0F || result[1].anchorPitch < -90.0F)
            {
                result[1].anchorPitch = -CacMath.getWrappedAngleAndReverse(result[1].anchorPitch);
            }

            result[2].anchorLengthHorizontal = (float) CacMath.getLength(splitPointH2, endPointH2);
            if (lineHorizontal instanceof BezierCurveAdvanced)
            {
                result[2].anchorYaw = CacMath.getWrappedAngleAndReverse(
                        result[1].anchorYaw);// getAngleD((splitPointH2), (endPointH2));
            }

            result[2].anchorLengthVertical = (float) CacMath.getLength(splitPointV2, endPointV2);
            result[2].anchorPitch = MathHelper.wrapDegrees(CacMath.getAngleD((splitPointV2), (endPointV2)));
            if (result[2].anchorPitch > 90.0F || result[2].anchorPitch < -90.0F)
            {
                result[2].anchorPitch = -CacMath.getWrappedAngleAndReverse(result[2].anchorPitch);
            }

            result[0].anchorLengthHorizontal = (float) CacMath.getLength(lineHorizontal.getPoint(targetLength, 0),
                    controlPointStartH0);
            result[0].anchorLengthVertical = (float) CacMath.getLength(lineVertical.getPoint(targetLength, 0),
                    controlPointStartV0);
            result[3].anchorLengthHorizontal = (float) CacMath.getLength(
                    lineHorizontal.getPoint(targetLength, targetLength),
                    controlPointEndH3);
            result[3].anchorLengthVertical = (float) CacMath.getLength(
                    lineVertical.getPoint(targetLength, targetLength),
                    controlPointEndV3);

            result[0].cantEdge = this.startRP.cantEdge;
            result[0].cantCenter = this.getRailRoll(targetLength, targetOrder / 2);
            result[1].cantEdge = -this.getRailRoll(targetLength, targetOrder);
            result[1].cantCenter = -result[0].cantCenter;
            result[2].cantEdge = this.getRailRoll(targetLength, targetOrder);
            result[2].cantCenter = this.getRailRoll(targetLength, (targetLength - targetOrder) / 2 + targetOrder);
            result[3].cantEdge = this.endRP.cantEdge;
            result[3].cantCenter = -result[2].cantCenter;

            ret.add(new RailMapAdvanced(result[0], result[1]));
            ret.add(new RailMapAdvanced(result[2], result[3]));
            if (ret.get(0).getLength() < ModConfig.railSplitMinimumLength ||
                    ret.get(1).getLength() < ModConfig.railSplitMinimumLength)
            {
                break label_success;
            }
            return ret;
        }
        ret.clear();
        ret.add(this);
        return ret;
    }

    public List<RailMapAdvanced> split(int lengthIn, int orderIn)
    {
        List<RailMapAdvanced> ret = new ArrayList<>();
        label_success:
        {
            if (lengthIn <= ModConfig.railSplitMinimumLength)
            {
                break label_success;
            }
            // 同乘QUANTIZE细分方便运算
            int length = lengthIn * QUANTIZE;
            int order = orderIn * QUANTIZE;
            int targetLength = (int) Math.floor(this.getLineHorizontal().getLength() * QUANTIZE / 2);
            // true则只使用对角坐标
            boolean isBezier = lineHorizontal instanceof BezierCurveAdvanced;
            // 获取可用点
            List<LinePointWithOrder> acceptablePoints = this.getAcceptablePoint(this.getLineHorizontal(), targetLength,
                    isBezier ? ModConfig.railSplitThreshold : ModConfig.railSplitThresholdStraight, isBezier);
            // targetPoint 将会是最终的分割点
            LinePointWithOrder targetPoint = null;
            double minimumLength = lineHorizontal.getLength();
            // 寻找距离期望分割点最近的可用点
            for (LinePointWithOrder p : acceptablePoints)
            {
                double len = CacMath.getLength(p.getPoint(), lineHorizontal.getPoint(length, order));
                if (len < minimumLength)
                {
                    minimumLength = len;
                    targetPoint = p;
                }
            }
            return this.split(targetPoint);
        }
        ret.add(this);
        return ret;
    }

    public List<LinePointWithOrder> getAcceptablePoint(ILineAdvanced line, int length, double threshold,
                                                       boolean isBezier)
    {
        List<LinePointWithOrder> vecList = new ArrayList<>();
        if (line == null) return vecList;
        double[] prevPoint = null;
        for (int i = 0; i < length; i++)
        {
            double[] point = line.getPoint(length, i);
            if (prevPoint == null)
            {
                prevPoint = point;
                continue;
            }
            if (isBezier ? CacMath.isPointAcceptableCorner(point, threshold) :
                    CacMath.isPointAcceptable(point, threshold))
            {
                double posX = point[1];
                double posZ = point[0];
                int blockX1 = (int) Math.floor(posX);
                int blockZ1 = (int) Math.floor(posZ);
                int direction1 = CacMath.getRPDirection(blockX1, blockZ1, posX, posZ, isBezier);
                int direction2 = ((direction1 + 4) & 0x07);
                int realDir = CacMath.getRPDirection(prevPoint, point, isBezier);   // jvm crash
                if (realDir != -1 && (realDir == direction1 || realDir == direction2))
                {
                    vecList.add(new LinePointWithOrder(point, i, line));
                }
                else if (!isBezier && (direction1 == this.startRP.direction || direction1 == this.endRP.direction
                        || direction2 == this.startRP.direction || direction2 == this.endRP.direction))
                {
                    vecList.add(new LinePointWithOrder(point, i, line));
                }
            }
            prevPoint = point;
        }
        return vecList;
    }

    public static RailPosition cloneRP(RailPosition in)
    {
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
