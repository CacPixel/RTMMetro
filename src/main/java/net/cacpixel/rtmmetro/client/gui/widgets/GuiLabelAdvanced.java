package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.Align;
import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.Image;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class GuiLabelAdvanced extends GuiWidget
{
    private final List<String> labels = new ArrayList<>();
    public Image icon;
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
    private boolean wrapString = false;
    private boolean autoExpand = false;

    public GuiLabelAdvanced(IWidgetHolder holder, IntSupplier xSupplier, IntSupplier ySupplier,
                            IntSupplier widthSupplier, IntSupplier heightSupplier, int colorIn)
    {
        super(holder, xSupplier, ySupplier, widthSupplier, heightSupplier);
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

    public GuiLabelAdvanced clearLabels()
    {
        this.labels.clear();
        return this;
    }

    public GuiLabelAdvanced removeLine(String line)
    {
        this.labels.removeIf(line::equals);
        return this;
    }

    public GuiLabelAdvanced removeLine(int index)
    {
        if (index < labels.size())
        {
            labels.remove(index);
        }
        return this;
    }

    public GuiLabelAdvanced removeIfContains(String str)
    {
        this.labels.removeIf(str::contains);
        return this;
    }

    public GuiLabelAdvanced setText(String text)
    {
        this.labels.clear();
        this.labels.add(text);
        return this;
    }

    public GuiLabelAdvanced setIcon(Image icon)
    {
        this.icon = icon;
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
        Minecraft mc = getScreen().mc;
        FontRenderer fontRenderer = mc.fontRenderer;
        if (this.visible)
        {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.drawLabelBackground(mc, mouseX, mouseY);
            CacGuiUtils.drawString(CacGuiUtils.extractNewLine(labels, true), x, y, width, height, textColor | getScreen().getAlphaInt(0xFF),
                    alignX, alignY, CacGuiUtils.DEFAULT_LINE_HEIGHT,
                    icon == null ? null : new Image(icon).setColor(icon.color | getScreen().getAlphaInt(0xFF)),
                    wrapString);
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

    public boolean isWrapString()
    {
        return wrapString;
    }

    public GuiLabelAdvanced setWrapString(boolean wrapString)
    {
        this.wrapString = wrapString;
        return this;
    }

    public boolean isAutoExpand()
    {
        return autoExpand;
    }

    public GuiLabelAdvanced setAutoExpand(boolean autoExpand)
    {
        this.autoExpand = autoExpand;
        return this;
    }

    public void doExpand()
    {
        List<String> list = CacGuiUtils.extractNewLine(this.labels, true);
        for (String str : list)
        {
            int strWidth = getScreen().mc.fontRenderer.getStringWidth(str) + 2;
            if (width < strWidth)
            {
                width = strWidth;
            }
        }
        int strHeightTotal = labels.size() * (getScreen().mc.fontRenderer.FONT_HEIGHT - 1) + 2;
        if (height < strHeightTotal)
        {
            height = strHeightTotal;
        }
    }

    @Override
    public void onMakeLayoutStart()
    {
        if (autoExpand)
        {
            doExpand();
        }
    }
}
