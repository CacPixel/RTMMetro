package net.cacpixel.rtmmetro.rail.tileentity;

import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class TileEntityLargeRailMainCoreAdvanced extends TileEntityLargeRailSubCoreAdvanced {
    List<TileEntityLargeRailSubCoreAdvanced> subCores = new ArrayList<>();

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.readAdditionalData(tag);
    }

    public void readAdditionalData(NBTTagCompound tag) {
        super.readAdditionalData(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.writeAdditionalData(tag);
        return tag;
    }

    public void writeAdditionalData(NBTTagCompound tag) {
        super.writeAdditionalData(tag);
    }

}
