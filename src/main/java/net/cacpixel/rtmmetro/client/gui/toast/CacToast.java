package net.cacpixel.rtmmetro.client.gui.toast;

import net.cacpixel.rtmmetro.RTMMetroItems;
import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class CacToast implements IToastAdvanced
{
    public static final ResourceLocation RTMMETRO_TEXTURE_TOASTS = new ResourceLocation("textures/gui/toasts.png");
    private String title;
    private String subtitle;
    private long firstDrawTime;
    private boolean newDisplay;

    public CacToast(ITextComponent titleComponent, @Nullable ITextComponent subtitleComponent)
    {
        this.title = titleComponent.getUnformattedText();
        this.subtitle = subtitleComponent == null ? null : subtitleComponent.getUnformattedText();
    }

    public IToast.Visibility draw(GuiToastAdvanced toastGui, long delta)
    {
        if (this.newDisplay)
        {
            this.firstDrawTime = delta;
            this.newDisplay = false;
        }

        toastGui.getMinecraft().getTextureManager().bindTexture(RTMMETRO_TEXTURE_TOASTS);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        toastGui.drawTexturedModalRect(0, 0, 0, 96, 160, 32);

        if (this.subtitle == null)
        {
            toastGui.getMinecraft().fontRenderer.drawString(this.title, 30, 12, 0x0E9700);
        }
        else
        {
            toastGui.getMinecraft().fontRenderer.drawString(this.title, 30, 7, 0x0E9700);
            toastGui.getMinecraft().fontRenderer.drawString(this.subtitle, 30, 18, 0x000000);
        }
        RenderHelper.enableGUIStandardItemLighting();
        toastGui.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(null, new ItemStack(RTMMetroItems.railAdvanced),
                8, 8);
        long duration = delta - this.firstDrawTime;
        CacGuiUtils.drawRect(3, 28, (int) ((float) duration / 5000 * 154), 30, 0xFF202020);
        return duration < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
    }

    public void setDisplayedText(ITextComponent titleComponent, @Nullable ITextComponent subtitleComponent)
    {
        this.title = titleComponent.getUnformattedText();
        this.subtitle = subtitleComponent == null ? null : subtitleComponent.getUnformattedText();
        this.newDisplay = true;
    }

    public static void addOrUpdate(Class<? extends CacToast> clazz, Object type, ITextComponent title, @Nullable ITextComponent subTitle)
    {
        CacToast toast = GuiToastAdvanced.INSTANCE.getToast(clazz, type);

        if (toast == null)
        {
            GuiToastAdvanced.INSTANCE.add(new CacToast(title, subTitle));
        }
        else
        {
            toast.setDisplayedText(title, subTitle);
        }
    }

    public static void addOrUpdate(ITextComponent title, @Nullable ITextComponent subTitle)
    {
        addOrUpdate(CacToast.class, IToast.NO_TOKEN, title, subTitle);
    }
}
