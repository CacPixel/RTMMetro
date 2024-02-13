package net.cacpixel.rtmmetro.rail.util;

import jp.ngt.rtm.rail.util.RailDir;
import jp.ngt.rtm.rail.util.RailPosition;

public class RailMapSwitchAdvanced extends RailMapAdvanced {
    public final RailDir startDir;
    public final RailDir endDir;
    private boolean isOpen;

    public RailMapSwitchAdvanced(RailPosition par1, RailPosition par2, RailDir sDir, RailDir eDir) {
        super(par1, par2);
        this.startDir = sDir;
        this.endDir = eDir;
    }

    /** @deprecated */
    @Deprecated
    public RailMapSwitchAdvanced setState(boolean par1) {
        this.isOpen = par1;
        return this;
    }
}
