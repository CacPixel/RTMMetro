package net.cacpixel.rtmmetro;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RTMMetroCreativeTabs extends CreativeTabs {
    public static final CreativeTabs RTM_METRO_TAB_ITEMS = new RTMMetroCreativeTabs("rtm_metro_tab_items");
    public static final CreativeTabs RTM_METRO_TAB_TOOLS = new RTMMetroCreativeTabs("rtm_metro_tab_tools");

    public RTMMetroCreativeTabs(String label) {
        super(label);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        if (this == RTM_METRO_TAB_ITEMS) {
            return new ItemStack(Items.WOODEN_AXE);
        }
        return new ItemStack(Items.AIR);
    }
}
