package net.cacpixel.rtmmetro;

import jp.ngt.ngtlib.util.NGTRegHandler;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.cacpixel.rtmmetro.items.ItemRailAdvanced;
import net.cacpixel.rtmmetro.items.ItemRailEditor;
import net.cacpixel.rtmmetro.items.ItemRigidCatenary;
import net.cacpixel.rtmmetro.rail.block.BlockMarkerAdvanced;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class RTMMetroItems
{
    public static Item rigidCatenary;
    public static Item railAdvanced;
    public static Item railEditor;
    public static Item railBuilder;
    public static Item railSplitter;

    public static void init()
    {
        // RTM_METRO_TAB_RAILWAY
        rigidCatenary = NGTRegHandler.register(new ItemRigidCatenary(), "rigid_catenary", "rtmmetro.rigid_catenary",
                RTMMetroCreativeTabs.RTM_METRO_TAB_RAILWAY, RTMMetro.MODID);
        railAdvanced = NGTRegHandler.register(new ItemRailAdvanced(), "rail_advanced", "rtmmetro.rail_advanced",
                RTMMetroCreativeTabs.RTM_METRO_TAB_RAILWAY, RTMMetro.MODID);

        // RTM_METRO_TAB_TOOLS
        railEditor = NGTRegHandler.register(new ItemRailEditor(), "rail_editor", "rtmmetro.rail_editor",
                RTMMetroCreativeTabs.RTM_METRO_TAB_TOOLS, RTMMetro.MODID);
        railBuilder = NGTRegHandler.register(new ItemRailEditor(), "rail_builder", "rtmmetro.rail_builder",
                RTMMetroCreativeTabs.RTM_METRO_TAB_TOOLS, RTMMetro.MODID);
        railSplitter = NGTRegHandler.register(new ItemRailEditor(), "rail_splitter", "rtmmetro.rail_splitter",
                RTMMetroCreativeTabs.RTM_METRO_TAB_TOOLS, RTMMetro.MODID);
    }

    @SideOnly(Side.CLIENT)
    public static void initClient()
    {
        registerItemModel(rigidCatenary, 0, "rigid_catenary");
        registerItemModel(railAdvanced, 0, "rail_advanced");
        registerItemModel(railEditor, 0, "rail_editor");
        registerItemModel(railBuilder, 0, "rail_builder");
        registerItemModel(railSplitter, 0, "rail_splitter");
    }

    @SideOnly(Side.CLIENT)
    public static void initClient2()
    {
        ItemColors colors = NGTUtilClient.getMinecraft().getItemColors();

        colors.registerItemColorHandler((stack, tintIndex) -> {
            BlockMarkerAdvanced block = (BlockMarkerAdvanced) Block.getBlockFromItem(stack.getItem());
            return block.markerType.color;
        }, RTMMetroBlock.MARKER_ADVANCED, RTMMetroBlock.MARKER_ADVANCED_SWITCH);

    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModel(Item item, int meta, String name)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(RTMMetro.MODID + ":" + name, "inventory"));
    }

}
