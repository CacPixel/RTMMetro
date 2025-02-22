package net.cacpixel.rtmmetro.mixin.early;

import net.cacpixel.rtmmetro.util.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP
{
    @Final
    @Shadow
    private Minecraft mc;

    @Inject(method = "clickBlock", at = @At("HEAD"), cancellable = true)
    public void clickBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> cir)
    {
        if (BlockUtils.cancelRailBlockBreak(this.mc.world, this.mc.player, loc))
        {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"), cancellable = true)
    public void onPlayerDamageBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> cir)
    {
        if (BlockUtils.cancelRailBlockBreak(this.mc.world, this.mc.player, loc))
        {
            cir.setReturnValue(true);
        }
    }
}
