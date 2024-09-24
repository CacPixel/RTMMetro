package net.cacpixel.rtmmetro.traincontrollerdemo;

import jp.ngt.ngtlib.io.NGTLog;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UartDevicesMCEventHandler
{

    @SubscribeEvent
    public void onLoadWorld(WorldEvent.Load event)
    {
//        if (event.getWorld().isRemote)
//        {
//            String nativeLibName = LibRxtxLoader.getLibName() + "." + LibRxtxLoader.getExt();
//            try
//            {
//                UartDevices.initDevices();
//            }
//            catch (UnsatisfiedLinkError e)
//            {
//                NGTLog.debug("Could not find " + nativeLibName + ", serial connections will be disabled.");
//            }
//        }
    }

    @SubscribeEvent
    public void onUnloadWorld(WorldEvent.Unload event)
    {
//        if (event.getWorld().isRemote)
//        {
//            UartDevices.deInitDevices();
//        }
    }
}
