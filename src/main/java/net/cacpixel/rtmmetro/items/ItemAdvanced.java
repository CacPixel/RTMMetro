package net.cacpixel.rtmmetro.items;

import jp.ngt.ngtlib.item.ItemArgHolderBase;
import jp.ngt.ngtlib.item.ItemCustom;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemAdvanced extends ItemCustom
{

    @Override
    protected ActionResult<ItemStack> onItemRightClick(ItemArgHolderBase.ItemArgHolder holder)
    {
        return holder.success();
    }

    @Override
    protected ActionResult<ItemStack> onItemUse(ItemArgHolderBase.ItemArgHolder holder, float hitX, float hitY,
                                                float hitZ)
    {
        return new ActionResult(EnumActionResult.PASS, holder.getItemStack());
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack)
    {
        return super.getItemStackDisplayName(itemStack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);
    }

}

