package net.cacpixel.rtmmetro.rail.util;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.BezierCurve;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.StraightLine;
import jp.ngt.rtm.modelpack.cfg.RailConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.rail.util.RailMapBasic;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.cacpixel.rtmmetro.rail.util.construct.RailConstructTask;
import net.cacpixel.rtmmetro.rail.util.construct.RailProcessThread;
import net.cacpixel.rtmmetro.rail.util.construct.TaskGridConstruct;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class RailMapAdvanced extends RailMapBasic {
    ConcurrentHashMap<Integer, double[]> pointMap = new ConcurrentHashMap<>();
    CopyOnWriteArrayList<int[]> railsConcurrent = new CopyOnWriteArrayList<>();

    public RailMapAdvanced(RailPosition par1, RailPosition par2) {
        super(par1, par2);
    }

    @Override
    protected void createLine() {
        if (RTMMetro.hasFixrtm()) {
            super.createLine();
        } else {
            double d0 = this.startRP.posX;
            double d1 = this.startRP.posY;
            double d2 = this.startRP.posZ;
            double d3 = this.endRP.posX;
            double d4 = this.endRP.posY;
            double d5 = this.endRP.posZ;
            boolean flag = (this.endRP.direction - this.startRP.direction) % 4 == 0;
            boolean flag1 = d2 == d5 || d0 == d3;
            boolean flag2 = Math.abs(d2 - d5) == Math.abs(d0 - d3) && this.startRP.direction % 2 != 0 && this.endRP.direction % 2 != 0;
            boolean isBezier = (startRP.anchorYaw != NGTMath.wrapAngle(startRP.direction * 45.0F)
                    || endRP.anchorYaw != NGTMath.wrapAngle(endRP.direction * 45.0F));
            if ((!flag || !flag1 && !flag2) || isBezier) {
                double d6 = Math.abs(d5 - d2);
                double d7 = Math.abs(d3 - d0);
                double d9 = d6 >= d7 ? d6 : d7;
                double d11 = d6 <= d7 ? d6 : d7;
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
                this.lineHorizontal = new BezierCurve(d2, d0, d2 + d18, d0 + d14, d5 + d15, d3 + d16, d5, d3);
            } else {
                this.lineHorizontal = new StraightLine(d2, d0, d5, d3);
            }

            double d17 = Math.sqrt(NGTMath.pow(d3 - d0, 2) + NGTMath.pow(d5 - d2, 2));
            boolean flag4 = this.startRP.anchorLengthVertical == 0.0F && this.endRP.anchorLengthVertical == 0.0F;
            if (flag4) {
                this.lineVertical = new StraightLine(0.0D, d1, d17, d4);
            } else {
                double d8 = (double) (NGTMath.cos(this.startRP.anchorPitch) * this.startRP.anchorLengthVertical);
                double d10 = (double) (NGTMath.sin(this.startRP.anchorPitch) * this.startRP.anchorLengthVertical);
                double d12 = (double) (NGTMath.cos(this.endRP.anchorPitch) * this.endRP.anchorLengthVertical);
                double d20 = (double) (NGTMath.sin(this.endRP.anchorPitch) * this.endRP.anchorLengthVertical);
                this.lineVertical = new BezierCurve(0.0D, d1, d8, d1 + d10, d17 - d12, d4 + d20, d17, d4);
            }
        }
    }

    public List<int[]> getRailBlockList(ResourceStateRail prop, boolean regenerate, TileEntityMarkerAdvanced marker) {
        if (this.rails.isEmpty() || regenerate) {
            this.createRailList(prop, marker);
        }

        return new ArrayList<>(this.rails);
    }

    protected void createRailList(ResourceStateRail prop, TileEntityMarkerAdvanced marker) {
//        super.createRailList(prop);
        this.rails.clear();
        ModelSetRail modelSet = (ModelSetRail) prop.getResourceSet();
        int halfWidth = ((RailConfig) modelSet.getConfig()).ballastWidth >> 1;
        double halfPi = 1.5707963267948966;
        int split = (int) (this.getLength() * 4.0);
        RailProcessThread thread = RailProcessThread.getInstance();
        int step = 100;
        marker.gridTasks = new TaskGridConstruct[split / step + 1];
        for (int order = 1; order < (split - 1); order += step) {
            marker.gridTasks[order / step] = new TaskGridConstruct(this, prop, order);
            thread.addTask(marker.gridTasks[order / step]);
        }

        for (int j = 1; j < split - 1; ++j) {
            double[] point;
            do {
                point = pointMap.get(j);
            } while (point == null);
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

    public void createRailList0(ResourceStateRail prop, int order) {
        int step = 100;
        int split = (int) (this.getLength() * 4.0);
        Map<Integer, double[]> map = new HashMap<>(128);
        for (int i = 0; (i < step); i++) {
            double[] point = this.getRailPos(split, order + i);
            map.put(order + i, point);
            if (i + order >= split - 1) {
                break;
            }
        }
        pointMap.putAll(map);
    }

    @Override
    protected void addRailBlock(int x, int y, int z) {
        super.addRailBlock(x, y, z);
    }

}
