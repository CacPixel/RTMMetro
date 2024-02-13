package net.cacpixel.rtmmetro.rail.util;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.rail.BlockMarker;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.network.PacketMarkerData;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MarkerManager {

    private final String saveName;
    private final boolean isRemote;
    private final List<MarkerData> markers = new ArrayList<>();
    private MarkerSavedData saveData;

    public MarkerManager(boolean isRemote, String saveName) {
        this.isRemote = isRemote;
        this.saveName = saveName;
    }

    public MarkerManager(boolean isRemote) {
        this(isRemote, "rtmmetro_markers");
    }

    public static MarkerManager getInstance() {
        return RTMMetro.proxy.getMarkerManager();
    }

    private void sendPacket(TileEntityMarkerAdvanced marker, boolean readyToRemove, boolean isRemote) {
        try {
            if (marker != null) {
                if (!isRemote || !marker.getWorld().isRemote)
                    RTMMetro.NETWORK_WRAPPER.sendToAll(new PacketMarkerData(new MarkerData(marker), readyToRemove));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPacket(TileEntityMarkerAdvanced marker, boolean readyToRemove) {
        getInstance().sendPacket(marker, readyToRemove, true);
    }

    public static void sendPacketWithoutCheck(TileEntityMarkerAdvanced marker, boolean readyToRemove) {
        getInstance().sendPacket(marker, readyToRemove, false);
    }

    public void sendMarkersToClient(EntityPlayerMP player) {
        for (MarkerData marker : this.markers) {
            try {
                if (marker != null) {
                    RTMMetro.NETWORK_WRAPPER.sendTo(new PacketMarkerData(marker, false), player);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadData(World world) {
        MarkerSavedData data = (MarkerSavedData) world.loadData(MarkerSavedData.class, saveName);
        if (data == null) {
            data = new MarkerSavedData(saveName);
            world.setData(saveName, data);
        }
        this.saveData = data;
    }

    public List<MarkerData> getMarkerList() {
        return this.markers;
    }

    public TileEntityMarkerAdvanced getMarker(BlockPos pos, World world) {
        for (MarkerData marker : markers) {
            if (marker.getPos().equals(pos) && marker.getWorldName().equals(MarkerManager.getWorldName(world))) {
                return (TileEntityMarkerAdvanced) world.getTileEntity(pos);
            }
        }
        return null;
    }

    public boolean hasMarker(BlockPos pos, World world) {
        for (MarkerData m : markers) {
            if (m.getPos().equals(pos) && m.getWorldName().equals(MarkerManager.getWorldName(world))) {
                return true;
            }
        }
        return false;
    }

    public void createMarker(TileEntityMarkerAdvanced marker) {
        this.createMarker(MarkerManager.getWorldName(marker.getWorld()), marker.getPos());
    }

    public void createMarker(MarkerData marker) {
        this.createMarker(marker.getWorldName(), marker.getPos());
    }

    public void createMarker(String worldName, BlockPos pos) {
        this.markers.removeIf(marker -> {
            return marker.getPos().equals(pos) && marker.getWorldName().equals(worldName);
        });
        this.markers.add(new MarkerData(pos, worldName));
        this.markDirty();
    }

    public void removeMarker(String worldName, BlockPos pos) {
        this.markers.removeIf(marker -> {
            return marker.getPos().equals(pos) && marker.getWorldName().equals(worldName);
        });
        this.markDirty();
    }

    public void removeMarker(World world, List<RailPosition> rps) {
        for (RailPosition rp : rps) {
            BlockPos pos = new BlockPos(rp.blockX, rp.blockY, rp.blockZ);
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityMarkerAdvanced) {
                TileEntityMarkerAdvanced marker = (TileEntityMarkerAdvanced) te;
                this.removeMarker(MarkerManager.getWorldName(marker.getWorld()), marker.getPos());
            }
        }
        this.markDirty();
    }

//    public String getNewId() {
//        return UUID.randomUUID().toString();
//    }

    public void validate(World world) {
        for (Iterator<MarkerData> iterator = markers.iterator(); iterator.hasNext(); ) {
            MarkerData m = iterator.next();
            if (!m.getWorldName().equals(MarkerManager.getWorldName(world))) {
                continue;
            }
            if (!(world.getBlockState(m.getPos()).getBlock() instanceof BlockMarker)) {
                if (!world.isRemote) {
                    NGTLog.debug("[Server] A dead marker on (x=%d, y=%d, z=%d) detected in rtmmetro_markers.dat " +
                                    "and it will be removed on next world save.",
                            m.getX(), m.getY(), m.getZ());
                } else {
                    NGTLog.debug("[Client] A dead marker on (x=%d, y=%d, z=%d) detected in rtmmetro_markers.dat " +
                                    "and it will be removed on next world save on the server side.",
                            m.getX(), m.getY(), m.getZ());
                }
                TileEntityMarkerAdvanced marker = (TileEntityMarkerAdvanced) world.getTileEntity(m.getPos());
                sendPacketWithoutCheck(marker, true);
                iterator.remove();
            }
        }
        this.markDirty();
    }

    public void markDirty() {
        if (!this.isRemote && this.saveData != null) {
            this.saveData.markDirty();
        }
    }

    public void clear() {
        this.markers.clear();
    }

    public static String getWorldName(World w) {
        int dimension = w.provider.getDimension();
        return "dimension" + "_" + dimension;
    }

}
