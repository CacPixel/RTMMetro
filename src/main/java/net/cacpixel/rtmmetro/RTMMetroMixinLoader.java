package net.cacpixel.rtmmetro;

import org.spongepowered.asm.mixin.Mixins;
import zone.rong.mixinbooter.MixinLoader;

@MixinLoader
public class RTMMetroMixinLoader
{
    public RTMMetroMixinLoader()
    {
        Mixins.addConfiguration("mixins.rtmmetro.json");
    }
}
