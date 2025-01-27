package net.cacpixel.rtmmetro.mixin;

import jp.ngt.rtm.entity.util.CollisionHelper;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.modelset.ModelSetBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CollisionHelper.class, remap = false)
public class MixinCollisionHelper
{
    @Inject(method = "syncCollisionObj", at = @At("HEAD"), cancellable = true)
    public void syncCollisionObj(ResourceType type, ModelSetBase modelSet, CallbackInfo ci)
    {
        ci.cancel();
    }
}
