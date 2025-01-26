package net.cacpixel.rtmmetro;

import jp.ngt.ngtlib.item.ItemColoredBlock;
import jp.ngt.ngtlib.util.NGTRegHandler;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.rail.BlockMarker;
import net.cacpixel.rtmmetro.rail.block.*;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityLargeRailCoreAdvanced;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityLargeRailSwitchCoreAdvanced;
import net.cacpixel.rtmmetro.rail.tileentity.TileEntityMarkerAdvanced;
import net.cacpixel.rtmmetro.render.RenderLargeRailAdvanced;
import net.cacpixel.rtmmetro.render.RenderMarkerBlockAdvanced;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RTMMetroBlock
{

    public static BlockMarkerAdvanced MARKER_ADVANCED;
    public static BlockMarkerAdvanced MARKER_ADVANCED_SWITCH;
    public static Block LARGE_RAIL_BASE_ADVANCED;
    public static Block LARGE_RAIL_CORE_ADVANCED;
    public static Block LARGE_RAIL_SWITCH_BASE_ADVANCED;
    public static Block LARGE_RAIL_SWITCH_CORE_ADVANCED;

    public static void init()
    {
        // no tab
        LARGE_RAIL_BASE_ADVANCED = NGTRegHandler.register(new BlockLargeRailBaseAdvanced(), "large_rail_base_advanced",
                "rtmmetro.LRBaseAdvanced", null, RTMMetro.MODID);
        LARGE_RAIL_CORE_ADVANCED = NGTRegHandler.register(new BlockLargeRailCoreAdvanced(),
                "large_rail_main_core_advanced", "rtmmetro.LRMainCoreAdvanced", null, RTMMetro.MODID);
        LARGE_RAIL_SWITCH_BASE_ADVANCED = NGTRegHandler.register(new BlockLargeRailSwitchBaseAdvanced(),
                "large_rail_switch_base_advanced", "rtmmetro.LRSBaseAdvanced", null, RTMMetro.MODID);
        LARGE_RAIL_SWITCH_CORE_ADVANCED = NGTRegHandler.register(new BlockLargeRailSwitchCoreAdvanced(),
                "large_rail_switch_core_advanced", "rtmmetro.LRSCoreAdvanced", null, RTMMetro.MODID);

        // RTM_METRO_TAB_RAILWAY
        MARKER_ADVANCED = NGTRegHandler.register(new BlockMarkerAdvanced(BlockMarker.MarkerType.STANDARD),
                "marker_advanced", "rtmmetro.marker_advanced", RTMMetroCreativeTabs.RTM_METRO_TAB_RAILWAY,
                ItemColoredBlock.class, RTMMetro.MODID);
        MARKER_ADVANCED_SWITCH = NGTRegHandler.register(new BlockMarkerAdvanced(BlockMarker.MarkerType.SWITCH),
                "marker_switch_advanced", "rtmmetro.marker_switch_advanced", RTMMetroCreativeTabs.RTM_METRO_TAB_RAILWAY,
                ItemColoredBlock.class, RTMMetro.MODID);

        // RTM_METRO_TAB_TOOLS

        GameRegistry.registerTileEntity(TileEntityLargeRailCoreAdvanced.class, "TERailMainCoreAdvanced");
        GameRegistry.registerTileEntity(TileEntityLargeRailSwitchCoreAdvanced.class, "TERailSwitchCoreAdvanced");
        GameRegistry.registerTileEntity(TileEntityMarkerAdvanced.class, "TEMarkerAdvanced");
    }

    @SideOnly(Side.CLIENT)
    public static void initClient()
    {
        registerBlockModel(MARKER_ADVANCED, 0, "marker0");
        registerBlockModel(MARKER_ADVANCED, 1, "marker1");
        registerBlockModel(MARKER_ADVANCED, 2, "marker2");
        registerBlockModel(MARKER_ADVANCED, 3, "marker3");
        registerBlockModel(MARKER_ADVANCED, 4, "marker20");
        registerBlockModel(MARKER_ADVANCED, 5, "marker21");
        registerBlockModel(MARKER_ADVANCED, 6, "marker22");
        registerBlockModel(MARKER_ADVANCED, 7, "marker23");
        registerBlockModel(MARKER_ADVANCED_SWITCH, 0, "marker0");
        registerBlockModel(MARKER_ADVANCED_SWITCH, 1, "marker1");
        registerBlockModel(MARKER_ADVANCED_SWITCH, 2, "marker2");
        registerBlockModel(MARKER_ADVANCED_SWITCH, 3, "marker3");
        registerBlockModel(MARKER_ADVANCED_SWITCH, 4, "marker20");
        registerBlockModel(MARKER_ADVANCED_SWITCH, 5, "marker21");
        registerBlockModel(MARKER_ADVANCED_SWITCH, 6, "marker22");
        registerBlockModel(MARKER_ADVANCED_SWITCH, 7, "marker23");

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLargeRailCoreAdvanced.class,
                RenderLargeRailAdvanced.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLargeRailSwitchCoreAdvanced.class,
                RenderLargeRailAdvanced.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMarkerAdvanced.class,
                RenderMarkerBlockAdvanced.INSTANCE);

        NGTUtilClient.registerBuildinModel(RTMMetroBlock.LARGE_RAIL_BASE_ADVANCED, true);
        NGTUtilClient.registerBuildinModel(RTMMetroBlock.LARGE_RAIL_CORE_ADVANCED, true);
        NGTUtilClient.registerBuildinModel(RTMMetroBlock.LARGE_RAIL_SWITCH_BASE_ADVANCED, true);
        NGTUtilClient.registerBuildinModel(RTMMetroBlock.LARGE_RAIL_SWITCH_CORE_ADVANCED, true);

    }

    @SideOnly(Side.CLIENT)
    public static void initClient2()
    {
        BlockColors colors = NGTUtilClient.getMinecraft().getBlockColors();

        colors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
            BlockMarkerAdvanced block = (BlockMarkerAdvanced) state.getBlock();
            return block.markerType.color;
        }, MARKER_ADVANCED, MARKER_ADVANCED_SWITCH);


    }

    @SideOnly(Side.CLIENT)
    public static void registerBlockModel(Block block, int meta, String name)
    {
//        RTMItem.registerItemModel(Item.getItemFromBlock(block), meta, name);
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta,
                new ModelResourceLocation(RTMMetro.MODID + ":" + name, "inventory"));
    }


}
