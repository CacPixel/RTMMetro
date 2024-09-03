package net.cacpixel.rtmmetro.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.Name("FixNgtPacketCustomPlugin")
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE + 5)
public class FixNgtPacketCustomPlugin implements IFMLLoadingPlugin
{

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]{
                ClassTransformerFixNgtPacketCustom.class.getCanonicalName()
        };
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
