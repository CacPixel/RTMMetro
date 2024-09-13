package net.cacpixel.rtmmetro.mixin;

import jp.ngt.rtm.modelpack.init.Advertisement;
import jp.ngt.rtm.modelpack.init.MPLAdButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = MPLAdButton.class, remap = false)
public class MixinMPLAdButton
{
    /**
     * @author CacPixel
     * @reason 阻止RTM追加包加载过程联网获取广告（失效链接）以减少启动游戏耗时
     */
    @Overwrite
    private Advertisement[] getAds()
    {
        return null;
    }
}
