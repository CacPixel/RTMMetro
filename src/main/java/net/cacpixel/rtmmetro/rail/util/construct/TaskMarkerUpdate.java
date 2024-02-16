package net.cacpixel.rtmmetro.rail.util.construct;

import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;

public class TaskMarkerUpdate extends RailConstructTask {
    public TileEntityMarkerAdvanced marker;

    public TaskMarkerUpdate(TileEntityMarkerAdvanced marker) {
        super();
        this.marker = marker;
    }

    @Override
    public void runTask() {
        marker.searchOtherMarkers();
        marker.onChangeRailShape();
        this.complete();
    }
}
