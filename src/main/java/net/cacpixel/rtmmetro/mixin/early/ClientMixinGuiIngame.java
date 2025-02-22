package net.cacpixel.rtmmetro.mixin.early;

import net.cacpixel.rtmmetro.client.gui.toast.GuiToastAdvanced;
import net.cacpixel.rtmmetro.util.ModLog;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiIngame.class)
public class ClientMixinGuiIngame
{
    @Inject(method = "resetPlayersOverlayFooterHeader", at = @At(value = "TAIL"))
    public void resetPlayersOverlayFooterHeader(CallbackInfo ci)
    {
        GuiToastAdvanced.INSTANCE.clear();
    }
}
