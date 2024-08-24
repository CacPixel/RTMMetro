package net.cacpixel.rtmmetro;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.ResourceType;
import net.cacpixel.rtmmetro.client.gui.RTMMetroGUIHandler;
import net.cacpixel.rtmmetro.client.rtmtoolbox.RTMToolBox;
import net.cacpixel.rtmmetro.event.RTMMetroEventHandler;
import net.cacpixel.rtmmetro.proxy.CommonProxy;
import net.cacpixel.rtmmetro.traincontrollerdemo.LibRxtxLoader;
import net.cacpixel.rtmmetro.traincontrollerdemo.UartDevicesMCEventHandler;
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
        dependencies = "required-after:rtm@[2.4.24,);required-after:ngtlib@[2.4.21,)"
)
public class RTMMetro {
    public static final String MODID = "rtmmetro";
    public static final String NAME = "RTM Metro";
    public static final String VERSION = "0.1.0";
    private static boolean HAS_FIXRTM_MOD = false;
    public static short guiIdRigidCatenary = getNextGuiID();
    public static short guiIdRailAdvanced = getNextGuiID();

    @Mod.Instance(MODID)
    public static RTMMetro INSTANCE;

    @Mod.Metadata(MODID)
    public static ModMetadata METADATA;

    @SidedProxy(clientSide = "net.cacpixel.rtmmetro.proxy.ClientProxy", serverSide = "net.cacpixel.rtmmetro.proxy.CommonProxy")
    public static CommonProxy proxy;
    private static Logger logger;
    public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        LibRxtxLoader.load();
        RTMMetroResource.init();
        RTMMetroBlock.init();
        RTMMetroItems.init();
        RTMMetroPackets.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new RTMMetroGUIHandler());
        proxy.preInit();
//        RTMToolBox.getInstance().init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        printTagMap();
        proxy.init();
        RTMMetroToolTip.init();
        MinecraftForge.EVENT_BUS.register(new RTMMetroEventHandler());
        MinecraftForge.EVENT_BUS.register(new UartDevicesMCEventHandler());
    }

    @EventHandler
    public void complete(FMLLoadCompleteEvent event) {
        proxy.complete();

        if (Loader.isModLoaded("fix-rtm")) {
            HAS_FIXRTM_MOD = true;
        }
    }

    public static boolean hasFixrtm() {
        return HAS_FIXRTM_MOD;
    }

    private static short guiId;

    private static short getNextGuiID() {
        return guiId++;
    }

    private static void printTagMap() {
        // 反射获取ModelPackManager.typeMap字段，debug用
        try {
            Field field = ModelPackManager.class.getDeclaredField("typeMap");
            field.setAccessible(true);
            Map<String, ResourceType> map = (Map<String, ResourceType>) field.get(ModelPackManager.INSTANCE);
            Set<String> strSet = map.keySet();
            NGTLog.debug("[RTMMetro] Registered ResourceTypes:");
            for (String str : strSet) {
                NGTLog.debug("  - " + str);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
