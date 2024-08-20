package net.cacpixel.rtmmetro.rail.block;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.ngtlib.util.PermissionManager;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.item.ItemRail;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.rail.BlockMarker;
import jp.ngt.rtm.rail.TileEntityLargeRailSwitchBase;
import jp.ngt.rtm.rail.util.RailMaker;
import jp.ngt.rtm.rail.util.RailMap;
import jp.ngt.rtm.rail.util.RailPosition;
import jp.ngt.rtm.rail.util.SwitchType;
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.RTMMetroItems;
import net.cacpixel.rtmmetro.items.ItemRailAdvanced;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityLargeRailMainCoreAdvanced;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityLargeRailSwitchCoreAdvanced;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.cacpixel.rtmmetro.rail.util.MarkerData;
import net.cacpixel.rtmmetro.rail.util.MarkerManager;
import net.cacpixel.rtmmetro.rail.util.RailMapAdvanced;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockMarkerAdvanced extends BlockMarker {

    public BlockMarkerAdvanced(BlockMarker.MarkerType type) {
        super(type);
    }

//    public BlockMarkerAdvanced(MarkerType type) {
//        super(Material.GLASS);
//        this.markerType = type;
//        this.setLightOpacity(1);
//        this.setLightLevel(1.0F);
//        this.setHardness(1.0F);
//        this.setResistance(5.0F);
//        this.setSoundType(SoundType.GLASS);
//        this.setAABB(new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F));
//    }


    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        switch (this.markerType) {
            case STANDARD:
                items.add(new ItemStack(this, 1, 0));
                items.add(new ItemStack(this, 1, 4));
                break;
            case SWITCH:
                items.add(new ItemStack(this, 1, 0));
                items.add(new ItemStack(this, 1, 4));
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new TileEntityMarkerAdvanced();
    }

    public static int getFacing(EntityLivingBase placer, boolean isDiagonal) {
        return isDiagonal ? NGTMath.floor(NGTMath.normalizeAngle((double) placer.rotationYaw + 180.0D) / 90.0D) & 3 : NGTMath.floor(NGTMath.normalizeAngle((double) placer.rotationYaw + 180.0D) / 90.0D + 0.5D) & 3;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int i = stack.getItemDamage();
        int j = getFacing(placer, i >= 4);
        int k = i / 4;
        BlockUtil.setBlock(world, pos, this, j + k * 4, 2);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        if (pos.getY() > 0 && pos.getY() < 255) {
            return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        MarkerManager.getInstance().removeMarker(MarkerManager.getWorldName(worldIn), pos);
        if (!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntityMarkerAdvanced) {
                MarkerManager.sendPacket((TileEntityMarkerAdvanced) te, true);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    protected boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ) {
        World world = holder.getWorld();
        EntityPlayer entityplayer = holder.getPlayer();
        int i = holder.getBlockPos().getX();
        int j = holder.getBlockPos().getY();
        int k = holder.getBlockPos().getZ();
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        if (itemstack != null) {
            TileEntity tileentity = world.getTileEntity(holder.getBlockPos());
            if (!(tileentity instanceof TileEntityMarkerAdvanced)) {
                return true;
            }

            if (itemstack.getItem() == Item.getItemFromBlock(RTMMetroBlock.MARKER_ADVANCED)
                    || itemstack.getItem() == Item.getItemFromBlock(RTMMetroBlock.MARKER_ADVANCED_SWITCH)) {
                if (world.isRemote) {
//                    entityplayer.openGui(RTMCore.instance, RTMCore.guiIdRailMarker, world, i, j, k);
                }
                return true;
            }
        }

        if (!world.isRemote && this.onMarkerActivated(world, i, j, k, entityplayer, true) && !entityplayer.capabilities.isCreativeMode) {
            itemstack.shrink(1);
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    public boolean onMarkerActivatedClient(TileEntityMarkerAdvanced marker, int x, int y, int z) {
        return this.onMarkerActivated(marker.getWorld(), x, y, z, NGTUtilClient.getMinecraft().player, false);
    }

    public void makeRailMap(TileEntityMarkerAdvanced marker, int x, int y, int z) {
        if (marker.getWorld().isRemote) {
            this.onMarkerActivatedClient(marker, x, y, z);
        } else {
            this.onMarkerActivated(marker.getWorld(), x, y, z, null, false);
        }
    }

    public boolean onMarkerActivated(World world, int x, int y, int z, EntityPlayer player, boolean makeRail) {
        ResourceStateRail resourcestaterail = this.hasRail(player, makeRail);
        if (resourcestaterail == null) {
            return false;
        } else {
            boolean flag = player == null || player.capabilities.isCreativeMode;
            List<RailPosition> list;
            if (ModConfig.useFastMarkerSearchMethod) {
                list = this.getAllMarkers(world, x, y, z, player);
            } else {
                list = this.searchAllMarker(world, x, y, z, player);
            }

            for (RailPosition railposition : list) {
                railposition.addHeight((double) (resourcestaterail.blockHeight - 0.0625F));
            }

            return createRail(world, x, y, z, list, resourcestaterail, makeRail, flag);
        }
    }

    private List<RailPosition> searchAllMarker(World world, int x, int y, int z, EntityPlayer player) {
        List<RailPosition> list = new ArrayList<>();
        int i = ModConfig.railGeneratingDistance;
        int j = i * 2;
        int k = ModConfig.railGeneratingHeight;
        int l = k * 2;

        for (int i1 = 0; i1 < j; ++i1) {
            for (int j1 = 0; j1 < l; ++j1) {
                for (int k1 = 0; k1 < j; ++k1) {
                    int l1 = x - i + i1;
                    int i2 = y - k + j1;
                    int j2 = z - i + k1;
                    RailPosition railposition = this.getRailPosition(world, l1, i2, j2);
                    if (railposition != null) {
                        list.add(railposition);
                    }
                }
            }
        }

        if (ModConfig.useModernMarkerSortMethod) {
            modernMarkerSortMethod(list, player);
        } else {
            originalMarkerSortMethod(list);
        }
        return list;
    }

    private List<RailPosition> getAllMarkers(World world, int x, int y, int z, EntityPlayer player) {
        try {
            List<RailPosition> list = new ArrayList<>();
            //if(!world.isRemote)
//            MarkerManager.getInstance().validate(world);

            List<MarkerData> markers = MarkerManager.getInstance().getMarkerList();
            int dis = ModConfig.railGeneratingDistance;
            int hei = ModConfig.railGeneratingHeight;

            long startTime = System.nanoTime();
            for (MarkerData m : markers) {
                if (!MarkerManager.getWorldName(world).equals(m.getWorldName())) {
                    continue;
                }
                if (m.getX() <= x + dis && m.getX() >= x - dis
                        && m.getZ() <= z + dis && m.getZ() >= z - dis
                        && m.getY() <= y + hei && m.getY() >= y - hei) {
                    TileEntityMarkerAdvanced m1 = (TileEntityMarkerAdvanced) world.getTileEntity(new BlockPos(m.getX(), m.getY(), m.getZ()));
                    if (m1 != null) {
                        list.add(m1.getMarkerRP());
                    }
                }
            }
            long duration = System.nanoTime() - startTime;
//            NGTLog.debug("getAllMarkers (cacpixel method) time:%.2f us", duration / 1000.0f);
            if (ModConfig.useModernMarkerSortMethod) {
                modernMarkerSortMethod(list, player);
            } else {
                originalMarkerSortMethod(list);
            }
            return list;
        } catch (Throwable e) {
            e.printStackTrace();
            return new ArrayList<>();

        }

    }

    public static void originalMarkerSortMethod(List<RailPosition> list) {
        list.sort((arg0, arg1) -> {
            if (arg0.switchType != arg1.switchType) {
                return arg1.switchType - arg0.switchType;
            } else {
                return arg0.blockY != arg1.blockY ? arg0.blockY - arg1.blockY : arg0.hashCode() - arg1.hashCode();
            }
        });
    }

    public static void modernMarkerSortMethod(List<RailPosition> list, EntityPlayer player) {
        list.sort((arg0, arg1) -> {
            if (arg0.switchType != arg1.switchType) {
                return arg1.switchType - arg0.switchType;
            } else if (player != null) {
                double px = player.posX;
                double py = player.posY;
                double pz = player.posZ;
                double l0 = Math.sqrt(Math.pow(arg0.blockX - px, 2) + Math.pow(arg0.blockY - py, 2) + Math.pow(arg0.blockZ - pz, 2));
                double l1 = Math.sqrt(Math.pow(arg1.blockX - px, 2) + Math.pow(arg1.blockY - py, 2) + Math.pow(arg1.blockZ - pz, 2));
                return (int) Math.floor(l0 - l1);
            } else {
                return arg0.hashCode() - arg1.hashCode();
            }
        });
    }

    public static boolean createRail(World world, int x, int y, int z, List<RailPosition> rps, ResourceStateRail state, boolean makeRail, boolean isCreative) {
        if (rps.size() == 1) {
            RailPosition railposition = rps.get(0);
            if (railposition.hasScript()) {
//                createCustomRail(world, railposition, state, makeRail, isCreative);
            } else {
                TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
                if (tileEntity instanceof TileEntityMarkerAdvanced) {
                    TileEntityMarkerAdvanced marker = (TileEntityMarkerAdvanced) tileEntity;
                    List<BlockPos> list = new ArrayList<>();
                    list.add(new BlockPos(railposition.blockX, railposition.blockY, railposition.blockZ));
                    marker.setMarkersPos(list);
                }
            }
        } else if (rps.size() == 2) {
            RailPosition startRP = rps.get(0);
            RailPosition endRP = rps.get(1);
//            if (startRP.switchType == 1 && endRP.switchType == 1) {
//                createTurntable(world, startRP, endRP, state, makeRail, isCreative);
//            } else {
//            RailPosition railposition2 = endRP.blockY >= startRP.blockY ? startRP : endRP;
//            RailPosition railposition3 = endRP.blockY >= startRP.blockY ? endRP : startRP;
            prepareNormalRail(world, startRP, endRP, state, makeRail, isCreative);
//            }
        } else if (rps.size() > 2) {
            createSwitchRail(world, x, y, z, rps, state, makeRail, isCreative);
        }

        return false;
    }

    private static void prepareNormalRail(World world, RailPosition start, RailPosition end, ResourceStateRail prop, boolean makeRail, boolean isCreative) {
        RailMapAdvanced originalRailMap = new RailMapAdvanced(start, end);
        if (makeRail && originalRailMap.canPlaceRail(world, isCreative, prop)) {
            TileEntity tileEntity = world.getTileEntity(new BlockPos(start.blockX, start.blockY, start.blockZ));
            List<RailPosition> rps2 = new ArrayList<>();
            rps2.add(start);
            rps2.add(end);
            MarkerManager.getInstance().removeMarker(world, rps2);

            // 是否应该遍历所有的RailPositions？
            if (!world.isRemote) {
                MarkerManager.sendPacket((TileEntityMarkerAdvanced) world.getTileEntity(new BlockPos(start.blockX, start.blockY, start.blockZ)), true);
            }

//            int split = Math.max((int) originalRailMap.getLength() / 50, 1);
            int split = 2;
            if (tileEntity instanceof TileEntityMarkerAdvanced) {
                TileEntityMarkerAdvanced marker = (TileEntityMarkerAdvanced) tileEntity;
//                split = Math.max(1, marker.splits);
            } else {
                return;
            }

            List<RailMapAdvanced> rms = new ArrayList<>();
            RailMapAdvanced next = originalRailMap;
            int length = (int) Math.floor(originalRailMap.getLength()) * 2;
            for (int i = 0; i < split; i++) {
                int order = length / split * (i + 1);
                RailMapAdvanced railmap;
                if (i == split - 1) {
                    rms.add(next);
                    break;
                } else {
                    List<RailMapAdvanced> railMaps = next.split(length, order);
                    if (railMaps.size() == 1) {
                        rms.add(next);
                        break;
                    }
                    railmap = railMaps.get(0);
                    next = railMaps.get(1);
                    rms.add(railmap);
                }
            }
            for (RailMapAdvanced railmap : rms) {
                createNormalRail(world, railmap.getStartRP(), railmap.getEndRP(), prop, makeRail, isCreative);
            }
        } else {
            createNormalRail(world, start, end, prop, makeRail, isCreative);
        }
    }

    private static void createNormalRail(World world, RailPosition start, RailPosition end, ResourceStateRail prop, boolean makeRail, boolean isCreative) {
        createNormalRail(world, start, end, prop, makeRail, isCreative, new ArrayList<>());
    }

    private static void createNormalRail(World world, RailPosition start, RailPosition end, ResourceStateRail prop, boolean makeRail, boolean isCreative,
                                         List<BlockPos> conflicts) {
        RailMapAdvanced rm = new RailMapAdvanced(start, end);
        if (makeRail && rm.canPlaceRail(world, isCreative, prop)) {
            rm = new RailMapAdvanced(start, end);
            rm.setRail(world, RTMMetroBlock.LARGE_RAIL_BASE_ADVANCED, start.blockX, start.blockY, start.blockZ, prop);
            for (BlockPos pos : conflicts) {
                BlockUtil.setAir(world, pos.getX(), pos.getY(), pos.getZ());
            }

            BlockUtil.setBlock(world, start.blockX, start.blockY, start.blockZ, RTMMetroBlock.LARGE_RAIL_MAINCORE_ADVANCED, 0, 3);
            TileEntityLargeRailMainCoreAdvanced mainCore = (TileEntityLargeRailMainCoreAdvanced) BlockUtil.getTileEntity(world, start.blockX, start.blockY, start.blockZ);
            mainCore.setRailPositions(new RailPosition[]{start, end});
            mainCore.getResourceState().readFromNBT(prop.writeToNBT());
            mainCore.setStartPoint(start.blockX, start.blockY, start.blockZ);
            mainCore.createRailMap();
            mainCore.sendPacket();
            if (BlockUtil.getBlock(world, end.blockX, end.blockY, end.blockZ) instanceof BlockMarkerAdvanced) {
                BlockUtil.setAir(world, end.blockX, end.blockY, end.blockZ);
            }
        } else {
            TileEntity tileentity = BlockUtil.getTileEntity(world, start.blockX, start.blockY, start.blockZ);
            if (tileentity instanceof TileEntityMarkerAdvanced) {
                List<BlockPos> list = new ArrayList();
                list.add(new BlockPos(start.blockX, start.blockY, start.blockZ));
                list.add(new BlockPos(end.blockX, end.blockY, end.blockZ));
                ((TileEntityMarkerAdvanced) tileentity).setMarkersPos(list);
            }
        }
    }

//    private static boolean createCustomRail(World world, RailPosition rp, ResourceStateRail prop, boolean makeRail, boolean isCreative) {
//        RailMap railmap = new RailMapCustom(rp, rp.scriptName, rp.scriptArgs);
//        if (makeRail && railmap.canPlaceRail(world, isCreative, prop)) {
//            List<RailPosition> rps = new ArrayList<>();
//            rps.add(railmap.getStartRP());
//            rps.add(railmap.getEndRP());
//            MarkerManager.getInstance().removeMarker(world, rps);
//            // 是否应该遍历所有的RailPositions？
//            if (!world.isRemote)
//                MarkerManager.sendPacket((TileEntityMarkerAdvanced) world.getTileEntity(new BlockPos(rps.get(0).blockX, rps.get(0).blockY, rps.get(0).blockZ)), true);
//
//            railmap.setRail(world, RTMMetroBlock.LARGE_RAIL_BASE_ADVANCED, rp.blockX, rp.blockY, rp.blockZ, prop);
//            BlockUtil.setBlock(world, rp.blockX, rp.blockY, rp.blockZ, RTMMetroBlock.LARGE_RAIL_MAINCORE_ADVANCED, 0, 3);
//            TileEntityLargeRailMainCoreAdvanced TileEntityLargeRailMainCoreAdvanced = (TileEntityLargeRailMainCoreAdvanced) BlockUtil.getTileEntity(world, rp.blockX, rp.blockY, rp.blockZ);
//            TileEntityLargeRailMainCoreAdvanced.setRailPositions(new RailPosition[]{rp, railmap.getEndRP()});
//            TileEntityLargeRailMainCoreAdvanced.getResourceState().readFromNBT(prop.writeToNBT());
//            TileEntityLargeRailMainCoreAdvanced.setStartPoint(rp.blockX, rp.blockY, rp.blockZ);
//            TileEntityLargeRailMainCoreAdvanced.createRailMap();
//            TileEntityLargeRailMainCoreAdvanced.sendPacket();
//            return true;
//        } else {
//            TileEntity tileentity = BlockUtil.getTileEntity(world, rp.blockX, rp.blockY, rp.blockZ);
//            if (tileentity instanceof TileEntityMarkerAdvanced) {
//                List<BlockPos> list = new ArrayList();
//                list.add(new BlockPos(rp.blockX, rp.blockY, rp.blockZ));
//                ((TileEntityMarkerAdvanced) tileentity).setMarkersPos(list);
//            }
//
//            return false;
//        }
//    }

    private static boolean createSwitchRail(World world, int x, int y, int z, List<RailPosition> list, ResourceStateRail prop, boolean makeRail, boolean isCreative) {
        RailMaker railmaker = new RailMaker(world, list);
        SwitchType switchtype = railmaker.getSwitch();
        if (switchtype == null) {
            return false;
        } else {
            RailMap[] arailmap = switchtype.getAllRailMap();
            if (arailmap == null) {
                return false;
            } else {
                boolean flag = false;

                for (RailMap railmap : arailmap) {
                    if (!railmap.canPlaceRail(world, isCreative, prop)) {
                        flag = true;
                    }
                }

                if (makeRail && !flag) {
                    MarkerManager.getInstance().removeMarker(world, list);
                    RailPosition railposition = list.get(0);

                    // 是否应该遍历所有的RailPositions？
                    if (!world.isRemote)
                        MarkerManager.sendPacket((TileEntityMarkerAdvanced) world.getTileEntity(new BlockPos(railposition.blockX, railposition.blockY, railposition.blockZ)), true);

                    x = railposition.blockX;
                    y = railposition.blockY;
                    z = railposition.blockZ;

                    for (RailMap railmap1 : arailmap) {
                        railmap1.setRail(world, RTMMetroBlock.LARGE_RAIL_BASE_ADVANCED, x, y, z, prop);
                    }

                    for (RailPosition railposition1 : list) {
                        BlockUtil.setBlock(world, railposition1.blockX, railposition1.blockY, railposition1.blockZ, RTMMetroBlock.LARGE_RAIL_SWITCH_BASE_ADVANCED, 0, 3);
                        TileEntityLargeRailSwitchBase tileentitylargerailswitchbase = (TileEntityLargeRailSwitchBase) BlockUtil.getTileEntity(world, railposition1.blockX, railposition1.blockY, railposition1.blockZ);
                        tileentitylargerailswitchbase.setStartPoint(x, y, z);
                    }

                    BlockUtil.setBlock(world, x, y, z, RTMMetroBlock.LARGE_RAIL_SWITCH_CORE_ADVANCED, 0, 3);
                    TileEntityLargeRailSwitchCoreAdvanced TileEntityLargeRailSwitchCoreAdvanced = (TileEntityLargeRailSwitchCoreAdvanced) BlockUtil.getTileEntity(world, x, y, z);
                    TileEntityLargeRailSwitchCoreAdvanced.setRailPositions(list.toArray(new RailPosition[list.size()]));
                    TileEntityLargeRailSwitchCoreAdvanced.getResourceState().readFromNBT(prop.writeToNBT());
                    TileEntityLargeRailSwitchCoreAdvanced.setStartPoint(x, y, z);
                    TileEntityLargeRailSwitchCoreAdvanced.createRailMap();
                    TileEntityLargeRailSwitchCoreAdvanced.sendPacket();
                    return true;
                } else {
                    TileEntity tileentity = BlockUtil.getTileEntity(world, x, y, z);
                    if (tileentity instanceof TileEntityMarkerAdvanced) {
                        List<BlockPos> posList = new ArrayList();

                        for (int i = 0; i < list.size(); ++i) {
                            RailPosition rp = list.get(i);
                            posList.add(new BlockPos(rp.blockX, rp.blockY, rp.blockZ));
                        }

                        ((TileEntityMarkerAdvanced) tileentity).setMarkersPos(posList);
                    }

                    return false;
                }
            }
        }
    }

//    private static boolean createTurntable(World world, RailPosition start, RailPosition end, ResourceStateRail prop, boolean makeRail, boolean isCreative) {
//        int i = 0;
//        int j = start.blockY;
//        int k = 0;
//        int l = 0;
//        if (start.blockX == end.blockX && (start.blockZ - end.blockZ) % 2 == 0) {
//            i = start.blockX;
//            k = (start.blockZ + end.blockZ) / 2;
//            l = Math.abs(start.blockZ - end.blockZ) / 2;
//        }
//
//        if (start.blockZ == end.blockZ && (start.blockX - end.blockX) % 2 == 0) {
//            i = (start.blockX + end.blockX) / 2;
//            k = start.blockZ;
//            l = Math.abs(start.blockX - end.blockX) / 2;
//        }
//
//        if (l == 0) {
//            return false;
//        } else {
//            RailMapTurntable railmapturntable = new RailMapTurntable(start, end, i, j, k, l);
//            if (makeRail && railmapturntable.canPlaceRail(world, isCreative, prop)) {
//                railmapturntable.setRail(world, RTMRail.largeRailBase, i, j, k, prop);
//                BlockUtil.setBlock(world, i, j, k, RTMRail.TURNTABLE_CORE, 0, 3);
//                TileEntityTurnTableCore tileentityturntablecore = (TileEntityTurnTableCore) BlockUtil.getTileEntity(world, i, j, k);
//                tileentityturntablecore.setRailPositions(new RailPosition[]{start, end});
//                tileentityturntablecore.getResourceState().readFromNBT(prop.writeToNBT());
//                tileentityturntablecore.setStartPoint(i, j, k);
//                tileentityturntablecore.createRailMap();
//                tileentityturntablecore.sendPacket();
//                return true;
//            } else {
//                return false;
//            }
//        }
//    }

    public static byte getMarkerDir(Block block, int meta) {
        int i = meta & 3;
        int j = (6 - i & 3) * 2;
        if ((block == RTMMetroBlock.MARKER_ADVANCED || block == RTMMetroBlock.MARKER_ADVANCED_SWITCH) && meta >= 4) {
            j = j + 7 & 7;
        }

        return (byte) j;
    }

    private RailPosition getRailPosition(World world, int x, int y, int z) {
        TileEntity tileentity = BlockUtil.getTileEntity(world, x, y, z);
        return tileentity instanceof TileEntityMarkerAdvanced ? ((TileEntityMarkerAdvanced) tileentity).getMarkerRP() : null;
    }

    public ResourceStateRail hasRail(@Nullable EntityPlayer player, boolean par2) {
        if (player == null) {
            return ItemRail.getDefaultProperty();
        } else {
            if (PermissionManager.INSTANCE.hasPermission(player, "editRail")) {
                ItemStack itemstack = player.inventory.getCurrentItem();
                if (itemstack.getItem() == RTMItem.itemLargeRail) {
                    return ((ItemRail) RTMItem.itemLargeRail).getModelState(itemstack);
                } else if (itemstack.getItem() == RTMMetroItems.railAdvanced) {
                    return ((ItemRailAdvanced) RTMMetroItems.railAdvanced).getModelState(itemstack);
                }

                if (player.capabilities.isCreativeMode || !par2) {
                    return ItemRail.getDefaultProperty();
                }
            }

            return null;
        }
    }

    public enum MarkerType {
        STANDARD(0xFF0000),
        SWITCH(0x0000FF);

        public final int color;

        private MarkerType(int par1) {
            this.color = par1;
        }
    }

}
