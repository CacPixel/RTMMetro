package net.cacpixel.rtmmetro.util;

import jp.ngt.ngtlib.NGTCore;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.RTMMetroItems;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockUtils
{
    public static int worldEditWandId = 0;

    public static boolean isPosEqual(BlockPos pos1, BlockPos pos2)
    {
        return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ();
    }

    public static boolean isPosEqual(BlockPos pos1, RailPosition rp)
    {
        return pos1.getX() == rp.blockX && pos1.getY() == rp.blockY && pos1.getZ() == rp.blockZ;
    }

    public static boolean isPosEqual(RailPosition rp1, RailPosition rp2)
    {
        return rp1.blockX == rp2.blockX && rp1.blockY == rp2.blockY && rp1.blockZ == rp2.blockZ;
    }

    public static TileEntityMarkerAdvanced getMarkerFromPos(World world, RailPosition rp)
    {
        TileEntity te = BlockUtil.getTileEntity(world, new BlockPos(rp.blockX, rp.blockY, rp.blockZ));
        if (te instanceof TileEntityMarkerAdvanced)
            return ((TileEntityMarkerAdvanced) te);
        else
            return null;
    }

    public static TileEntityMarkerAdvanced getMarkerFromPos(World world, BlockPos pos)
    {
        TileEntity te = BlockUtil.getTileEntity(world, new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
        if (te instanceof TileEntityMarkerAdvanced)
            return ((TileEntityMarkerAdvanced) te);
        else
            return null;
    }

    public static RailPosition getRPFromPos(World world, BlockPos pos)
    {
        TileEntity te = BlockUtil.getTileEntity(world, new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
        if (te instanceof TileEntityMarkerAdvanced)
            return ((TileEntityMarkerAdvanced) te).getMarkerRP();
        else
            return null;
    }

    public static BlockPos getPosFromRP(RailPosition position)
    {
        return new BlockPos(position.blockX, position.blockY, position.blockZ);
    }

    public static List<TileEntityMarkerAdvanced> getAllMarkers(World world, List<BlockPos> markerPosList, BlockPos currentPos)
    {
        return markerPosList.stream()
                .map(it -> getMarkerFromPos(world, it))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<TileEntityMarkerAdvanced> getOtherMarkers(World world, List<BlockPos> markerPosList, BlockPos currentPos)
    {
        return markerPosList.stream().filter(m -> !BlockUtils.isPosEqual(m, currentPos))
                .map(it -> getMarkerFromPos(world, it))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static TileEntityMarkerAdvanced getOppositeMarker(World world, List<BlockPos> markerPosList, BlockPos currentPos)
    {
        return markerPosList.stream().filter(m -> !BlockUtils.isPosEqual(m, currentPos))
                .map(it -> getMarkerFromPos(world, it))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public static List<RailPosition> getAllRPs(World world, List<BlockPos> markerPosList, BlockPos currentPos)
    {
        return getAllMarkers(world, markerPosList, currentPos).stream().map(it -> it.rp).collect(Collectors.toList());
    }

    public static List<RailPosition> getOtherRPs(World world, List<BlockPos> markerPosList, BlockPos currentPos)
    {
        return getOtherMarkers(world, markerPosList, currentPos).stream().map(it -> it.rp).collect(Collectors.toList());
    }

    public static RailPosition getOppositeRP(World world, List<BlockPos> markerPosList, BlockPos currentPos)
    {
        TileEntityMarkerAdvanced marker = getOppositeMarker(world, markerPosList, currentPos);
        return (marker == null) ? null : marker.rp;
    }

    public static boolean cancelRailBlockBreak(World world, EntityPlayer player, BlockPos pos)
    {
        if (worldEditWandId == 0)
        {
            worldEditWandId = -1;
            File file = null;
            if (Loader.isModLoaded("worldedit"))
            {
                file = NGTCore.proxy.getMinecraftDirectory("config/worldedit/worldedit.properties");
                ModLog.debug("Find worldedit forge mod configuration");
            }
            if (file != null)
            {
                try
                {
                    Stream<String> lines = Files.lines(file.toPath());
                    lines.forEach(str -> {
                        if (str.startsWith("wand-item"))
                        {
                            worldEditWandId = Integer.parseInt(str.substring("wand-item=".length()));
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        Block block = world.getBlockState(pos).getBlock();
        Item item = player.getHeldItemMainhand().getItem();
        Item worldEditWand = worldEditWandId <= 0 ? null : Item.getItemById(271);
        if (block == RTMMetroBlock.LARGE_RAIL_BASE_ADVANCED
                || block == RTMMetroBlock.LARGE_RAIL_CORE_ADVANCED
                || block == RTMMetroBlock.LARGE_RAIL_SWITCH_CORE_ADVANCED
                || block == RTMMetroBlock.LARGE_RAIL_SWITCH_BASE_ADVANCED)
        {
            return item != Item.getItemFromBlock(RTMMetroBlock.MARKER_ADVANCED)
                    && item != Item.getItemFromBlock(RTMMetroBlock.MARKER_ADVANCED_SWITCH)
                    && item != RTMMetroItems.railAdvanced
                    && item != RTMMetroItems.railEditor
                    && item != worldEditWand;
        }
        return false;
    }
}
