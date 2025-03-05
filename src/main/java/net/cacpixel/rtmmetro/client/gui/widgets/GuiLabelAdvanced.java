package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.Align;
import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class GuiLabelAdvanced extends GuiWidget
{
    private final List<String> labels = new ArrayList<>();
    public ResourceLocation icon;
    public Align alignX = Align.LEFT_OR_UP_ALIGNED;
    public Align alignY = Align.LEFT_OR_UP_ALIGNED;
    public boolean visible = true;
    public int textColor;
    public int backColor;
    public int ulColor;
    public int brColor;
    public int border;
    public int lineHeight = 10;
    private boolean hasBackground = false;

    public GuiLabelAdvanced(IWidgetHolder holder, int id, IntSupplier xSupplier, IntSupplier ySupplier,
                            IntSupplier widthSupplier, IntSupplier heightSupplier, int colorIn)
    {
        super(holder, id, xSupplier, ySupplier, widthSupplier, heightSupplier);
        this.textColor = colorIn;
        this.backColor = -1;
        this.ulColor = -1;
        this.brColor = -1;
        this.border = 0;
    }

    public GuiLabelAdvanced addLine(String line)
    {
        this.labels.add(line);
        return this;
    }

    public GuiLabelAdvanced setText(String text)
    {
        this.labels.clear();
        this.labels.add(text);
        return this;
    }

    public GuiLabelAdvanced setAlignX(Align align)
    {
        this.alignX = align;
        return this;
    }

    public GuiLabelAdvanced setAlignY(Align align)
    {
        this.alignY = align;
        return this;
    }

    public void drawLabel(int mouseX, int mouseY, float partialTicks)
    {
        Minecraft mc = pScr.mc;
        if (this.visible)
        {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.drawLabelBackground(mc, mouseX, mouseY);
            int hh = this.y + this.height / 2 + this.border / 2;
            int strY = this.alignY == Align.LEFT_OR_UP_ALIGNED ? this.y :
                    this.alignY == Align.RIGHT_OR_DOWN_ALIGNED ? this.y + this.height - this.labels.size() * lineHeight + 1 :
                            hh - this.labels.size() * lineHeight / 2 + 1;
            for (int k = 0; k < this.labels.size(); ++k)
            {
                CacGuiUtils.drawString(this.labels.get(k), x, strY, width, 8,
                        textColor | pScr.getAlphaInt(0xFF), alignX, alignY);
                strY += lineHeight;
            }
        }
    }

    protected void drawLabelBackground(Minecraft mcIn, int mouseX, int mouseY)
    {
        if (this.hasBackground)
        {
            int i = this.width + this.border * 2;
            int j = this.height + this.border * 2;
            int k = this.x - this.border;
            int l = this.y - this.border;
            CacGuiUtils.drawRect(k, l, k + i, l + j, this.backColor);
            CacGuiUtils.drawHorizontalLine(k, k + i, l, this.ulColor);
            CacGuiUtils.drawHorizontalLine(k, k + i, l + j, this.brColor);
            CacGuiUtils.drawVerticalLine(k, l, l + j, this.ulColor);
            CacGuiUtils.drawVerticalLine(k + i, l, l + j, this.brColor);
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.drawLabel(mouseX, mouseY, partialTicks);
    }

}
