package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import static net.minecraftforge.fml.client.config.GuiUtils.drawTexturedModalRect;


@SideOnly(Side.CLIENT)
public class CacGuiUtils
{
    public static BezierCurveAdvanced guiBezierAlphaIn;
    public static BezierCurveAdvanced guiBezierAlphaOut;
    public static BezierCurveAdvanced guiBezierTranslationIn;
    public static BezierCurveAdvanced guiBezierTranslationOut;
    public static double xMax = 100;

    public static void init()
    {
        guiBezierAlphaIn = new BezierCurveAdvanced(
                0 * xMax, 0,
                0.2 * xMax, 0.5,
                0.0 * xMax, 1.3,
                1.0 * xMax, 1.0);
        guiBezierAlphaOut = new BezierCurveAdvanced(
                0 * xMax, 1,
                0.2 * xMax, 0.5,
                0 * xMax, -0.3,
                1 * xMax, 0);
        guiBezierTranslationIn = new BezierCurveAdvanced(
                0 * xMax, 0,
                0.25 * xMax, 0.1,
                0.0 * xMax, 1.0,
                1.0 * xMax, 1.0);
        guiBezierTranslationOut = new BezierCurveAdvanced(
                0 * xMax, 1,
                0.25 * xMax, 0.9,
                0 * xMax, 0,
                1 * xMax, 0);

        guiBezierAlphaIn.initNP();
        guiBezierAlphaOut.initNP();
        guiBezierTranslationIn.initNP();
        guiBezierTranslationOut.initNP();
    }

    public static void drawContinuousTexturedBox(ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth,
                                                 int textureHeight,
                                                 int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel,
                                                 GuiScreenAdvanced pScr)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(res);
        drawContinuousTexturedBox(x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder,
                zLevel, pScr);
    }

    public static void drawContinuousTexturedBox(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight,
                                                 int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel,
                                                 GuiScreenAdvanced pScr)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, pScr.alpha);
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
        drawTexturedModalRect(x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, zLevel);
        // Bottom Left
        drawTexturedModalRect(x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, zLevel);
        // Bottom Right
        drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth,
                v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);

        for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++)
        {
            // Top Border
            drawTexturedModalRect(x + leftBorder + (i * fillerWidth), y, u + leftBorder, v, (i == xPasses ? remainderWidth : fillerWidth),
                    topBorder, zLevel);
            // Bottom Border
            drawTexturedModalRect(x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight, u + leftBorder,
                    v + topBorder + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), bottomBorder, zLevel);

            // Throw in some filler for good measure
            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
            {
                drawTexturedModalRect(x + leftBorder + (i * fillerWidth), y + topBorder + (j * fillerHeight), u + leftBorder, v + topBorder,
                        (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight), zLevel);
            }
        }

        // Side Borders
        for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
        {
            // Left Border
            drawTexturedModalRect(x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder,
                    (j == yPasses ? remainderHeight : fillerHeight), zLevel);
            // Right Border
            drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight), u + leftBorder + fillerWidth,
                    v + topBorder, rightBorder, (j == yPasses ? remainderHeight : fillerHeight), zLevel);
        }
    }
}
