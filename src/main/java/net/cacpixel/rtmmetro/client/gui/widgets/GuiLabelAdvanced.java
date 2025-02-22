package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class GuiLabelAdvanced extends GuiWidget
{
    private final List<String> labels = new ArrayList<>();
    private Align align = Align.LEFT_ALIGNED;
    public boolean visible = true;
    private final int textColor;
    private final int backColor;
    private final int ulColor;
    private final int brColor;
    private final int border;
    private final boolean hasBackground = false;

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

    public GuiLabelAdvanced setCentered()
    {
        this.align = Align.CENTERED;
        return this;
    }

    public GuiLabelAdvanced setRightAligned()
    {
        this.align = Align.RIGHT_ALIGNED;
        return this;
    }

    public GuiLabelAdvanced setLeftAligned()
    {
        this.align = Align.LEFT_ALIGNED;
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
            int i = this.y + this.height / 2 + this.border / 2;
            int j = i - this.labels.size() * 10 / 2;

            for (int k = 0; k < this.labels.size(); ++k)
            {
                switch (align)
                {
                case LEFT_ALIGNED:
                    CacGuiUtils.drawString(mc.fontRenderer, this.labels.get(k), this.x, j + k * 10, this.textColor | pScr.getAlphaInt(0xFF));
                    break;
                case RIGHT_ALIGNED:
                    CacGuiUtils.drawRightAlignedString(mc.fontRenderer, this.labels.get(k), this.x, j + k * 10,
                            this.textColor | pScr.getAlphaInt(0xFF));
                case CENTERED:
                    CacGuiUtils.drawCenteredString(mc.fontRenderer, this.labels.get(k), this.x + this.width / 2,
                            j + k * 10,
                            this.textColor | pScr.getAlphaInt(0xFF));
                    break;
                default:
                    break;
                }
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

    public enum Align
    {
        LEFT_ALIGNED,
        CENTERED,
        RIGHT_ALIGNED,
    }
}
