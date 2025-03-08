package net.cacpixel.rtmmetro.mixin.early;

import jp.ngt.rtm.modelpack.init.ModelPackLoadThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModelPackLoadThread.class, remap = false)
public class MixinModelPackLoadThread
{
    @Unique
    private static boolean rtmmetro$initialized = false;

    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    public void run(CallbackInfo ci)
    {
        if (rtmmetro$initialized)
        {
            ci.cancel();
        }
        rtmmetro$initialized = true;
    }
}
