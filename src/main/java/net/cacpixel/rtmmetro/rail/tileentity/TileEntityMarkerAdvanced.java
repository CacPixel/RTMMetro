package net.cacpixel.rtmmetro.rail.tileentity;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.TileEntityCustom;
import jp.ngt.rtm.gui.InternalButton;
import jp.ngt.rtm.gui.InternalGUI;
import jp.ngt.rtm.item.ItemRail;
import jp.ngt.rtm.rail.util.*;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.network.PacketMarkerRPServer;
import net.cacpixel.rtmmetro.rail.block.BlockMarkerAdvanced;
import net.cacpixel.rtmmetro.rail.util.*;
import net.cacpixel.rtmmetro.rail.util.construct.RailProcessThread;
import net.cacpixel.rtmmetro.rail.util.construct.TaskGridConstruct;
import net.cacpixel.rtmmetro.rail.util.construct.TaskMarkerUpdate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class TileEntityMarkerAdvanced extends TileEntityCustom implements ITickable {

    private static final int SEARCH_COUNT = 40;
    public RailPosition rp;
    public BlockPos startPos;
    private RailMap[] railMaps, prevRailMaps;
    public List<BlockPos> markerPosList = new ArrayList();
    private List<int[]> grid, prevGrid;
    public float startPlayerPitch;
    public float startPlayerYaw;
    public byte startMarkerHeight;
    public int editMode;
    private int markerState;
    @SideOnly(Side.CLIENT)
    public InternalGUI gui;
    @SideOnly(Side.CLIENT)
    public InternalButton[] buttons;
    @SideOnly(Side.CLIENT)
    public float[][][] linePos;
    private int count;
    public boolean shouldUpdateClientLines = false; // 其他玩家修改了Line后置true，发送数据包给所有玩家更新Line
    public RailProcessThread processor;
    public TaskMarkerUpdate task = new TaskMarkerUpdate(this);
    public TaskGridConstruct[] gridTasks;

    public TileEntityMarkerAdvanced() {
        this.markerState = MarkerState.DISTANCE.set(this.markerState, true);
        this.markerState = MarkerState.GRID.set(this.markerState, false);
        this.markerState = MarkerState.LINE1.set(this.markerState, false);
        this.markerState = MarkerState.LINE2.set(this.markerState, false);
        this.markerState = MarkerState.ANCHOR21.set(this.markerState, false);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("RP")) {
            this.rp = RailPosition.readFromNBT(nbt.getCompoundTag("RP"));
        }

    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.rp != null) {
            nbt.setTag("RP", this.rp.writeToNBT());
        }

        return nbt;
    }

    private void updateClientLines() {
        if (!this.world.isRemote) {
            RTMMetro.NETWORK_WRAPPER.sendToAll(new PacketMarkerRPServer(this, this.rp));
        }
        this.shouldUpdateClientLines = false;
    }

    public void update() {
        MarkerManager markerManagerInstance = MarkerManager.getInstance();
        if (!this.getWorld().isRemote) {
            if (!markerManagerInstance.hasMarker(this.getPos(), this.getWorld())) {
                markerManagerInstance.createMarker(this);
                MarkerManager.sendPacket(this, false);
                markerManagerInstance.validate(this.getWorld());
            }
        }

        if (this.rp == null) {
            byte b0 = BlockMarkerAdvanced.getMarkerDir(this.getBlockType(), this.getBlockMetadata());
            byte b1 = (byte) (this.getBlockType() == RTMMetroBlock.MARKER_ADVANCED_SWITCH ? 1 : 0);
            this.rp = new RailPosition(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), b0, b1);
        }

        if (this.shouldUpdateClientLines) {
            this.updateClientLines();
        }

        if (this.getWorld().isRemote) {
            if (processor == null) {
                processor = RailProcessThread.getInstance();
            }
            if (!this.processor.isAlive()) {
                this.processor.start();
            }
            this.updateStartPos();
            if (!this.isCoreMarker()) {
                if (this.getCoreMarker() != null && this.getCoreMarker().task.hasProcessed()) {
                    this.updatePrevData();
                }
            }
            if (task.hasProcessed()) {
                if (this.isCoreMarker() || this.getRailMaps() == null || this.getRailMaps().length < 1) {
                    this.updatePrevData();
                    this.processor.addTask(this.task);
                }
            }
        }
    }

    private void updatePrevData() {
        if (this.railMaps != null) {
            this.prevRailMaps = this.railMaps.clone();
        }

        this.prevGrid = new ArrayList<>();
        if (this.grid != null) {
            this.prevGrid.addAll(this.grid);
        }
    }

    public void searchOtherMarkers() {
        ((BlockMarkerAdvanced) this.getBlockType()).makeRailMap(this, this.getX(), this.getY(), this.getZ());
    }

    private void updateStartPos() {
        if (this.startPos != null) {
            TileEntity tileentity = this.getWorld().getTileEntity(this.startPos);
            if (!(tileentity instanceof TileEntityMarkerAdvanced)) {
                this.startPos = null;
            }
        }
    }

    public RailPosition getMarkerRP() {
        return this.rp;
    }

    public void setMarkerRP(RailPosition par1) {
        this.rp = par1;
    }

    private RailPosition getMarkerRP(BlockPos pos) {
        TileEntity tileentity = BlockUtil.getTileEntity(this.getWorld(), pos);
        return tileentity instanceof TileEntityMarkerAdvanced ? ((TileEntityMarkerAdvanced) tileentity).rp : null;
    }

    public List<int[]> getGrid() {
        return this.grid;
    }

    public List<int[]> getPrevGrid() {
        return this.prevGrid;
    }

    public RailMap[] getRailMaps() {
        return this.railMaps;
    }

    public RailMap[] getPrevRailMaps() {
        return this.prevRailMaps;
    }

    public void onChangeRailShape() {
        if (!this.isCoreMarker()) {
            TileEntityMarkerAdvanced TileEntityMarkerAdvanced = this.getCoreMarker();
            if (TileEntityMarkerAdvanced != null) {
                TileEntityMarkerAdvanced.onChangeRailShape();
            }
        } else {
            RailMap[] arailmap = new RailMapAdvanced[this.railMaps.length];

            for (int i = 0; i < arailmap.length; ++i) {
                RailPosition railposition = this.railMaps[i].getStartRP();
                RailPosition railposition1 = this.railMaps[i].getEndRP();
                railposition1.cantCenter = -railposition.cantCenter;
                arailmap[i] = new RailMapAdvanced(railposition, railposition1);
            }

            this.railMaps = arailmap;
//            this.linePos = (float[][][]) null;
            this.createGrids();

            for (BlockPos blockpos : this.markerPosList) {
                TileEntity tileentity = BlockUtil.getTileEntity(this.getWorld(), blockpos);
                if (tileentity instanceof TileEntityMarkerAdvanced) {
                    TileEntityMarkerAdvanced TileEntityMarkerAdvanced1 = (TileEntityMarkerAdvanced) tileentity;
                    TileEntityMarkerAdvanced1.railMaps = arailmap;
                }
            }
        }

    }

    public void setMarkersPos(List<BlockPos> list) {
        BlockPos blockpos = null;
        if (list.size() == 1) {
            RailPosition railposition = this.getMarkerRP((BlockPos) list.get(0));
            if (railposition != null && railposition.hasScript()) {
//                RailMapAdvanced railmap = new RailMapCustom(railposition, railposition.scriptName, railposition.scriptArgs);
//                this.railMaps = new RailMapAdvanced[]{railmap};
                blockpos = new BlockPos(railposition.blockX, railposition.blockY, railposition.blockZ);
            } else {
                this.railMaps = new RailMapAdvanced[]{};
            }
        } else if (list.size() == 2) {
            if (list.get(0) != null && list.get(1) != null) {
                RailPosition railposition2 = this.getMarkerRP((BlockPos) list.get(0));
                RailPosition railposition3 = this.getMarkerRP((BlockPos) list.get(1));
                if (railposition2 != null && railposition3 != null) {
                    RailMapAdvanced railmap1 = new RailMapAdvanced(railposition2, railposition3);
                    this.railMaps = new RailMapAdvanced[]{railmap1};
                    blockpos = new BlockPos(railposition2.blockX, railposition2.blockY, railposition2.blockZ);
                }
            }
        } else {
            List<RailPosition> list2 = new ArrayList<>();

            for (BlockPos blockpos2 : list) {
                RailPosition railposition1 = this.getMarkerRP(blockpos2);
                if (railposition1 != null) {
                    list2.add(railposition1);
                }
            }

            SwitchType switchtype = (new RailMaker(this.getWorld(), list2)).getSwitch();
            if (switchtype != null) {
                // Todo: 使用新的SwitchType或者依次手动clone railMaps为RailMapSwitchAdvanced (RailMapSwitch final class，临时解决方案)

                this.railMaps = switchtype.getAllRailMap();
                if (this.railMaps != null) {
                    RailPosition railposition4 = this.railMaps[0].getStartRP();
                    blockpos = new BlockPos(railposition4.blockX, railposition4.blockY, railposition4.blockZ);
                }
            }
        }

        if (this.railMaps != null) {
            this.markerPosList = list;
//            this.createGrids();
            if (blockpos != null) {
                for (int i = 0; i < list.size(); ++i) {
                    BlockPos blockpos1 = (BlockPos) list.get(i);
                    TileEntity tileentity = BlockUtil.getTileEntity(this.getWorld(), blockpos1);
                    if (tileentity instanceof TileEntityMarkerAdvanced) {
                        TileEntityMarkerAdvanced ma = (TileEntityMarkerAdvanced) tileentity;
                        ma.setStartPos(blockpos, this.railMaps);
                    }
                }
            }

        }
    }

    private void createGrids() {
        this.grid = new ArrayList<>();
        try {
            for (RailMap railmap : this.railMaps) {
                if (this.railMaps instanceof RailMapAdvanced[]) {
                    this.grid.addAll(((RailMapAdvanced) railmap).getRailBlockList(ItemRail.getDefaultProperty(), true, this));
                } else {
                    // Todo：暂时不支持道岔的Grid生成多线程优化
                    this.grid.addAll(railmap.getRailBlockList(ItemRail.getDefaultProperty(), true));
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setStartPos(BlockPos pos, RailMap[] maps) {
//        NGTLog.debug("[Marker] Start pos %s", new Object[]{pos.toString()});
        this.startPos = pos;
        this.railMaps = maps;
        if (!this.isCoreMarker()) {
            this.markerPosList.clear();
            this.grid = null;
        }
    }

    public boolean isCoreMarker() {
        if (this.startPos == null) {
            return false;
        } else {
            return this.startPos.getX() == this.getX() && this.startPos.getY() == this.getY() && this.startPos.getZ() == this.getZ();
        }
    }

    public TileEntityMarkerAdvanced getCoreMarker() {
        if (this.startPos == null) {
            return null;
        } else {
            TileEntity tileentity = BlockUtil.getTileEntity(this.getWorld(), this.startPos);
            return tileentity instanceof TileEntityMarkerAdvanced ? (TileEntityMarkerAdvanced) tileentity : null;
        }
    }

    public RailPosition[] getAllRP() {
        if (this.markerPosList.isEmpty()) {
            return new RailPosition[]{this.rp};
        } else {
            List<RailPosition> list = new ArrayList<>();

            for (BlockPos blockpos : this.markerPosList) {
                RailPosition railposition = this.getMarkerRP(blockpos);
                if (railposition != null) {
                    list.add(railposition);
                }
            }

            return list.toArray(new RailPosition[list.size()]);
        }
    }

    public boolean getState(MarkerState state) {
        return state.get(this.markerState);
    }

    public void flipState(MarkerState state) {
        boolean flag = state.get(this.markerState);
        this.setState(state, !flag);
    }

    public void setState(MarkerState state, boolean data) {
        if (!this.isCoreMarker()) {
            TileEntityMarkerAdvanced coreMarker = this.getCoreMarker();
            if (coreMarker != null) {
                coreMarker.setState(state, data);
            } else if (state == MarkerState.DISTANCE) {
                this.markerState = state.set(this.markerState, data);
            }
        } else {
            this.markerState = state.set(this.markerState, data);

            for (BlockPos blockpos : this.markerPosList) {
                TileEntity tileentity = BlockUtil.getTileEntity(this.getWorld(), blockpos);
                if (tileentity instanceof TileEntityMarkerAdvanced) {
                    TileEntityMarkerAdvanced TileEntityMarkerAdvanced1 = (TileEntityMarkerAdvanced) tileentity;
                    TileEntityMarkerAdvanced1.markerState = this.markerState;
                }
            }
        }

    }

    public String getStateString(MarkerState state) {
        boolean flag = state.get(this.markerState);
        return String.format("%s : %s", state.toString(), flag ? "ON" : "OFF");
    }

}
