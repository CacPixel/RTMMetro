package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiContainerCustom;
import jp.ngt.ngtlib.gui.GuiScreenCustom;
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiWidget;
import net.cacpixel.rtmmetro.client.gui.widgets.IWidgetHolder;
import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.cacpixel.rtmmetro.util.ModLog;
import net.cacpixel.rtmmetro.util.RTMMetroException;
import net.cacpixel.rtmmetro.util.RTMMetroUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;

@SideOnly(Side.CLIENT)
public abstract class GuiScreenAdvanced extends GuiScreen implements IWidgetHolder
{
    public GuiScreenAdvanced parentScreen;
    public boolean drawParent = false;
    public int x = 0;
    public int y = 0;
    private int nextWidgetId;
    public ArrayList<GuiWidget> widgets = new ArrayList<>();
    public GuiLayoutBase layout = new GuiLayoutNone(this);
    public PriorityQueue<GuiWidget> actionQueue = new PriorityQueue<>(
            Comparator.comparing(GuiWidget::getzLevel).reversed());
    protected float alpha;
    private AnimationStatus animationStatus;
    protected float animationTime;
    protected float duration;
    public float translationX;
    public float translationY;
    public float scaleX = 1.0F;
    public float scaleY = 1.0F;
    public boolean initialized = false;
    public int glStackCount = 0;
    private final ScissorManager scissorManager = new ScissorManager(this);
    private int lastClickedX;
    private int lastClickedY;
    private int eventButton;
    private long lastMouseEvent;
    private int touchValue;

    public GuiScreenAdvanced()
    {
        super();
        alpha = 0.05f;
        this.setAnimationStatus(AnimationStatus.NONE);
        duration = ModConfig.guiAnimationDuration;
    }

    public void glPushMatrix()
    {
        ++glStackCount;
        GL11.glPushMatrix();
    }

    public void glPopMatrix()
    {
        --glStackCount;
        if (glStackCount < 0)
            throw new RTMMetroException("glStackCount < 0, glPopMatrix too much!");
        GL11.glPopMatrix();
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiScreenAdvanced> T setDuration(float duration)
    {
        this.duration = duration;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiScreenAdvanced> T setParent(GuiScreenAdvanced parent)
    {
        if (this == parent)
        {
            throw new RTMMetroException("Parent screen refers to itself.");
        }
        parentScreen = parent;
        return (T) this;
    }

    public void screenResize()
    {
        makeLayout();
    }

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.widgets.clear();
        nextWidgetId = 0;
    }

    public void closeButtonCallback(GuiWidget w)
    {
        this.displayPrevScreen();
    }

    public void drawScreenBefore(int mouseX, int mouseY, float partialTicks)
    {
        if (this.mc.currentScreen == this)
            this.handleInput();
        this.glPushMatrix();
        translationX = translationY = 0;
        if (x != 0 || y != 0)
        {
            this.translationX += x;
            this.translationY += y;
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.updateAnimation(partialTicks);
        this.updateAlpha();
        if (translationX != 0.0F || translationY != 0.0F)
            GlStateManager.translate(translationX, translationY, 0.0F);
        if (scaleX != 1.0F || scaleY != 1.0F)
            GlStateManager.scale(scaleX, scaleY, 1.0F);

        if (!isLastScreen() && !isThisScreen())
        {
            this.getScissorManager().apply();
        }
    }

    public void handleInput()
    {
        try
        {
            super.handleInput();
        }
        catch (Throwable e)
        {
            ModLog.showChatMessage(TextFormatting.RED +
                    I18n.format("message.error.fatal_problem_occurred", "Handling GUI Input"));
            ModLog.error("Caught exception while handling input: " + RTMMetroUtils.getStackTrace(e));
            Minecraft.getMinecraft().displayGuiScreen(null);
            Minecraft.getMinecraft().setIngameFocus();
        }
    }

    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        try
        {
            if (parentScreen != null && (this.drawParent))
            {
                parentScreen.drawScreen(mouseX, mouseY, partialTicks);
            }
            else if (parentScreen != null && !isOpened())
            {
                parentScreen.draw(mouseX, mouseY, partialTicks);
            }
            this.draw(mouseX, mouseY, partialTicks);
            if (glStackCount > 0)
                throw new RTMMetroException("glStackCount > 0, glPushMatrix too much!");
            getScissorManager().checkStackEmpty();
        }
        catch (Throwable e)
        {
            ModLog.showChatMessage(TextFormatting.RED +
                    I18n.format("message.error.fatal_problem_occurred", "Rendering GUI"));
            ModLog.error("Caught exception while rendering gui: " + RTMMetroUtils.getStackTrace(e));
            Minecraft.getMinecraft().displayGuiScreen(null);
            Minecraft.getMinecraft().setIngameFocus();
        }
        finally
        {
            while (glStackCount > 0)
            {
                this.glPopMatrix();
            }
            getScissorManager().forceDisableScissor();
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.widgets.forEach(x -> x.draw(mouseX, mouseY, partialTicks));
    }

    public void drawScreenAfter(int mouseX, int mouseY, float partialTicks)
    {
        if (!isLastScreen() && !isThisScreen())
        {
            this.getScissorManager().pop();
        }
        this.glPopMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }

    public List<GuiWidget> getWidgets()
    {
        return this.widgets;
    }

    @Override
    public final void updateScreen()
    {
        // update holders
        try
        {
            this.onUpdate();
            if (this.widgetValueUpdated())
            {
                this.updateValueFromWidgets();
            }
        }
        catch (ConcurrentModificationException e)
        {
            ModLog.showChatMessage(TextFormatting.RED +
                    I18n.format("message.error.fatal_problem_occurred", "Ticking screen"));
            ModLog.error("Caught exception while ticking screen: " + RTMMetroUtils.getStackTrace(e));
            Minecraft.getMinecraft().displayGuiScreen(null);
            Minecraft.getMinecraft().setIngameFocus();
            return;
        }
        // switch screen
        this.switchGuiScreenToPrevious();
    }

    @Override
    public void onUpdate()
    {
        IWidgetHolder.super.onUpdate();
    }

    @Override
    public void onGuiClosed()
    {
        if (this.parentScreen == null)
            Keyboard.enableRepeatEvents(false);
    }

    public void drawWorldBackground(int tint, int left, int top, int right, int bottom)
    {
        if (this.mc.world != null)
        {
            this.drawGradientRect(left, top, right, bottom, 0x101010 | this.getAlphaInt(0xC0),
                    0x101010 | this.getAlphaInt(0xD0));
        }
        else
        {
            this.drawBackground(tint, left, top, right, bottom);
        }
    }

    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
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
        bufferbuilder.pos((double) right, (double) top, (double) this.zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) left, (double) top, (double) this.zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) left, (double) bottom, (double) this.zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double) right, (double) bottom, (double) this.zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
//        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void drawBackground(int tint, int left, int top, int right, int bottom)
    {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int alpha = this.getAlphaHighBits(0xFF);
        float f = 32.0F;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(left, bottom, 0.0D)
                .tex(0, (float) (bottom - top) / f + (float) tint)
                .color(64, 64, 64, alpha).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D)
                .tex((float) (right - left) / f, (float) (bottom - top) / f + (float) tint)
                .color(64, 64, 64, alpha).endVertex();
        bufferbuilder.pos(right, top, 0.0D)
                .tex((float) (right - left) / f, tint)
                .color(64, 64, 64, alpha).endVertex();
        bufferbuilder.pos(left, top, 0.0D)
                .tex(0, tint)
                .color(64, 64, 64, alpha).endVertex();
        tessellator.draw();
    }

    @Override
    public void drawWorldBackground(int tint)
    {
        this.drawWorldBackground(tint, 0, 0, this.width, this.height);
    }

    public void drawDefaultBackgroundBefore()
    {
    }

    @Override
    public void drawDefaultBackground()
    {
        this.drawWorldBackground(0);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(
                new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
    }

    public void drawDefaultBackground(int left, int top, int right, int bottom)
    {
        this.drawWorldBackground(0, left, top, right, bottom);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(
                new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
    }

    protected float getAnimationProgress(BezierCurveAdvanced curve)
    {
        double point = curve.fromXGetY((int) curve.getLength(),
                (this.animationTime / this.duration) * CacGuiUtils.X_MAX);
        return MathHelper.clamp((float) point, 0.02f, 2.0f);
    }

    protected void updateAnimation(float partialTicks)
    {
        if (this.isOpening())
        {
            this.animationTime += partialTicks / 20;
            if (this.animationTime > this.duration)
            {
                this.setAnimationStatus(AnimationStatus.OPENED);
            }
        }
        else if (this.isClosing())
        {
            this.animationTime += partialTicks / 20;
            if (this.animationTime > this.duration)
            {
                this.setAnimationStatus(AnimationStatus.CLOSED);
            }
        }
    }

    protected void updateAlpha(float lowerBnd, float upperBnd)
    {
        if (this.isOpening())
        {
            this.alpha = (float) MathHelper.clampedLerp(lowerBnd, upperBnd,
                    this.getAnimationProgress(CacGuiUtils.guiBezierAlpha));
        }
        else if (this.isClosing())
        {
            this.alpha = (float) MathHelper.clampedLerp(lowerBnd, upperBnd,
                    1 - this.getAnimationProgress(CacGuiUtils.guiBezierAlpha));
        }
        else
        {
            if (this.getAnimationStatus() == AnimationStatus.CLOSED)
                this.alpha = lowerBnd;
            else
                this.alpha = upperBnd;
        }
    }

    protected void updateAlpha()
    {
        this.updateAlpha(0.02f, 1.0f);
    }

    public int getAlphaInt(int a)
    {
        return this.getAlphaHighBits(a) << 24;
    }

    protected int getAlphaHighBits(int a)
    {
        return (int) (a * this.getAlpha());
    }

    protected float getAlphaFloat(float finalAlphaFloat)
    {
        return this.getAlpha() * finalAlphaFloat;
    }

    protected void displayPrevScreen()
    {
        this.setAnimationStatus(AnimationStatus.CLOSING);
        if (parentScreen != null && !this.drawParent)
        {
            parentScreen.setAnimationStatus(AnimationStatus.OPENING);
        }
    }

    protected void switchGuiScreenToPrevious()
    {
        if (this.getAnimationStatus() == AnimationStatus.CLOSED ||
                ((this.parentScreen == null) && (animationTime >= duration / 8) && (this.getAnimationStatus() == AnimationStatus.CLOSING)))
        {
            if (parentScreen == null)
            {
                displayGuiScreen(null);
            }
            else
            {
                displayGuiScreen(this.parentScreen);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == Keyboard.KEY_ESCAPE)
        {
            this.onPressingEsc();
            super.keyTyped(typedChar, keyCode); // Close ALL Gui without animation while pressing ESC
            return;
        }
        if (isOpened())
        {
            this.getAllWidgets().forEach(w -> w.onKeyTyped(typedChar, keyCode));
        }
        if (keyCode == Keyboard.KEY_RETURN)
        {
            this.onPressingEnter();
        }
    }

    @Override
    public void handleMouseInput()
    {
        int x = CacGuiUtils.getMouseX();// - shiftMouseX();
        int y = CacGuiUtils.getMouseY();// - shiftMouseY();
        int button = Mouse.getEventButton();
        int scroll = Mouse.getEventDWheel();

        label:
        {
            if (Mouse.getEventButtonState())
            {
                if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0)
                {
                    break label;
                }
                this.eventButton = button;
                this.lastMouseEvent = Minecraft.getSystemTime();
                this.mouseClicked(x, y, this.eventButton);
            }
            else if (button != -1)
            {
                if (this.mc.gameSettings.touchscreen && --this.touchValue > 0)
                {
                    break label;
                }
                this.eventButton = -1;
                this.mouseReleased(x, y, button);
            }
            else if (this.eventButton != -1 && this.lastMouseEvent > 0L)
            {
                long l = Minecraft.getSystemTime() - this.lastMouseEvent;
                this.mouseClickMove(x, y, this.eventButton, l);
            }
        }
        if (scroll != 0)
        {
            this.mouseScroll(x, y, scroll);
        }

        if (this.widgetValueUpdated())
        {
            this.updateValueFromWidgets();
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button)
    {
        this.lastClickedX = x;
        this.lastClickedY = y;
        if (isOpened())
        {
            this.getAllWidgets().forEach(w -> w.onClick(x, y, button));
        }
    }

    @Override
    protected void mouseClickMove(int x, int y, int clickedMouseButton, long timeSinceLastClick)
    {
        if (isOpened())
        {
            this.getAllWidgets().forEach(w -> w.onClickAndDrag(x, y, clickedMouseButton, timeSinceLastClick));
        }
    }

    @Override
    protected void mouseReleased(int x, int y, int state)
    {
        if (isOpened())
        {
            this.getAllWidgets().forEach(w -> w.onMouseReleased(x, y, state));
        }
    }

    protected void mouseScroll(int x, int y, int scroll)
    {
        if (isOpened())
        {
            this.getAllWidgets().forEach(w -> w.onScroll(x, y, scroll));
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        super.handleKeyboardInput();
        if (this.widgetValueUpdated())
        {
            this.updateValueFromWidgets();
        }
    }

    protected void onPressingEsc()
    {
    }

    protected void onPressingEnter()
    {
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        this.mc = mc;
        this.itemRender = mc.getRenderItem();
        this.fontRenderer = mc.fontRenderer;
        this.width = width;
        this.height = height;
        try
        {
            if (!initialized)
            {
                this.initGui();
                initialized = true;
            }
            this.screenResize();
            if (parentScreen != null)
                this.parentScreen.setWorldAndResolution(mc, width, height);
        }
        catch (Throwable e)
        {
            ModLog.showChatMessage(TextFormatting.RED +
                    I18n.format("message.error.fatal_problem_occurred", "Initializing GUI"));
            ModLog.error("Caught exception while initializing gui: " + RTMMetroUtils.getStackTrace(e));
            Minecraft.getMinecraft().displayGuiScreen(null);
            Minecraft.getMinecraft().setIngameFocus();
        }
    }

    public static void drawHoveringTextS(List<String> textLines, int x, int y, GuiScreen screen)
    {
        if (screen instanceof GuiScreenCustom)
        {
            screen.drawHoveringText(textLines, x, y);
        }
        else if (screen instanceof GuiContainerCustom)
        {
            screen.drawHoveringText(textLines, x, y);
        }

        //以降で描画するボタンの明るさを変えないように
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
    }

    public int getNextWidgetId()
    {
        return nextWidgetId++;
    }

    public float getAlpha()
    {
        return alpha;
    }

    @Override
    public GuiScreenAdvanced getScreen()
    {
        return this;
    }

    @Override
    public Queue<GuiWidget> getActionQueue()
    {
        return actionQueue;
    }

    public void onWidgetValueChanged(GuiWidget widget)
    {
    }

    public void updateValueFromWidgets()
    {
    }

    public int getHalfWidth()
    {
        return this.width / 2;
    }

    public int getHalfHeight()
    {
        return this.height / 2;
    }

    @Override
    public boolean isMouseInside()
    {
        return CacGuiUtils.isMouseInside(x, y, width, height);
    }

    @Override
    public boolean isMouseInside(int mouseX, int mouseY)
    {
        return CacGuiUtils.isMouseInside(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public boolean isLastClickInside()
    {
        return CacGuiUtils.isMouseInside(x, y, width, height, getLastClickedX(), getLastClickedY());
    }

    public int shiftMouseX()
    {
        return x;
    }

    public int shiftMouseY()
    {
        return y;
    }

    public boolean isOpening()
    {
        return this.getAnimationStatus() == AnimationStatus.OPENING;
    }

    public boolean isClosing()
    {
        return this.getAnimationStatus() == AnimationStatus.CLOSING;
    }

    public boolean isOpened()
    {
        return this.getAnimationStatus() == AnimationStatus.OPENED;
    }

    public boolean isInAnimation()
    {
        return this.isOpening() || this.isClosing();
    }

    public AnimationStatus getAnimationStatus()
    {
        return animationStatus;
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiScreenAdvanced> T setAnimationStatus(AnimationStatus animationStatus)
    {
        this.animationStatus = animationStatus;
        if (animationStatus == AnimationStatus.OPENED || animationStatus == AnimationStatus.CLOSED)
        {
            this.animationTime = 0;
        }
        return (T) this;
    }

    public static void displayGuiScreen(GuiScreenAdvanced screen)
    {
        Minecraft mc = Minecraft.getMinecraft();
        mc.displayGuiScreen(screen);
        if (screen != null)
        {
            if (screen.getAnimationStatus() == AnimationStatus.NONE)
            {
                screen.setAnimationStatus(AnimationStatus.OPENING);
            }
            if (screen.parentScreen != null && !screen.drawParent)
            {
                screen.parentScreen.setAnimationStatus(AnimationStatus.CLOSING);
            }
        }
    }

    @Override
    public int getX() {return x;}

    @Override
    public int getY() {return y;}

    @Override
    public int getWidth() {return width;}

    @Override
    public int getHeight() {return height;}

    public ScissorManager getScissorManager()
    {
        return scissorManager;
    }

    public boolean isLastScreen()
    {
        return this.parentScreen == null && isThisScreen();
    }

    public boolean isThisScreen()
    {
        return this.mc.currentScreen == this;
    }

    @Override
    public GuiLayoutBase getLayout()
    {
        return layout;
    }

    @Override
    public void setLayout(GuiLayoutBase layout)
    {
        this.layout = layout;
    }

    public boolean widgetValueUpdated()
    {
        return getAllWidgets().stream().anyMatch(GuiWidget::checkValueUpdated);
    }

    public int getLastClickedX()
    {
        return lastClickedX;
    }

    public int getLastClickedY()
    {
        return lastClickedY;
    }

    public enum AnimationStatus
    {
        NONE,
        OPENING,
        OPENED,
        CLOSING,
        CLOSED
    }
}
