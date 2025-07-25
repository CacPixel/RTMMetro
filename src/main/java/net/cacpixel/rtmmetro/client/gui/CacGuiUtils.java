package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.util.NGTUtilClient;
import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class CacGuiUtils
{
    public static final int DEFAULT_SCROLL_VALUE = 120;
    public static final int DEFAULT_LINE_HEIGHT = 10;
    public static BezierCurveAdvanced guiBezierAlpha;
    public static BezierCurveAdvanced guiBezierTranslation;
    public static BezierCurveAdvanced guiBezierScroll;
    public static final double X_MAX = 1000;

    static
    {
        initBezierCurve();
    }

    public static void initBezierCurve()
    {
        guiBezierAlpha = new BezierCurveAdvanced(
                0 * X_MAX, 0,
                0.2 * X_MAX, 0.5,
                0.0 * X_MAX, 1.3,
                1.0 * X_MAX, 1.0);
        guiBezierTranslation = new BezierCurveAdvanced(
                0 * X_MAX, 0,
                0.25 * X_MAX, 0.1,
                0.0 * X_MAX, 1.0,
                1.0 * X_MAX, 1.0);
        guiBezierScroll = new BezierCurveAdvanced(
                0 * X_MAX, 0,
                0.38 * X_MAX, 0.97,
                0.81 * X_MAX, 0.98,
                1.0 * X_MAX, 1.0);

        guiBezierAlpha.initNP();
        guiBezierTranslation.initNP();
        guiBezierScroll.initNP();
    }

    public static void init()
    {
    }

    public static void glColor(int color)
    {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        float a = (float) (color >> 24 & 255) / 255.0F;
        GlStateManager.color(r, g, b, a);
    }

    public static void drawContinuousTexturedBox(ResourceLocation res, int x, int y, int u, int v, int width,
                                                 int height, int textureWidth,
                                                 int textureHeight,
                                                 int topBorder, int bottomBorder, int leftBorder, int rightBorder,
                                                 float zLevel,
                                                 GuiScreenAdvanced pScr)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(res);
        drawContinuousTexturedBox(x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder,
                leftBorder, rightBorder,
                zLevel, pScr);
    }

    public static void drawContinuousTexturedBox(int x, int y, int u, int v, int width, int height, int textureWidth,
                                                 int textureHeight,
                                                 int topBorder, int bottomBorder, int leftBorder, int rightBorder,
                                                 float zLevel,
                                                 GuiScreenAdvanced pScr)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, pScr.getAlpha());
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        int fillerWidth = textureWidth - leftBorder - rightBorder;
        int fillerHeight = textureHeight - topBorder - bottomBorder;
        int canvasWidth = width - leftBorder - rightBorder;
        int canvasHeight = height - topBorder - bottomBorder;
        int xPasses = canvasWidth / fillerWidth;
        int remainderWidth = canvasWidth % fillerWidth;
        int yPasses = canvasHeight / fillerHeight;
        int remainderHeight = canvasHeight % fillerHeight;

        // Draw Border
        // Top Left
        drawTexturedModalRect(x, y, u, v, leftBorder, topBorder, zLevel);
        // Top Right
        drawTexturedModalRect(x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder,
                zLevel);
        // Bottom Left
        drawTexturedModalRect(x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder,
                bottomBorder, zLevel);
        // Bottom Right
        drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth,
                v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);

        for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++)
        {
            // Top Border
            drawTexturedModalRect(x + leftBorder + (i * fillerWidth), y, u + leftBorder, v,
                    (i == xPasses ? remainderWidth : fillerWidth),
                    topBorder, zLevel);
            // Bottom Border
            drawTexturedModalRect(x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight, u + leftBorder,
                    v + topBorder + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), bottomBorder, zLevel);

            // Throw in some filler for good measure
            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
            {
                drawTexturedModalRect(x + leftBorder + (i * fillerWidth), y + topBorder + (j * fillerHeight),
                        u + leftBorder, v + topBorder,
                        (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight),
                        zLevel);
            }
        }

        // Side Borders
        for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
        {
            // Left Border
            drawTexturedModalRect(x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder,
                    (j == yPasses ? remainderHeight : fillerHeight), zLevel);
            // Right Border
            drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight),
                    u + leftBorder + fillerWidth,
                    v + topBorder, rightBorder, (j == yPasses ? remainderHeight : fillerHeight), zLevel);
        }
    }

    public static void drawTexturedModalRect(int x, int y, int width, int height, int u, int v, int uw, int vh,
                                             float zLevel, int textureWidth, int textureHeight)
    {
        drawTexturedModalRect((double) x, y, width, height, u, v, uw, vh, zLevel, textureWidth, textureHeight);
    }

    public static void drawTexturedModalRect(double x, double y, double width, double height, double u, double v,
                                             double uw, double vh,
                                             double zLevel, int textureWidth, int textureHeight)
    {
        double uScale = 1d / textureWidth;
        double vScale = 1d / textureHeight;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder wr = tessellator.getBuffer();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        wr.pos(x, y + height, zLevel).tex(u * uScale, ((v + vh) * vScale)).endVertex();
        wr.pos(x + width, y + height, zLevel).tex((u + uw) * uScale, ((v + vh) * vScale)).endVertex();
        wr.pos(x + width, y, zLevel).tex((u + uw) * uScale, (v * vScale)).endVertex();
        wr.pos(x, y, zLevel).tex(u * uScale, (v * vScale)).endVertex();
        tessellator.draw();
    }

    public static void drawTexturedModalRect(int x, int y, int width, int height, float zLevel)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder wr = tessellator.getBuffer();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        wr.pos(x, y + height, zLevel).tex(0, 1).endVertex();
        wr.pos(x + width, y + height, zLevel).tex(1, 1).endVertex();
        wr.pos(x + width, y, zLevel).tex(1, 0).endVertex();
        wr.pos(x, y, zLevel).tex(0, 0).endVertex();
        tessellator.draw();
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, float zLevel)
    {
        drawTexturedModalRect(x, y, width, height, u, v, width, height, zLevel, 0x100, 0x100);
    }

    public static void drawRect(int left, int top, int right, int bottom, int color)
    {
        if (left < right)
        {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
//        GlStateManager.disableBlend();
    }

    public static void drawHorizontalLine(int startX, int endX, int y, int color)
    {
        if (endX < startX)
        {
            int i = startX;
            startX = endX;
            endX = i;
        }

        drawRect(startX, y, endX + 1, y + 1, color);
    }

    public static void drawVerticalLine(int x, int startY, int endY, int color)
    {
        if (endY < startY)
        {
            int i = startY;
            startY = endY;
            endY = i;
        }

        drawRect(x, startY + 1, x + 1, endY, color);
    }

    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor, float zLevel)
    {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void bindTexture(ResourceLocation rl)
    {
        Objects.requireNonNull(rl);
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
    }

    public static List<String> extractNewLine(List<String> strIn, boolean extractCRLF)
    {
        StringBuilder sb = new StringBuilder();
        for (String str : strIn)
        {
            sb.append(str);
            sb.append("\n");
        }
        return extractNewLine(sb.toString(), extractCRLF);
    }

    public static List<String> extractNewLine(String strIn, boolean extractCRLF)
    {
        if (extractCRLF)
        {
            return Arrays.stream(strIn.replaceAll("\r\n", "\n").split("\n"))
                    .collect(Collectors.toList());
        }
        else
        {
            return Arrays.stream(strIn.split("\n"))
                    .collect(Collectors.toList());
        }
    }

    public static void drawString(String textIn, int x, int y, int w, int h, int color,
                                  Align alignX, Align alignY, Image image, boolean wrap)
    {
        drawString(textIn, x, y, w, h, color, alignX, alignY, DEFAULT_LINE_HEIGHT, image, wrap);
    }

    public static void drawString(String textIn, int x, int y, int w, int h, int color,
                                  Align alignX, Align alignY, int lineHeight, Image image, boolean wrap)
    {
        drawString(extractNewLine(textIn, true), x, y, w, h, color, alignX, alignY, lineHeight, image, wrap);
    }

    public static void drawString(List<String> strListIn, int x, int y, int w, int h, int color,
                                  Align alignX, Align alignY, int lineHeight, Image image, boolean wrap)
    {
        ResourceLocation icon = null;
        int iconColor = 0;
        if (image != null)
        {
            icon = image.location;
            iconColor = image.color;
        }
        FontRenderer fontRendererIn = Minecraft.getMinecraft().fontRenderer;
        int fontHeight = fontRendererIn.FONT_HEIGHT - 1;
        float iconScale = 1.5F;
        int iconSize = icon != null ? (int) (fontHeight * iconScale) : 0;
        int diff = (int) (iconSize * 0.5f);
        w = Math.max(w, fontHeight + iconSize + 1); // 保证宽度足够，否则wrapFormattedStringToWidth会报stackoverflow
        List<String> strList;
        if (wrap)
        {
            strList = new ArrayList<>();
            for (String str : strListIn)
            {
                strList.addAll(fontRendererIn.listFormattedStringToWidth(str, w - iconSize - diff));
            }
        }
        else
        {
            strList = strListIn;
        }

        int offset = alignY == Align.LEFT_OR_UP_ALIGNED ? 0 :
                alignY == Align.RIGHT_OR_DOWN_ALIGNED ? (lineHeight - fontHeight) :
                        (lineHeight - fontHeight) / 2;
        int strX = alignX == Align.LEFT_OR_UP_ALIGNED ? x :
                alignX == Align.RIGHT_OR_DOWN_ALIGNED ? x + w :
                        x + w / 2;
        int strY = alignY == Align.LEFT_OR_UP_ALIGNED ? y :
                alignY == Align.RIGHT_OR_DOWN_ALIGNED ? y + h - strList.size() * lineHeight + offset :
                        y + h / 2 - (strList.size() * lineHeight / 2) + offset;
        for (String text : strList)
        {
            int strStartX;
            switch (alignX)
            {
            case RIGHT_OR_DOWN_ALIGNED:
                strStartX = drawRightAlignedString(fontRendererIn, text, strX + iconSize, strY, color);
                break;
            case CENTERED:
                strStartX = drawCenteredString(fontRendererIn, text, strX + (iconSize + diff) / 2, strY, color);
                break;
            default:
                strStartX = drawString(fontRendererIn, text, strX + iconSize + diff, strY, color);
                break;
            }
            if (strList.indexOf(text) == 0 && icon != null)
            {
                CacGuiUtils.bindTexture(icon);
                glColor(iconColor);
                CacGuiUtils.drawTexturedModalRect(strStartX - iconSize - diff,
                        (int) (strY - iconSize / 2.0 + fontHeight / 2.0), iconSize, iconSize, image.u, image.v,
                        image.uWidth, image.vHeight, 0, image.textureWidth, image.textureHeight);
            }
            strY += lineHeight;
        }
    }

    public static int drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float) (x - fontRendererIn.getStringWidth(text) / 2), (float) y,
                color);
        return (x - fontRendererIn.getStringWidth(text) / 2);
    }

    public static int drawString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float) x, (float) y, color);
        return x;
    }

    public static int drawRightAlignedString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, x - fontRendererIn.getStringWidth(text), y, color);
        return x - fontRendererIn.getStringWidth(text);
    }

    public static int getMouseX()
    {
        Minecraft mc = NGTUtilClient.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int mouseX = Mouse.getX() / sr.getScaleFactor();
        int mouseY = (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor();
        return mouseX;
    }

    public static int getMouseY()
    {
        Minecraft mc = NGTUtilClient.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int mouseX = Mouse.getX() / sr.getScaleFactor();
        int mouseY = (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor();
        return mouseY;
    }

    public static boolean isMouseInside(int x, int y, int width, int height)
    {
        int mouseX = CacGuiUtils.getMouseX();
        int mouseY = CacGuiUtils.getMouseY();
        return x <= mouseX && mouseX < (x + width)
                && y <= mouseY && mouseY < (y + height);
    }

    public static boolean isMouseInside(int x, int y, int width, int height, int mouseX, int mouseY)
    {
        return x <= mouseX && mouseX < (x + width)
                && y <= mouseY && mouseY < (y + height);
    }
}
