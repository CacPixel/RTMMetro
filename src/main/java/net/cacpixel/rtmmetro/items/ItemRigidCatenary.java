package net.cacpixel.rtmmetro.items;

import jp.ngt.ngtlib.item.ItemArgHolderBase;
import jp.ngt.rtm.item.ItemRail;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.state.ResourceState;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.rail.BlockLargeRailBase;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.RTMMetroResource;
import net.cacpixel.rtmmetro.modelpack.config.RigidCatenaryConfig;
import net.cacpixel.rtmmetro.modelpack.modelset.ModelSetRigidCatenary;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemRigidCatenary extends ItemAdvancedWithModel<ModelSetRigidCatenary, RigidCatenaryConfig> {
    public ItemRigidCatenary() {
        super();
        this.maxStackSize = 1;
    }

    public int getGuiId(ItemStack stack) {
        return RTMMetro.guiIdRigidCatenary;
//        return RTMCore.guiIdSelectItemModel;
    }

    @Override
    protected ResourceType getModelType(ItemStack itemStack) {
        return RTMMetroResource.RIGID_CATENARY;
    }

    @Override
    protected ResourceState getNewState(ItemStack itemStack, ResourceType resourceType) {
        return new ResourceState(resourceType, null);
    }

    @Override
    protected ActionResult<ItemStack> onItemRightClick(ItemArgHolderBase.ItemArgHolder holder) {
        if (!holder.getPlayer().capabilities.isCreativeMode) {
            return holder.pass();
        }
        if (holder.getWorld().isRemote) {
//            super.onItemRightClick(holder);
            holder.getPlayer().openGui(RTMMetro.INSTANCE,
                    this.getGuiId(holder.getItemStack()), holder.getWorld(), 0, 0, 0);
        }
        return holder.success();
    }

    @Override
    protected ActionResult<ItemStack> onItemUse(ItemArgHolderBase.ItemArgHolder holder, float hitX, float hitY, float hitZ) {
        if (!holder.getPlayer().capabilities.isCreativeMode) {
            return holder.pass();
        }

        World world = holder.getWorld();
        IBlockState blockState = world.getBlockState(holder.getBlockPos());
        TileEntityLargeRailCore core = BlockLargeRailBase.getCore(world, holder.getBlockPos());
        if (core == null) {
            return holder.pass();
        }
        if (!world.isRemote) {
//            NGTLog.debug("rigid catenary on item use !");
            ResourceStateRail state = ItemRail.getDefaultProperty();
            //值得注意的是，我们的轨道是一个新的东西，继承BlockLargeRailXXX，TileEntity也是新的
            //这边新增subRail的办法候选
            core.addSubRail(state);
        }
        return holder.success();
    }

    @SideOnly(Side.CLIENT)
    protected void addInformation(ItemArgHolderBase.ItemArgHolder holder, List<String> list, ITooltipFlag flag) {
        super.addInformation(holder, list, flag);
        if (ModelPackManager.INSTANCE.modelLoaded) {
            list.add(TextFormatting.GRAY + "这里放一点刚性接触网的各种参数, get nbt获取...");
        }

    }

}