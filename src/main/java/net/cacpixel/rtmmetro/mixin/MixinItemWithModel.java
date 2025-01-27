package net.cacpixel.rtmmetro.mixin;

import jp.ngt.ngtlib.item.ItemCustom;
import jp.ngt.ngtlib.network.PacketNBT;
import jp.ngt.rtm.item.ItemWithModel;
import jp.ngt.rtm.modelpack.modelset.ResourceSet;
import jp.ngt.rtm.modelpack.state.ResourceState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemWithModel.class, remap = false)
public class MixinItemWithModel<T extends ResourceSet> extends ItemCustom
{
    @Shadow
    private ItemStack selectedItem;

    @Shadow
    private EntityPlayer selectedPlayer;

    @Inject(method = "setModelState", at = @At(value = "INVOKE", target =
            "Ljp/ngt/ngtlib/network/PacketNBT;sendToServer(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V"),
            cancellable = true)
    public void setModelState(ItemStack itemStack, ResourceState<T> state, CallbackInfo ci)
    {
        ci.cancel();
    }

    @Inject(method = "closeGui", at = @At("HEAD"), cancellable = true)
    public void closeGui(ResourceState par1, CallbackInfoReturnable<Boolean> cir)
    {
        this.rtmmetro$setModelStateAndSendPacket(this.selectedItem, par1);
        cir.setReturnValue(true);
    }

    @Unique
    public void rtmmetro$setModelStateAndSendPacket(ItemStack itemStack, ResourceState<T> state)
    {
        if (!itemStack.hasTagCompound())
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        itemStack.getTagCompound().setTag("State", state.writeToNBT());
        if (this.selectedPlayer != null)
        {
            PacketNBT.sendToServer(this.selectedPlayer, this.selectedItem);
        }
    }
}
