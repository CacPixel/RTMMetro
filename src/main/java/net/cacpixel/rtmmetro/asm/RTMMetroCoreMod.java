package net.cacpixel.rtmmetro.asm;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.Side;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.Name("RTMMetroCoreMod")
public class RTMMetroCoreMod implements IFMLLoadingPlugin, IEarlyMixinLoader
{
    public static final boolean isClient = FMLLaunchHandler.side() == Side.CLIENT;

    @Override
    public List<String> getMixinConfigs()
    {
        return Collections.singletonList("mixins.rtmmetro_early.json");
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]{
                RTMMetroClassTransformer.class.getCanonicalName()
        };
    }

    @Override
    public String getModContainerClass() {return null;}

    @Nullable
    @Override
    public String getSetupClass() {return null;}

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {return null;}
}
