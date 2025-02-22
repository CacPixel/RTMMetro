package net.cacpixel.rtmmetro.client.gui.toast;

import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;

public interface IToastAdvanced extends IToast
{
    default IToast.Visibility draw(GuiToast toastGui, long delta) {return Visibility.HIDE;}

    IToast.Visibility draw(GuiToastAdvanced toastGui, long delta);
}
