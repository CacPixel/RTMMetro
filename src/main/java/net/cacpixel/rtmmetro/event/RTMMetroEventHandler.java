package net.cacpixel.rtmmetro.event;

import net.cacpixel.rtmmetro.rail.util.MarkerManager;
import net.cacpixel.rtmmetro.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RTMMetroEventHandler
{

    @SubscribeEvent
    public void onLoadWorld(WorldEvent.Load event)
    {
        if (event.getWorld().isRemote)
        {
            onClientWorldLoad(event);
        }
        else
        {
            MinecraftServer server = event.getWorld().getMinecraftServer();
            if (server != null)
            {
                onServerWorldLoad(event);
                if (server.worlds == null || server.worlds.length == 1)
                {
                    onServerWorldFirstLoad(event);
                }
            }
        }
    }

    private void onServerWorldFirstLoad(WorldEvent.Load event)
    {
    }

    private void onServerWorldLoad(WorldEvent.Load event)
    {
        MarkerManager.getInstance().loadData(event.getWorld());
    }

    @SideOnly(Side.CLIENT)
    private void onClientWorldLoad(WorldEvent.Load event)
    {
    }


    @SubscribeEvent
    public void onUnloadWorld(WorldEvent.Unload event)
    {
        if (event.getWorld().isRemote)
        {
            onClientWorldUnload(event);
        }
        else
        {
            MinecraftServer server = event.getWorld().getMinecraftServer();
            if (server != null)
            {
                onServerWorldUnload(event);
                if (server.worlds.length <= 1)
                {
                    onServerAllWorldsUnload(event);
                }
            }
        }
    }

    private void onServerAllWorldsUnload(WorldEvent.Unload event)
    {
    }

    private void onServerWorldUnload(WorldEvent.Unload event)
    {
    }

    @SideOnly(Side.CLIENT)
    private void onClientWorldUnload(WorldEvent.Unload event)
    {
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        MarkerManager.getInstance().sendMarkersToClient((EntityPlayerMP) event.player);
        MarkerManager.getInstance().validate(event.player.getEntityWorld());
    }

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event)
    {
        if (BlockUtils.cancelRailBlockBreak(event.getWorld(), event.getPlayer(), event.getPos()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {

    }

}
