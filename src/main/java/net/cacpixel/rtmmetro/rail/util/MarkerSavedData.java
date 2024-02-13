package net.cacpixel.rtmmetro.rail.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.WorldSavedData;

import java.util.List;

public final class MarkerSavedData extends WorldSavedData {

    public MarkerSavedData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        MarkerManager.getInstance().clear();
        NBTTagList tagList = nbt.getTagList("RTMMetroAdvancedMarkers", 10);

        for (int i = 0; i <= tagList.tagCount(); ++i) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            if (tag.hasNoTags()) {
                continue;
            }
            String str = tag.getString("worldName");
            int x = tag.getInteger("x");
            int y = tag.getInteger("y");
            int z = tag.getInteger("z");
            MarkerData marker = new MarkerData(new BlockPos(x, y, z), str);
            MarkerManager.getInstance().createMarker(marker);
            //sendPacketWithoutCheck(marker, false);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList tagList = new NBTTagList();
        List<MarkerData> markers = MarkerManager.getInstance().getMarkerList();
        for (MarkerData marker : markers) {
            NBTTagCompound tag = new NBTTagCompound();
            String str = marker.getWorldName();
            tag.setString("worldName", str);
            tag.setInteger("x", marker.getX());
            tag.setInteger("y", marker.getY());
            tag.setInteger("z", marker.getZ());
            tagList.appendTag(tag);

        }
        nbt.setTag("RTMMetroAdvancedMarkers", tagList);
        return nbt;
    }


}

