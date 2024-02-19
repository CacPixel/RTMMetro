package net.cacpixel.rtmmetro.rail.util;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.BezierCurve;
import jp.ngt.ngtlib.math.ILine;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.StraightLine;
import jp.ngt.rtm.modelpack.cfg.RailConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetRail;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.rail.util.RailMapBasic;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.cacpixel.rtmmetro.rail.util.construct.RailProcessThread;
import net.cacpixel.rtmmetro.rail.util.construct.TaskInitNP;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
}
