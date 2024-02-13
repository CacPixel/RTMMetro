package net.cacpixel.rtmmetro.rail.util;

import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.minecraft.util.math.BlockPos;

public class MarkerData {
    private final BlockPos pos;
    private final String worldName;

    public MarkerData(BlockPos pos, String worldName) {
        this.pos = pos;
        this.worldName = worldName;
    }

    public MarkerData(TileEntityMarkerAdvanced marker) {
        this.pos = marker.getPos();
        this.worldName = MarkerManager.getWorldName(marker.getWorld());
    }

    public int getX() {
        return pos.getX();
    }

    public int getY() {
        return pos.getY();
    }

    public int getZ() {
        return pos.getZ();
    }

    public String getWorldName() {
        return this.worldName;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}
