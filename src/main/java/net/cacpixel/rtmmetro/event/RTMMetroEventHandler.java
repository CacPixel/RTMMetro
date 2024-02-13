package net.cacpixel.rtmmetro.event;

import net.cacpixel.rtmmetro.RTMMetroBlock;
import net.cacpixel.rtmmetro.rail.util.MarkerManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class RTMMetroEventHandler {

    @SubscribeEvent
    public void onLoadWorld(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            MarkerManager.getInstance().loadData(event.getWorld());
        }
    }

    @SubscribeEvent
    public void onUnloadWorld(WorldEvent.Unload event) {

    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        MarkerManager.getInstance().sendMarkersToClient((EntityPlayerMP) event.player);
        MarkerManager.getInstance().validate(event.player.getEntityWorld());
    }

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event) {
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        if (block == RTMMetroBlock.LARGE_RAIL_BASE_ADVANCED
                || block == RTMMetroBlock.LARGE_RAIL_MAINCORE_ADVANCED
                || block == RTMMetroBlock.LARGE_RAIL_SUBCORE_ADVANCED
                || block == RTMMetroBlock.LARGE_RAIL_SWITCH_CORE_ADVANCED
                || block == RTMMetroBlock.LARGE_RAIL_SWITCH_BASE_ADVANCED) {
            if (event.getPlayer().getHeldItemMainhand().getItem() != Item.getItemFromBlock(RTMMetroBlock.MARKER_ADVANCED)) {
                event.setCanceled(true);
            }
        }
    }
}
