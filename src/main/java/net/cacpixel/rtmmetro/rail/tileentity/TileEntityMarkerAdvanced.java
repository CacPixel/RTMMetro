package net.cacpixel.rtmmetro.rail.tileentity;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.TileEntityCustom;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.gui.InternalButton;
import jp.ngt.rtm.gui.InternalGUI;
import jp.ngt.rtm.item.ItemRail;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import jp.ngt.rtm.rail.util.*;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.network.PacketMarkerServer;
import net.cacpixel.rtmmetro.rail.block.BlockMarkerAdvanced;
import net.cacpixel.rtmmetro.rail.util.AnchorEditStatus;
import net.cacpixel.rtmmetro.rail.util.MarkerManager;
import net.cacpixel.rtmmetro.rail.util.RailDrawingScheme;
import net.cacpixel.rtmmetro.rail.util.RailMapAdvanced;
import net.cacpixel.rtmmetro.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TileEntityMarkerAdvanced extends TileEntityCustom implements ITickable
{
    private static final int SEARCH_COUNT = 40;
    public RailPosition rp;
    public BlockPos startPos;
    private RailMap[] railMaps;
    private RailMapAdvanced originalRailMap; // don't use when switch rail
    public List<BlockPos> markerPosList = new ArrayList();
    private List<int[]> grid;
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
    public int splits = 2;
    /******************************************************************/
    public static final int DEFAULT_GROUP_ID = 1;
    public static final int GROUP_ID_INDIVIDUAL = 0;
    public static final String DEFAULT_NAME = "marker";
    private int groupId; // groupId=0 则为未分组的marker
    private String name; // 名字，最好是唯一的
    public RailDrawingScheme drawingScheme; // 画轨方案，默认 RTM_DEFAULT
    public AnchorEditStatus editStatusH; // 绿线编辑状态，默认 FOLLOW_NEIGHBOR
    public AnchorEditStatus editStatusV;

    /******************************************************************/

    public TileEntityMarkerAdvanced()
    {
        this.name = DEFAULT_NAME + (MarkerManager.getInstance().getMarkerList().size() + 1);
        this.groupId = DEFAULT_GROUP_ID;
        this.drawingScheme = RailDrawingScheme.RTM_DEFAULT;
        this.editStatusH = AnchorEditStatus.FOLLOW_NEIGHBOR;
        this.editStatusV = AnchorEditStatus.FOLLOW_NEIGHBOR;
        this.markerState = MarkerState.DISTANCE.set(this.markerState, true);
        this.markerState = MarkerState.GRID.set(this.markerState, false);
        this.markerState = MarkerState.LINE1.set(this.markerState, false);
        this.markerState = MarkerState.LINE2.set(this.markerState, false);
        this.markerState = MarkerState.ANCHOR21.set(this.markerState, false);
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.name = nbt.getString("Name");
        this.groupId = nbt.getInteger("GroupId");
        this.drawingScheme = RailDrawingScheme.get(nbt.getInteger("RailDrawingScheme"));
        this.editStatusH = AnchorEditStatus.get(nbt.getInteger("AnchorEditStatusH"));
        this.editStatusV = AnchorEditStatus.get(nbt.getInteger("AnchorEditStatusV"));
        if (nbt.hasKey("RP"))
        {
            this.rp = RailPosition.readFromNBT(nbt.getCompoundTag("RP"));
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setString("Name", this.name);
        nbt.setInteger("GroupId", this.groupId);
        nbt.setInteger("RailDrawingScheme", this.drawingScheme.ordinal());
        nbt.setInteger("AnchorEditStatusH", this.editStatusH.ordinal());
        nbt.setInteger("AnchorEditStatusV", this.editStatusV.ordinal());
        if (this.rp != null)
        {
            nbt.setTag("RP", this.rp.writeToNBT());
        }
        return nbt;
    }

    private void updateClientLines()
    {
        if (this.shouldUpdateClientLines)
        {
            RTMMetro.NETWORK_WRAPPER.sendToAllAround(new PacketMarkerServer(this, this.rp),
                    new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.getX(), this.getY(), this.getZ(),
                            (RTMMetro.proxy.getViewDistance() + 3) * 16));
        }
        this.shouldUpdateClientLines = false;
    }

    public void update()
    {
        MarkerManager markerManagerInstance = MarkerManager.getInstance();
        if (!this.getWorld().isRemote)
        {
            if (!markerManagerInstance.hasMarker(this.getPos(), this.getWorld()))
            {
                markerManagerInstance.createMarker(this);
                MarkerManager.sendPacket(this, false);
                markerManagerInstance.validate(this.getWorld());
            }
        }

        if (this.rp == null)
        {
            byte b0 = BlockMarkerAdvanced.getMarkerDir(this.getBlockType(), this.getBlockMetadata());
            byte b1 = (byte) (this.getBlockType() == RTMMetroBlock.MARKER_ADVANCED_SWITCH ? 1 : 0);
            this.rp = new RailPosition(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), b0, b1);
        }

        if (this.getWorld().isRemote)
        {
            this.updateStartPos();

            if (this.isCoreMarker() || this.getRailMaps() == null || this.getRailMaps().length < 1)
            {
                this.searchOtherMarkers();
                this.onChangeRailShape();
            }
        }
        if (!this.world.isRemote)
        {
            this.updateClientLines(); // Server only
        }
    }

    public void searchOtherMarkers()
    {
        Block block = this.getBlockType();
        if (block instanceof BlockMarkerAdvanced)
        {
            ((BlockMarkerAdvanced) this.getBlockType()).makeRailMap(this, this.getX(), this.getY(), this.getZ());
        }
    }

    private void updateStartPos()
    {
        if (this.startPos != null)
        {
            TileEntity tileentity = this.getWorld().getTileEntity(this.startPos);
            if (!(tileentity instanceof TileEntityMarkerAdvanced))
            {
                this.startPos = null;
            }
        }
    }

    public RailPosition getMarkerRP()
    {
        return this.rp;
    }

    public void setMarkerRP(RailPosition par1)
    {
        this.rp = par1;
    }

    public RailPosition getMarkerRP(BlockPos pos)
    {
        TileEntity tileentity = BlockUtil.getTileEntity(this.getWorld(), pos);
        return tileentity instanceof TileEntityMarkerAdvanced ? ((TileEntityMarkerAdvanced) tileentity).rp : null;
    }

    public List<int[]> getGrid()
    {
        return this.grid;
    }

    public RailMap[] getRailMaps()
    {
        return this.railMaps;
    }

    public void onChangeRailShape()
    {
        if (!this.isCoreMarker())
        {
            TileEntityMarkerAdvanced core = this.getCoreMarker();
            if (core != null)
            {
                core.onChangeRailShape();
            }
        }
        else
        {
            RailMap[] arailmap = new RailMapAdvanced[this.railMaps.length];
            for (int i = 0; i < arailmap.length; ++i)
            {
                RailPosition startRP = this.railMaps[i].getStartRP();
                RailPosition endRP = this.railMaps[i].getEndRP();
                endRP.cantCenter = -startRP.cantCenter;
                arailmap[i] = new RailMapAdvanced(startRP, endRP);
            }
            this.railMaps = arailmap;
//            this.linePos = (float[][][]) null;
            this.createGrids();

            for (BlockPos blockpos : this.markerPosList)
            {
                TileEntity tileentity = BlockUtil.getTileEntity(this.getWorld(), blockpos);
                if (tileentity instanceof TileEntityMarkerAdvanced)
                {
                    TileEntityMarkerAdvanced marker = (TileEntityMarkerAdvanced) tileentity;
                    marker.railMaps = arailmap;
                }
            }
        }

    }

    /**
     *  只有core marker才会调用这个方法
     */
    public void setMarkersPos(List<BlockPos> list)
    {
        BlockPos blockpos = null;
        if (list.size() == 1)
        {
            RailPosition railposition = this.getMarkerRP((BlockPos) list.get(0));
            if (railposition != null && railposition.hasScript())
            {
//                RailMapAdvanced railmap = new RailMapCustom(railposition, railposition.scriptName, railposition
//                .scriptArgs);
//                this.railMaps = new RailMapAdvanced[]{railmap};
                blockpos = new BlockPos(railposition.blockX, railposition.blockY, railposition.blockZ);
            }
            else
            {
                this.railMaps = new RailMapAdvanced[]{};
            }
        }
        else if (list.size() == 2)
        {
            if (list.get(0) != null && list.get(1) != null)
            {
                RailPosition startRP = this.getMarkerRP((BlockPos) list.get(0));
                RailPosition endRP = this.getMarkerRP((BlockPos) list.get(1));
                if (startRP != null && endRP != null)
                {
                    List<RailMapAdvanced> rms = new ArrayList<>();
                    originalRailMap = new RailMapAdvanced(startRP, endRP);
                    blockpos = new BlockPos(startRP.blockX, startRP.blockY, startRP.blockZ);
                    int split = 2;
                    RailMapAdvanced next = originalRailMap;
                    int length = (int) Math.floor(originalRailMap.getLength()) * 2;
                    for (int i = 0; i < split; i++)
                    {
                        int order = length / split * (i + 1);
                        RailMapAdvanced railmap;
                        if (i == split - 1)
                        {
                            rms.add(next);
                            break;
                        }
                        else
                        {
                            List<RailMapAdvanced> railMaps = next.split(length, order);
                            if (railMaps.size() == 1)
                            {
                                rms.add(next);
                                break;
                            }
                            railmap = railMaps.get(0);
                            try
                            {
                                next = railMaps.get(1);
                            }
                            catch (ArrayIndexOutOfBoundsException e)
                            {
                                e.printStackTrace();
                            }
                            rms.add(railmap);
                        }
                    }
                    this.railMaps = new RailMapAdvanced[rms.size()];
                    rms.toArray(this.railMaps);
                }
            }
        }
        else
        {
            List<RailPosition> list2 = new ArrayList<>();

            for (BlockPos blockpos2 : list)
            {
                RailPosition railposition1 = this.getMarkerRP(blockpos2);
                if (railposition1 != null)
                {
                    list2.add(railposition1);
                }
            }

            SwitchType switchtype = (new RailMaker(this.getWorld(), list2)).getSwitch();
            if (switchtype != null)
            {
                // Todo: 使用新的SwitchType或者依次手动clone railMaps为RailMapSwitchAdvanced (RailMapSwitch final class，临时解决方案)

                this.railMaps = switchtype.getAllRailMap();
                if (this.railMaps != null)
                {
                    RailPosition railposition4 = this.railMaps[0].getStartRP();
                    blockpos = new BlockPos(railposition4.blockX, railposition4.blockY, railposition4.blockZ);
                }
            }
        }

        if (this.railMaps != null)
        {
            this.markerPosList.clear();
            this.markerPosList.addAll(list);
//            this.createGrids();
            if (blockpos != null)
            {
                for (int i = 0; i < list.size(); ++i)
                {
                    BlockPos blockpos1 = (BlockPos) list.get(i);
                    TileEntity tileentity = BlockUtil.getTileEntity(this.getWorld(), blockpos1);
                    if (tileentity instanceof TileEntityMarkerAdvanced)
                    {
                        TileEntityMarkerAdvanced ma = (TileEntityMarkerAdvanced) tileentity;
                        ma.setStartPos(blockpos, this.railMaps, list);
                        ma.originalRailMap = this.getOriginalRailMap();
                    }
                }
            }
        }
    }

    private void setStartPos(BlockPos pos, RailMap[] maps, List<BlockPos> list)
    {
        this.startPos = pos;
        this.railMaps = maps;
        if (!this.isCoreMarker())
        {
            this.grid = null;
            this.markerPosList.clear();
            this.markerPosList.addAll(list);
        }
    }

    private void createGrids()
    {
        this.grid = new ArrayList<>();
        for (RailMap railmap : this.railMaps)
        {
            this.grid.addAll(((RailMapAdvanced) railmap).getRailBlockList(ItemRail.getDefaultProperty(), true));
        }
    }

    public boolean isCoreMarker()
    {
        if (this.startPos == null)
        {
            return false;
        }
        else
        {
            return this.startPos.getX() == this.getX() && this.startPos.getY() == this.getY() &&
                    this.startPos.getZ() == this.getZ();
        }
    }

    public TileEntityMarkerAdvanced getCoreMarker()
    {
        if (this.startPos == null)
        {
            return null;
        }
        else
        {
            TileEntity tileentity = BlockUtil.getTileEntity(this.getWorld(), this.startPos);
            return tileentity instanceof TileEntityMarkerAdvanced ? (TileEntityMarkerAdvanced) tileentity : null;
        }
    }

    public RailPosition[] getAllRP()
    {
        if (this.markerPosList.isEmpty())
        {
            return new RailPosition[]{this.rp};
        }
        else
        {
            List<RailPosition> list = new ArrayList<>();

            for (BlockPos blockpos : this.markerPosList)
            {
                RailPosition railposition = this.getMarkerRP(blockpos);
                if (railposition != null)
                {
                    list.add(railposition);
                }
            }

            return list.toArray(new RailPosition[list.size()]);
        }
    }

    public List<RailPosition> getAllRPList()
    {
        if (this.markerPosList.isEmpty())
        {
            return Stream.of(this.rp).collect(Collectors.toList());
        }
        else
        {
            List<RailPosition> list = new ArrayList<>();

            for (BlockPos blockpos : this.markerPosList)
            {
                RailPosition railposition = this.getMarkerRP(blockpos);
                if (railposition != null)
                {
                    list.add(railposition);
                }
            }
            return list;
        }
    }

    public boolean getState(MarkerState state)
    {
        return state.get(this.markerState);
    }

    public void flipState(MarkerState state)
    {
        boolean flag = state.get(this.markerState);
        this.setState(state, !flag);
    }

    public void setState(MarkerState state, boolean data)
    {
        if (!this.isCoreMarker())
        {
            TileEntityMarkerAdvanced coreMarker = this.getCoreMarker();
            if (coreMarker != null)
            {
                coreMarker.setState(state, data);
            }
            else if (state == MarkerState.DISTANCE)
            {
                this.markerState = state.set(this.markerState, data);
            }
        }
        else
        {
            this.markerState = state.set(this.markerState, data);

            for (BlockPos blockpos : this.markerPosList)
            {
                TileEntity tileentity = BlockUtil.getTileEntity(this.getWorld(), blockpos);
                if (tileentity instanceof TileEntityMarkerAdvanced)
                {
                    TileEntityMarkerAdvanced TileEntityMarkerAdvanced1 = (TileEntityMarkerAdvanced) tileentity;
                    TileEntityMarkerAdvanced1.markerState = this.markerState;
                }
            }
        }

    }

    public String getStateString(MarkerState state)
    {
        boolean flag = state.get(this.markerState);
        return String.format("%s : %s", state.toString(), flag ? "ON" : "OFF");
    }

    public static TileEntityMarkerAdvanced getNeighborMarker(World world, RailPosition rp)
    {
        TileEntityMarkerAdvanced marker = BlockUtils.getMarkerFromPos(world, rp);
        if (marker != null)
        {
            return getNeighborMarker(marker);
        }
        return null;
    }

    public static TileEntityMarkerAdvanced getNeighborMarker(TileEntityMarkerAdvanced tileEntity)
    {
        int i = tileEntity.getMarkerRP().direction;
        BlockPos blockpos = tileEntity.getMarkerRP().getNeighborBlockPos();
        TileEntity neighborMarker = tileEntity.getWorld().getTileEntity(blockpos);
        if (neighborMarker instanceof TileEntityMarkerAdvanced)
        {
            return (TileEntityMarkerAdvanced) neighborMarker;
        }
        return null;
    }

    public static RailPosition getNeighborRail(World world, RailPosition rp)
    {
        TileEntityMarkerAdvanced marker = BlockUtils.getMarkerFromPos(world, rp);
        if (marker != null)
        {
            return getNeighborRail(marker);
        }
        return null;
    }

    public static RailPosition getNeighborRail(TileEntityMarkerAdvanced tileEntity)
    {
        int i = tileEntity.getMarkerRP().direction;
        BlockPos blockpos = tileEntity.getMarkerRP().getNeighborBlockPos();
        TileEntity tileentity = tileEntity.getWorld().getTileEntity(blockpos);
        if (!(tileentity instanceof TileEntityLargeRailBase))
        {
            return null;
        }
        else
        {
            TileEntityLargeRailCore tileentitylargerailcore = ((TileEntityLargeRailBase) tileentity).getRailCore();
            if (tileentitylargerailcore == null)
            {
                return null;
            }
            else
            {
                double d0 = Double.MAX_VALUE;
                RailPosition railposition = null;

                for (RailMap railmap : tileentitylargerailcore.getAllRailMaps())
                {
                    double d1 = NGTMath.getDistanceSq(tileEntity.getMarkerRP().posX, tileEntity.getMarkerRP().posZ,
                            railmap.getStartRP().posX, railmap.getStartRP().posZ);
                    if (d1 < d0)
                    {
                        d0 = d1;
                        railposition = railmap.getStartRP();
                    }

                    d1 = NGTMath.getDistanceSq(tileEntity.getMarkerRP().posX, tileEntity.getMarkerRP().posZ,
                            railmap.getEndRP().posX, railmap.getEndRP().posZ);
                    if (d1 < d0)
                    {
                        d0 = d1;
                        railposition = railmap.getEndRP();
                    }
                }

                return railposition;
            }
        }
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name.trim();
    }

    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        if (groupId >= 1 && groupId <= 1000)
            this.groupId = groupId;
        else
            this.groupId = 0;
    }

    public RailMapAdvanced getOriginalRailMap()
    {
        return originalRailMap;
    }

    public static class MarkerCriticalValues
    {
        public String name;
        public int groupId;
        public BlockPos pos;
        public RailPosition rp;
        public List<BlockPos> markerPosList;
        public RailDrawingScheme drawingScheme;
        public AnchorEditStatus editStatusH;
        public AnchorEditStatus editStatusV;

        public MarkerCriticalValues(TileEntityMarkerAdvanced marker)
        {
            this.name = marker.getName();
            this.groupId = marker.groupId;
            this.pos = marker.pos;
            this.rp = marker.getMarkerRP();
            this.markerPosList = marker.markerPosList;
            this.drawingScheme = marker.drawingScheme;
            this.editStatusH = marker.editStatusH;
            this.editStatusV = marker.editStatusV;
        }

        public MarkerCriticalValues(MarkerCriticalValues old)
        {
            this.groupId = old.groupId;
            this.drawingScheme = old.drawingScheme;
            this.editStatusH = old.editStatusH;
            this.editStatusV = old.editStatusV;
        }

        @Override
        public MarkerCriticalValues clone()
        {
            MarkerCriticalValues value = new MarkerCriticalValues(this);
            value.name = new String(this.name);
            value.pos = new BlockPos(this.pos.getX(), this.pos.getY(), this.pos.getZ());
            value.rp = RailMapAdvanced.cloneRP(this.rp);
            value.markerPosList = new ArrayList<>();
            for (int i = 0; i < this.markerPosList.size(); i++)
            {
                BlockPos pos = this.markerPosList.get(i);
                value.markerPosList.add(i, new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
            }
            return value;
        }

        @Override
        public String toString()
        {
            return super.toString();
        }
    }
}
