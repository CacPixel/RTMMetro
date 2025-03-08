package net.cacpixel.rtmmetro.mixin.early;

import jp.ngt.ngtlib.io.ScriptUtil;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.script.ScriptEngineManager;

@Mixin(value = ScriptUtil.class, remap = false)
public class MixinScriptUtil
{
    @Shadow
    private static ScriptEngineManager SEM = null;

    @Inject(method = "init",
            at = @At("HEAD"),
            cancellable = true)
    private static void init(CallbackInfo ci)
    {
        LaunchClassLoader loader = Launch.classLoader;
//        loader.addClassLoaderExclusion("javax.");
        loader.addClassLoaderExclusion("jdk.nashorn.");
        SEM = new ScriptEngineManager(null);
        ci.cancel();
    }
}
