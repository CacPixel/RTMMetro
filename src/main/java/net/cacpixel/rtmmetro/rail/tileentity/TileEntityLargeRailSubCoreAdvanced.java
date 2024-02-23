package net.cacpixel.rtmmetro.rail.tileentity;

import jp.ngt.rtm.rail.TileEntityLargeRailNormalCore;
import jp.ngt.rtm.rail.util.RailMap;
import net.cacpixel.rtmmetro.rail.component.IRailComponent;
import net.cacpixel.rtmmetro.rail.util.RailMapAdvanced;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class TileEntityLargeRailSubCoreAdvanced extends TileEntityLargeRailNormalCore {

    private TileEntityLargeRailMainCoreAdvanced mainCore;
    public List<IRailComponent> railComponents = new ArrayList<>();
    public boolean isRailBreaking = false;
    protected RailMapAdvanced railMapAdvanced;
    public boolean isFirstLoad = true;

    public TileEntityLargeRailMainCoreAdvanced getMainCore() {
        return this.mainCore;
    }

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

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.readAdditionalData(tag);
    }

    public void readAdditionalData(NBTTagCompound tag) {
        if (tag.hasKey("RTMMetroAdditional")) {
//            NBTTagCompound additional = tag.getCompoundTag("RTMMetroAdditional");
//            if (tag.hasKey("StartRP")) {
//                NBTTagCompound startRP = additional.getCompoundTag("StartRP");
//                this.railPositions[0].posX = startRP.getDouble("AccuratePosX");
//                this.railPositions[0].posY = startRP.getDouble("AccuratePosY");
//                this.railPositions[0].posZ = startRP.getDouble("AccuratePosZ");
//            }
//            if (tag.hasKey("EndRP")) {
//                NBTTagCompound endRP = additional.getCompoundTag("EndRP");
//                this.railPositions[1].posX = endRP.getDouble("AccuratePosX");
//                this.railPositions[1].posY = endRP.getDouble("AccuratePosY");
//                this.railPositions[1].posZ = endRP.getDouble("AccuratePosZ");
//            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.writeAdditionalData(tag);
        return tag;
    }

    public void writeAdditionalData(NBTTagCompound tag) {
//        NBTTagCompound additional = new NBTTagCompound();
//
//        NBTTagCompound startRP = new NBTTagCompound();
//        startRP.setDouble("AccuratePosX", this.railPositions[0].posX);
//        startRP.setDouble("AccuratePosY", this.railPositions[0].posY);
//        startRP.setDouble("AccuratePosZ", this.railPositions[0].posZ);
//        additional.setTag("StartRP", startRP);
//
//        NBTTagCompound endRP = new NBTTagCompound();
//        endRP.setDouble("AccuratePosX", this.railPositions[1].posX);
//        endRP.setDouble("AccuratePosY", this.railPositions[1].posY);
//        endRP.setDouble("AccuratePosZ", this.railPositions[1].posZ);
//        additional.setTag("EndRP", endRP);
//
//        tag.setTag("RTMMetroAdditional", additional);
    }

}
