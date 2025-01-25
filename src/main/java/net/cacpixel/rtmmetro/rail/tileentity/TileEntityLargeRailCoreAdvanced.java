package net.cacpixel.rtmmetro.rail.tileentity;

import jp.ngt.rtm.rail.TileEntityLargeRailNormalCore;
import jp.ngt.rtm.rail.util.RailMap;
import net.cacpixel.rtmmetro.rail.component.IRailComponent;
import net.cacpixel.rtmmetro.rail.util.RailMapAdvanced;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class TileEntityLargeRailCoreAdvanced extends TileEntityLargeRailNormalCore
{
    public List<IRailComponent> railComponents = new ArrayList<>();
    public boolean isRailBreaking = false;
    protected RailMapAdvanced railMapAdvanced;
    public boolean isFirstLoad = true;

//    @Override
//    public void update() {
////        super.update();
//        if (this.isFirstLoad) {
//            this.isFirstLoad = false;
//            if (this.getWorld().isRemote) {
//                this.shouldRerenderRail = false;
//            }
//        }
//    }

    @Override
    public void createRailMap()
    {
        if (this.isLoaded())
        {
            this.railMapAdvanced = new RailMapAdvanced(this.railPositions[0], this.railPositions[1]);
        }
    }

    @Override
    public RailMap getRailMap(Entity entity)
    {
        if (this.railMapAdvanced == null)
        {
            this.createRailMap();
        }
        return this.railMapAdvanced;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        this.readAdditionalData(tag);
    }

    public void readAdditionalData(NBTTagCompound tag)
    {
        if (tag.hasKey("RTMMetroAdditional"))
        {

        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        this.writeAdditionalData(tag);
        return tag;
    }

    public void writeAdditionalData(NBTTagCompound tag)
    {

    }

}
