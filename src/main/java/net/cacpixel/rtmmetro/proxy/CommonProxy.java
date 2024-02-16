package net.cacpixel.rtmmetro.proxy;

import net.cacpixel.rtmmetro.rail.util.MarkerManager;
import net.cacpixel.rtmmetro.rail.util.construct.RailProcessThread;

public class CommonProxy {
    private final MarkerManager mm = new MarkerManager(false);
    public RailProcessThread railProcessThread = new RailProcessThread(false);

    public CommonProxy() {
    }

    public void preInit() {
    }

    public void init() {
    }

    public void complete() {
    }

    public MarkerManager getMarkerManager()
    {
        return this.mm;
    }

    public RailProcessThread getRailProcessThread() {
        return this.railProcessThread;
    }

}
