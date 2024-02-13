package net.cacpixel.rtmmetro.render;

import jp.ngt.rtm.rail.RenderLargeRail;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;

public class RenderLargeRailAdvanced extends RenderLargeRail {
    public static final RenderLargeRailAdvanced INSTANCE = new RenderLargeRailAdvanced();

    public RenderLargeRailAdvanced() {
        super();
    }

    public void render(TileEntityLargeRailCore tileEntity, double d0, double d1, double d2, float f, int i, float alpha) {
        try {
            super.render(tileEntity, d0, d1, d2, f, i, alpha);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
