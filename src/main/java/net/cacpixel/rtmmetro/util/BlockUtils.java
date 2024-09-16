package net.cacpixel.rtmmetro.util;

import jp.ngt.ngtlib.NGTCore;
import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.RTMMetroItems;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Stream;

public class BlockUtils
{
    public static int worldEditWandId = 0;

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
                || block == RTMMetroBlock.LARGE_RAIL_MAINCORE_ADVANCED
                || block == RTMMetroBlock.LARGE_RAIL_SUBCORE_ADVANCED
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
