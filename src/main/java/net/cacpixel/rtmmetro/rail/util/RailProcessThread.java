package net.cacpixel.rtmmetro.rail.util;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.io.NGTLog;
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.minecraft.block.Block;

public class RailProcessThread extends Thread {
    private final TileEntityMarkerAdvanced marker;
    private boolean isBelongToServer;
    private boolean loop = true;
    public boolean startProcess = false;
    private static int id = 0;
    private long duration = (ModConfig.useFastMarkerSearchMethod) ? 4 * 50 : 40 * 50; // 转化为tick(tps=20)

    public RailProcessThread(TileEntityMarkerAdvanced marker, boolean isServer) {
        super("Rail Process Thread " + (isServer ? "Server " : "Client ") + id++);
        this.marker = marker;
        this.isBelongToServer = isServer;
    }

    @Override
    public void run() {
        NGTLog.debug("Thread Start.");
        while (this.loop) {
            if (!this.startProcess) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Block block = BlockUtil.getBlock(marker.getWorld(), marker.getPos());
                if (block != RTMMetroBlock.MARKER_ADVANCED && block != RTMMetroBlock.MARKER_ADVANCED_SWITCH) {
                    break;
                }
                continue;
            }
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
            this.startProcess = false;
        }
        NGTLog.debug("Thread Exit.");
    }

    public long getDuration() {
        return duration;
    }

    public void endLoop() {
        this.loop = false;
    }
}
