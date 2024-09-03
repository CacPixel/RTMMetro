package net.cacpixel.rtmmetro.items;

import jp.ngt.ngtlib.item.ItemArgHolderBase;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.item.ItemRail;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.state.ResourceState;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.rail.BlockLargeRailBase;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import jp.ngt.rtm.rail.util.RailPosition;
import net.cacpixel.rtmmetro.RTMMetroItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemRailAdvanced extends ItemRail
{
    public ItemRailAdvanced()
    {
        super();
        this.maxStackSize = 1;
    }

    public int getGuiId(ItemStack stack)
    {
//        return RTMMetro.guiIdRailAdvanced;
        return RTMCore.guiIdSelectItemModel;
    }

    @Override
    protected ResourceType getModelType(ItemStack itemStack)
    {
        return RTMResource.RAIL;
    }

    @Override
    protected ResourceState getNewState(ItemStack itemStack, ResourceType resourceType)
    {
        return new ResourceStateRail(resourceType, null);
    }

    @Override
    protected ActionResult<ItemStack> onItemRightClick(ItemArgHolderBase.ItemArgHolder holder)
    {
//        if (!holder.getPlayer().capabilities.isCreativeMode) {
//            return holder.pass();
//        }
//        if (holder.getWorld().isRemote) {
//            holder.getPlayer().openGui(RTMCore.instance,
//                    this.getGuiId(holder.getItemStack()), holder.getWorld(), 0, 0, 0);
//        }
//        return holder.success();
        return super.onItemRightClick(holder);
    }

    @Override
    protected ActionResult<ItemStack> onItemUse(ItemArgHolderBase.ItemArgHolder holder, float hitX, float hitY,
                                                float hitZ)
    {
        if (!holder.getPlayer().capabilities.isCreativeMode)
        {
            return holder.pass();
        }

        World world = holder.getWorld();
        IBlockState blockState = world.getBlockState(holder.getBlockPos());
        TileEntityLargeRailCore core = BlockLargeRailBase.getCore(world, holder.getBlockPos());
        if (core == null)
        {
            return holder.pass();
        }
        if (!world.isRemote)
        {
//            NGTLog.debug("rigid catenary on item use !");
            ResourceStateRail state = ItemRail.getDefaultProperty();
            //值得注意的是，我们的轨道是一个新的东西，继承BlockLargeRailXXX，TileEntity也是新的
            //这边新增subRail的办法候选
            core.addSubRail(state);
        }
        return holder.success();
    }

    @Override
    protected void addInformation(ItemArgHolderBase.ItemArgHolder holder, List list, ITooltipFlag flag)
    {
        super.addInformation(holder, list, flag);
//        ResourceStateRail state = (ResourceStateRail) this.getModelState(holder.getItemStack());
//        if (state == null) {
//            return;
//        }
//        list.add(TextFormatting.GRAY + "Height:" + state.blockHeight);
//
//        if (holder.getItemStack().hasTagCompound() && holder.getItemStack().getTagCompound().hasKey("ShapeName")) {
//            String shape = holder.getItemStack().getTagCompound().getString("ShapeName");
//            list.add(TextFormatting.GRAY + shape);
//        }
    }

    public static ItemStack getRailItem(ResourceStateRail prop)
    {
        ItemStack itemStack = new ItemStack(RTMMetroItems.railAdvanced, 1, 0);
        if (RTMMetroItems.railAdvanced instanceof ItemRailAdvanced)
        {
            ((ItemRailAdvanced) RTMMetroItems.railAdvanced).setModelState(itemStack, prop);
            return itemStack;
        }
        else
        {
            return new ItemStack(Blocks.AIR);
        }
    }

    public static ItemStack copyItemFromRail(TileEntityLargeRailCore core)
    {
        ItemStack stack = getRailItem(core.getResourceState());
        RailPosition[] rps = core.getRailPositions();
        setRPToItem(stack, rps);
        String shape = core.getRailShapeName();
        stack.getTagCompound().setString("ShapeName", shape);
        return stack;
    }

    private static void setRPToItem(ItemStack stack, RailPosition[] rps)
    {
        if (!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbt = stack.getTagCompound();

        nbt.setByte("Size", (byte) rps.length);
        for (int i = 0; i < rps.length; ++i)
        {
            nbt.setTag("RP" + i, rps[i].writeToNBT());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
    {
        if (!this.isInCreativeTab(tab))
        {
            return;
        }
        ItemStack itemStack = new ItemStack(RTMMetroItems.railAdvanced, 1, 0);
        ResourceStateRail state = getDefaultProperty();
        state.setResourceName("1067mm_Wood");
        state.setBlock(Blocks.GRAVEL, 0);
        state.setHeight(ResourceStateRail.INIT_HEIGHT);
        this.setModelState(itemStack, state);
        list.add(itemStack);
    }

}
