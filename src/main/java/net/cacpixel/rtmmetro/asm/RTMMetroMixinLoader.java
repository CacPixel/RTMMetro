package net.cacpixel.rtmmetro.asm;

import net.cacpixel.rtmmetro.util.ModLog;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@MixinLoader
@SuppressWarnings("unused")
public class RTMMetroMixinLoader implements ILateMixinLoader
{
//    public RTMMetroMixinLoader()
//    {
//        this.getMixinConfigs().forEach(Mixins::addConfiguration);
//    }

    @Override
    public List<String> getMixinConfigs()
    {
        return Stream.of("mixins.rtmmetro.json").collect(Collectors.toList());
    }

    @Override
    public void onMixinConfigQueued(String mixinConfig)
    {
        ModLog.info("Successfully added " + mixinConfig + " to mixin config.");
    }
}
