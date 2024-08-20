package net.cacpixel.rtmmetro.rail.util.construct;

import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;

public class InitNPTask extends RailConstructTask {
    private final BezierCurveAdvanced curve;
    private final float[] afloat;
    private final int index;
    private final int step;

    public InitNPTask(BezierCurveAdvanced curve, float[] afloat, int index, int step) {
        super();
        this.curve = curve;
        this.index = index;
        this.afloat = afloat;
        this.step = step;
    }

    @Override
    public void runTask() {
        curve.normalizeParams(afloat, index, step);
    }
}
