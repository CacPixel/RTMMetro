package net.cacpixel.rtmmetro;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.ResourceType;
import net.cacpixel.rtmmetro.client.gui.RTMMetroGUIHandler;
import net.cacpixel.rtmmetro.event.RTMMetroEventHandler;
import net.cacpixel.rtmmetro.modelpack.init.ModelPackManagerEx;
import net.cacpixel.rtmmetro.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;


@Mod(
        modid = RTMMetro.MODID,
        name = RTMMetro.NAME,
        version = RTMMetro.VERSION,
        dependencies = "required-after:rtm@[2.4.24,);required-after:ngtlib@[2.4.21,);required-after:mixinbooter"
)
public class RTMMetro
{
    public static final String MODID = "rtmmetro";
    public static final String NAME = "RTMMetro";
    public static final String VERSION = "0.1.8";
    private static boolean HAS_FIXRTM_MOD = false;

    @Mod.Instance(MODID)
    public static RTMMetro INSTANCE;

    @Mod.Metadata(MODID)
    public static ModMetadata METADATA;

    @SidedProxy(clientSide = "net.cacpixel.rtmmetro.proxy.ClientProxy",
            serverSide = "net.cacpixel.rtmmetro.proxy.CommonProxy")
    public static CommonProxy proxy;
    private static Logger logger;
    public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        RTMMetroBlock.init();
        RTMMetroItems.init();
        RTMMetroPackets.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new RTMMetroGUIHandler());
        proxy.preInit();
        ModelPackManagerEx.INSTANCE.loadRTMModelPack();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
//        printTagMap();
        proxy.init();
        RTMMetroToolTip.init();
        MinecraftForge.EVENT_BUS.register(new RTMMetroEventHandler());
    }

    @EventHandler
    public void complete(FMLLoadCompleteEvent event)
    {
        proxy.complete();
        if (Loader.isModLoaded("fix-rtm"))
        {
            HAS_FIXRTM_MOD = true;
        }
        ModelPackManagerEx.INSTANCE.waitForComplete();
    }

    public static boolean hasFixrtm()
    {
        return HAS_FIXRTM_MOD;
    }
}
