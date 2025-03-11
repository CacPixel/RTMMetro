package net.cacpixel.rtmmetro.mixin.early;

import jp.ngt.rtm.RTMResource;
import net.cacpixel.rtmmetro.modelpack.init.ModelPackManagerEx;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RTMResource.class, remap = false)
public class MixinRTMResource
{
    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private static void init(CallbackInfo ci)
    {
        if (ModelPackManagerEx.INSTANCE.isInitialized())
        {
            ci.cancel();
        }
    }
}
