package net.cacpixel.rtmmetro;

import jp.ngt.ngtlib.util.NGTRegHandler;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.cacpixel.rtmmetro.items.ItemRailAdvanced;
import net.cacpixel.rtmmetro.items.ItemRailEditor;
import net.cacpixel.rtmmetro.items.ItemRigidCatenary;
import net.cacpixel.rtmmetro.rail.block.BlockMarkerAdvanced;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class RTMMetroItems {

    public static Item rigidCatenary;
    public static Item railEditor;
    public static Item railAdvanced;

    public static void init() {
        // 物品
        rigidCatenary = NGTRegHandler.register(new ItemRigidCatenary(), "rigid_catenary", "rtmmetro.rigid_catenary", RTMMetroCreativeTabs.RTM_METRO_TAB_ITEMS, RTMMetro.MODID);
        railAdvanced = NGTRegHandler.register(new ItemRailAdvanced(), "rail_advanced", "rtmmetro.rail_advanced", RTMMetroCreativeTabs.RTM_METRO_TAB_ITEMS, RTMMetro.MODID);


        // 工具类物品
        railEditor = NGTRegHandler.register(new ItemRailEditor(), "rail_editor", "rtmmetro.rail_editor", RTMMetroCreativeTabs.RTM_METRO_TAB_TOOLS, RTMMetro.MODID);
    }

    @SideOnly(Side.CLIENT)
    public static void initClient()
    {

    }

    @SideOnly(Side.CLIENT)
    public static void initClient2()
    {
        ItemColors colors = NGTUtilClient.getMinecraft().getItemColors();

        colors.registerItemColorHandler((stack, tintIndex)->{
            BlockMarkerAdvanced block = (BlockMarkerAdvanced) Block.getBlockFromItem(stack.getItem());
            return block.markerType.color;
        }, RTMMetroBlock.MARKER_ADVANCED, RTMMetroBlock.MARKER_ADVANCED_SWITCH);

    }

}
