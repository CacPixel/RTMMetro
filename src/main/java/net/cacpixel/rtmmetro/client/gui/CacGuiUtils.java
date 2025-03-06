package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.io.ScriptUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiTextFieldAdvanced;
import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.cacpixel.rtmmetro.math.CacMath;
import net.cacpixel.rtmmetro.util.ModLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public class CacGuiUtils
{
    public static final int DEFAULT_SCROLL_VALUE = 120;
    public static final int DEFAULT_LINE_HEIGHT = 10;
    public static BezierCurveAdvanced guiBezierAlpha;
    public static BezierCurveAdvanced guiBezierTranslation;
    public static BezierCurveAdvanced guiBezierScroll;
    public static final double X_MAX = 1000;

    public static void init()
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

    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, float zLevel,
                                             int textureWidth, int textureHeight)
    {
        float uScale = 1f / textureWidth;
        float vScale = 1f / textureHeight;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder wr = tessellator.getBuffer();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        wr.pos(x, y + height, zLevel).tex(u * uScale, ((v + height) * vScale)).endVertex();
        wr.pos(x + width, y + height, zLevel).tex((u + width) * uScale, ((v + height) * vScale)).endVertex();
        wr.pos(x + width, y, zLevel).tex((u + width) * uScale, (v * vScale)).endVertex();
        wr.pos(x, y, zLevel).tex(u * uScale, (v * vScale)).endVertex();
        tessellator.draw();
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, float zLevel)
    {
        drawTexturedModalRect(x, y, u, v, width, height, zLevel, 0x100, 0x100);
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

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(GuiTextFieldAdvanced field, T defaultVal)
    {
        T ret;
        String text = field.getText();
        String prefix = "importClass(Packages." + Math.class.getCanonicalName() + "); \r\n" +
                "importClass(Packages." + NGTMath.class.getCanonicalName() + "); \r\n" +
                "importClass(Packages." + CacMath.class.getCanonicalName() + "); \r\n" +
                "importClass(Packages." + MathHelper.class.getCanonicalName() + "); \r\n";
        try
        {
            // 禁用词：换行符 分号 字符串 importClass importPackage load class ClassLoader invoke null exec System java
            // test str: NGTMath.class.getClassLoader().loadClass("java.lang.Runtime").getMethod("getRuntime").invoke
            // (null).exec("calc");
            if (Stream.of("\r", "\n", ";", "\"", "import", "class", "package", "load", "invoke", "null", "exec",
                            "system", "java")
                    .anyMatch(text.toLowerCase()::contains))
            {
                ModLog.debug("Execution not allowed: " + text);
                return defaultVal;
            }
            ScriptEngine se = ScriptUtil.doScript(prefix + "x = " + text);
            String result = ScriptUtil.getScriptField(se, "x").toString();
//            ModLog.debug("Executing script: " + "x = " + text + "; Result is: " + result);
            if (defaultVal instanceof Byte)
            {
                ret = (T) Byte.valueOf(result);
            }
            else if (defaultVal instanceof Integer)
            {
                ret = (T) Integer.valueOf(result);
            }
            else if (defaultVal instanceof Long)
            {
                ret = (T) Long.valueOf(result);
            }
            else if (defaultVal instanceof Float)
            {
                ret = (T) Float.valueOf(result);
            }
            else if (defaultVal instanceof Double)
            {
                ret = (T) Double.valueOf(result);
            }
            else
            {
                ModLog.debug("GuiHelper.getFieldValue : Type not supported : %s", defaultVal.getClass().toString());
                return defaultVal;
            }
        }
        catch (Throwable e)
        {
//            ModLog.debug("Expression syntax error: " + ((e.getCause() == null) ? e.getMessage() : e.getCause()
//            .getMessage()));
            return defaultVal;
        }
        return ret;
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

    public static void drawString(String textIn, int x, int y, int w, int h, int color,
                                  Align alignX, Align alignY)
    {
        drawString(textIn, x, y, w, h, color, alignX, alignY, DEFAULT_LINE_HEIGHT);
    }

    public static void drawString(String textIn, int x, int y, int w, int h, int color,
                                  Align alignX, Align alignY, int lineHeight)
    {
        List<String> strList = Arrays.stream(textIn.replaceAll("\r\n", "\n").split("\n"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        drawString(strList, x, y, w, h, color, alignX, alignY, lineHeight);
    }

    public static void drawString(List<String> strListIn, int x, int y, int w, int h, int color,
                                  Align alignX, Align alignY, int lineHeight)
    {
        FontRenderer fontRendererIn = Minecraft.getMinecraft().fontRenderer;
        w = Math.max(w, fontRendererIn.FONT_HEIGHT + 1); // 保证宽度足够，否则wrapFormattedStringToWidth会报stackoverflow
        List<String> strList = new ArrayList<>();
        for (String str : strListIn)
        {
            strList.addAll(fontRendererIn.listFormattedStringToWidth(str, w));
        }
        int offset = alignY == Align.LEFT_OR_UP_ALIGNED ? 0 :
                alignY == Align.RIGHT_OR_DOWN_ALIGNED ? (lineHeight - fontRendererIn.FONT_HEIGHT) :
                        (lineHeight - fontRendererIn.FONT_HEIGHT) / 2;
        int strX = alignX == Align.LEFT_OR_UP_ALIGNED ? x :
                alignX == Align.RIGHT_OR_DOWN_ALIGNED ? x + w :
                        x + w / 2;
        int strY = alignY == Align.LEFT_OR_UP_ALIGNED ? y :
                alignY == Align.RIGHT_OR_DOWN_ALIGNED ? y + h - strList.size() * lineHeight + offset :
                        y + h / 2 - (strList.size() * lineHeight / 2) + offset;
        for (String text : strList)
        {
            switch (alignX)
            {
            case LEFT_OR_UP_ALIGNED:
                drawString(fontRendererIn, text, strX, strY, color);
                break;
            case RIGHT_OR_DOWN_ALIGNED:
                drawRightAlignedString(fontRendererIn, text, strX, strY, color);
                break;
            case CENTERED:
                drawCenteredString(fontRendererIn, text, strX, strY, color);
                break;
            default:
                break;
            }
            strY += lineHeight;
        }
    }

    public static void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float) (x - fontRendererIn.getStringWidth(text) / 2), (float) y,
                color);
    }

    public static void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float) x, (float) y, color);
    }

    public static void drawRightAlignedString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, x - fontRendererIn.getStringWidth(text), y, color);
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
}
