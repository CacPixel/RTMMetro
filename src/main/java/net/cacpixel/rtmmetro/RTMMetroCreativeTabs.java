package net.cacpixel.rtmmetro;

import jp.ngt.rtm.RTMItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RTMMetroCreativeTabs extends CreativeTabs
{
    public static final CreativeTabs RTM_METRO_TAB_RAILWAY = new RTMMetroCreativeTabs("rtm_metro_tab_railway");
    public static final CreativeTabs RTM_METRO_TAB_TOOLS = new RTMMetroCreativeTabs("rtm_metro_tab_tools");

    public RTMMetroCreativeTabs(String label)
    {
        super(label);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack createIcon()
    {
        if (this == RTM_METRO_TAB_RAILWAY)
            return new ItemStack(RTMItem.itemtrain, 1, 1);
        else if (this == RTM_METRO_TAB_TOOLS)
            return new ItemStack(RTMMetroItems.railBuilder);
        else
            return new ItemStack(Items.AIR);
    }
}
