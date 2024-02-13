package net.cacpixel.rtmmetro.rail.tileentity;

import jp.ngt.rtm.rail.TileEntityLargeRailNormalCore;
import jp.ngt.rtm.rail.util.RailMap;
import net.cacpixel.rtmmetro.rail.component.IRailComponent;
import net.cacpixel.rtmmetro.rail.util.RailMapAdvanced;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class TileEntityLargeRailSubCoreAdvanced extends TileEntityLargeRailNormalCore {

    private TileEntityLargeRailMainCoreAdvanced mainCore;
    public List<IRailComponent> railComponents = new ArrayList<>();
    public boolean isRailBreaking = false;
    protected RailMapAdvanced railMapAdvanced;

    public TileEntityLargeRailMainCoreAdvanced getMainCore() {
        return this.mainCore;
    }

    @Override
    public void createRailMap() {
        if (this.isLoaded()) {
            this.railMapAdvanced = new RailMapAdvanced(this.railPositions[0], this.railPositions[1]);
        }
    }

    @Override
    public RailMap getRailMap(Entity entity) {
        if (this.railMapAdvanced == null) {
            this.createRailMap();
        }
        return this.railMapAdvanced;
    }

}
