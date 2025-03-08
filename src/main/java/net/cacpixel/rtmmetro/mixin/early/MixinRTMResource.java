package net.cacpixel.rtmmetro.mixin.early;

import jp.ngt.rtm.RTMResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RTMResource.class, remap = false)
public class MixinRTMResource
{
    @Unique
    private static boolean rtmmetro$initialized = false;

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private static void init(CallbackInfo ci)
    {
        if (rtmmetro$initialized)
        {
            ci.cancel();
        }
        rtmmetro$initialized = true;
    }
}
