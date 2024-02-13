package net.cacpixel.rtmmetro.rail.util;

import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;

public class RailProcessThread extends Thread {
    private final TileEntityMarkerAdvanced marker;
    private boolean isBelongToServer;
    private long duration = (ModConfig.useFastMarkerSearchMethod) ? 4 * 50 : 40 * 50; // 转化为tick(tps=20)

    public RailProcessThread(TileEntityMarkerAdvanced marker, boolean isServer) {
        super("0Rail Process Thread " + (isServer ? "Server" : "Client"));
        this.marker = marker;
        this.isBelongToServer = isServer;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try {
            marker.searchOtherMarkers();
            marker.onChangeRailShape();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        this.duration = System.currentTimeMillis() - startTime;
//        NGTLog.debug("Thread run elapsed: " + this.duration / 1000.0f + " sec");
        if (((int) Math.floor(this.duration / 50.0f) + 1) > TileEntityMarkerAdvanced.STANDARD_INTERVAL) {
            this.marker.refreshTicksInterval = ((int) Math.floor(this.duration / 50.0f) + 1) * ModConfig.railProcessThreadSleepTime;
        } else {
            this.marker.refreshTicksInterval = TileEntityMarkerAdvanced.STANDARD_INTERVAL;
        }
    }

    public long getDuration() {
        return duration;
    }
}
