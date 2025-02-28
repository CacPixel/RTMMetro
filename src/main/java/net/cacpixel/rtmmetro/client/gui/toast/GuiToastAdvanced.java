package net.cacpixel.rtmmetro.client.gui.toast;


import com.google.common.collect.Queues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Deque;

public class GuiToastAdvanced extends Gui
{
    public static final GuiToastAdvanced INSTANCE = new GuiToastAdvanced(Minecraft.getMinecraft());
    private final Minecraft mc;
    private final GuiToastAdvanced.ToastInstance<?>[] visible = new GuiToastAdvanced.ToastInstance[5];
    private final Deque<IToastAdvanced> toastsQueue = Queues.newArrayDeque();

    public GuiToastAdvanced(Minecraft mcIn)
    {
        this.mc = mcIn;
    }

    public void drawToast(ScaledResolution resolution)
    {
        if (!this.mc.gameSettings.hideGUI)
        {
            RenderHelper.disableStandardItemLighting();

            for (int i = 0; i < this.visible.length; ++i)
            {
                GuiToastAdvanced.ToastInstance<?> toastinstance = this.visible[i];

                if (toastinstance != null && toastinstance.render(resolution.getScaledWidth(), i))
                {
                    this.visible[i] = null;
                }

                if (this.visible[i] == null && !this.toastsQueue.isEmpty())
                {
                    this.visible[i] = new GuiToastAdvanced.ToastInstance<>(this.toastsQueue.removeFirst());
                }
            }
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends IToast> T getToast(Class<? extends T> clazz, Object type)
    {
        for (GuiToastAdvanced.ToastInstance<?> toastinstance : this.visible)
        {
            if (toastinstance != null && clazz.isAssignableFrom(toastinstance.getToast().getClass()) &&
                    toastinstance.getToast().getType().equals(type))
            {
                return (T) toastinstance.getToast();
            }
        }

        for (IToast itoast : this.toastsQueue)
        {
            if (clazz.isAssignableFrom(itoast.getClass()) && itoast.getType().equals(type))
            {
                return (T) itoast;
            }
        }

        return (T) null;
    }

    public void clear()
    {
        Arrays.fill(this.visible, null);
        this.toastsQueue.clear();
    }

    public void add(IToastAdvanced toastIn)
    {
        this.toastsQueue.add(toastIn);
    }

    public Minecraft getMinecraft()
    {
        return this.mc;
    }

    @SideOnly(Side.CLIENT)
    class ToastInstance<T extends IToastAdvanced>
    {
        private final T toast;
        private long animationTime;
        private long visibleTime;
        private IToast.Visibility visibility;

        private ToastInstance(T toastIn)
        {
            this.animationTime = -1L;
            this.visibleTime = -1L;
            this.visibility = IToast.Visibility.SHOW;
            this.toast = toastIn;
        }

        public T getToast()
        {
            return this.toast;
        }

        private float getVisibility(long p_193686_1_)
        {
            float f = MathHelper.clamp((float) (p_193686_1_ - this.animationTime) / 600.0F, 0.0F, 1.0F);
            f = f * f;
            return this.visibility == IToast.Visibility.HIDE ? 1.0F - f : f;
        }

        public boolean render(int p_193684_1_, int p_193684_2_)
        {
            long i = Minecraft.getSystemTime();

            if (this.animationTime == -1L)
            {
                this.animationTime = i;
                this.visibility.playSound(GuiToastAdvanced.this.mc.getSoundHandler());
            }

            if (this.visibility == IToast.Visibility.SHOW && i - this.animationTime <= 600L)
            {
                this.visibleTime = i;
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate((float) p_193684_1_ - 160.0F * this.getVisibility(i), (float) (p_193684_2_ * 32),
                    (float) (500 + p_193684_2_));
            IToast.Visibility itoast$visibility = this.toast.draw(GuiToastAdvanced.this, i - this.visibleTime);
            GlStateManager.popMatrix();

            if (itoast$visibility != this.visibility)
            {
                this.animationTime = i - (long) ((int) ((1.0F - this.getVisibility(i)) * 600.0F));
                this.visibility = itoast$visibility;
                this.visibility.playSound(GuiToastAdvanced.this.mc.getSoundHandler());
            }

            return this.visibility == IToast.Visibility.HIDE && i - this.animationTime > 600L;
        }
    }
}
