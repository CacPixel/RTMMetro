package net.cacpixel.rtmmetro.rail.util;

import jp.ngt.ngtlib.math.ILine;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.StraightLine;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.modelpack.cfg.RailConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.rail.BlockLargeRailBase;
import jp.ngt.rtm.rail.BlockMarker;
import jp.ngt.rtm.rail.util.RailMapBasic;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RailMapAdvanced extends RailMapBasic {

    public RailMapAdvanced(RailPosition par1, RailPosition par2) {
        super(par1, par2);
    }

    public ILine getLineHorizontal() {
        return this.lineHorizontal;
    }

    public ILine getLineVertical() {
        return this.lineVertical;
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
        boolean isValueValid = startRP.anchorYaw != 0.0f && endRP.anchorYaw != 0.0f;
//            if ((!isOppositeMarker || !isInSameAxis && !isOpposite45) || lineMoved) {
        if (((isOppositeMarker && (isInSameAxis || isOpposite45)) && !lineMoved) || !isValueValid) {
            this.lineHorizontal = new StraightLine(startZ, startX, endZ, endX);
        } else {
            double d6 = Math.abs(endZ - startZ);
            double d7 = Math.abs(endX - startX);
            double d9 = Math.max(d6, d7);
            double d11 = Math.min(d6, d7);
            if (this.startRP.anchorLengthHorizontal <= 0.0F) {
                boolean flag3 = this.startRP.direction % 2 == 0;
                double d13 = flag3 ? d9 : d11;
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
            this.lineVertical = new StraightLine(0.0D, startY, d17, endY);
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
    protected void addRailBlock(int x, int y, int z) {
        for (int i = 0; i < this.rails.size(); ++i) {
            int[] ia = (int[]) this.rails.get(i);
            if (ia[0] == x && ia[2] == z) {
                if (ia[1] <= y) {
                    return;
                }

                this.rails.remove(i);
                --i;
            }
        }

        BlockPos pos = new BlockPos(x, y, z);
//        if (/*!pos.equals(new BlockPos(this.startRP.blockX, this.startRP.blockY, this.startRP.blockZ))
//                && */!pos.equals(new BlockPos(this.endRP.blockX, this.endRP.blockY, this.endRP.blockZ))) {
            this.rails.add(new int[]{x, y, z});
//        } else {
//            NGTLog.debug(this.getEndRP().getNeighborBlockPos().toString());
//        }

    }

    @Override
    public void setRail(World world, Block block, int x0, int y0, int z0, ResourceStateRail prop) {
        super.setRail(world, block, x0, y0, z0, prop);
    }

    public void setRail(World world, Block block, int x0, int y0, int z0, ResourceStateRail prop, BlockPos conflict) {
        super.setRail(world, block, x0, y0, z0, prop);

    }

    private void setBaseBlock(World world, int x0, int y0, int z0) {
        int split = (int)(this.getLength() * 4.0);
        RailPosition rp = this.getStartRP();
        int minWidth = NGTMath.floor(rp.constLimitWN + 0.5F);
        int maxWidth = NGTMath.floor(rp.constLimitWP + 0.5F);
        int minHeight = NGTMath.floor(rp.constLimitHN);
        int maxHeight = NGTMath.floor(rp.constLimitHP);
        IBlockState[][] template = new IBlockState[maxHeight - minHeight + 1][maxWidth - minWidth + 1];

        for(int k = 0; k < split - 1; ++k) {
            double[] point = this.getRailPos(split, k);
            double x = point[1];
            double z = point[0];
            double y = this.getRailHeight(split, k);
            float yaw = NGTMath.wrapAngle(this.getRailYaw(split, k));

            for(int i = 0; i < template.length; ++i) {
                int h = minHeight + i;

                for(int j = 0; j < template[i].length; ++j) {
                    int w = minWidth + j;
                    Vec3 vec = new Vec3((double)w, (double)h, 0.0);
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

}
