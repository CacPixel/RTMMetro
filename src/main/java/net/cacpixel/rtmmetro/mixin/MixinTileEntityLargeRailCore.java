package net.cacpixel.rtmmetro.mixin;

import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import jp.ngt.rtm.rail.TileEntityLargeRailCore;
import jp.ngt.rtm.rail.util.RailPosition;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityLargeRailCore.class, remap = false)
public class MixinTileEntityLargeRailCore extends TileEntityLargeRailBase
{
    @Shadow
    protected RailPosition[] railPositions;

    @Inject(method = "readRailData", at = @At("HEAD"), cancellable = true)
    public void readRailData(NBTTagCompound nbt, CallbackInfo ci)
    {
        if (!nbt.hasKey("StartRP") || !nbt.hasKey("EndRP"))
        {
            ci.cancel();
        }
    }

    @Inject(method = "writeRailData", at = @At("HEAD"), cancellable = true)
    public void writeRailData(NBTTagCompound nbt, CallbackInfo ci)
    {
        if (this.railPositions == null || this.railPositions.length < 2 || this.railPositions[0] == null || this.railPositions[1] == null)
        {
            ci.cancel();
        }
    }
}
