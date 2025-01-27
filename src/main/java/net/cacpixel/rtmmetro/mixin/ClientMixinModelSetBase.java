package net.cacpixel.rtmmetro.mixin;

import jp.ngt.rtm.modelpack.cfg.ModelConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetBase;
import jp.ngt.rtm.modelpack.modelset.ResourceSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModelSetBase.class, remap = false)
public abstract class ClientMixinModelSetBase<T extends ModelConfig> extends ResourceSet<T>
{
    @Inject(method = "finishConstruct", at = @At("HEAD"), cancellable = true)
    public void finishConstruct(CallbackInfo ci)
    {
        ci.cancel();
    }
}
