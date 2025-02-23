package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiContainerCustom;
import jp.ngt.ngtlib.gui.GuiScreenCustom;
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiTextFieldAdvanced;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiWidget;
import net.cacpixel.rtmmetro.client.gui.widgets.IWidgetHolder;
import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.cacpixel.rtmmetro.util.ModLog;
import net.cacpixel.rtmmetro.util.RTMMetroUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

@SideOnly(Side.CLIENT)
public abstract class GuiScreenAdvanced extends GuiScreen implements IWidgetHolder
{
    public GuiScreenAdvanced parentScreen;
    public boolean hasValueUpdated; // todo: move to text field (no no no move to GuiWidget instead)
    private int nextWidgetId;
    public ArrayList<GuiWidget> widgets = new ArrayList<>();
    public PriorityQueue<GuiWidget> actionQueue = new PriorityQueue<>(Comparator.comparing(GuiWidget::getzLevel).reversed());
    private float alpha;
    public boolean isOpening;
    public boolean isClosing;
    protected float animationTime;
    protected float duration;
    protected boolean closeFlag;
    public float translationX;
    public float translationY;
    public float rotationX;
    public float rotationY;
    public float scaleX = 1.0F;
    public float scaleY = 1.0F;
    public boolean initialized = false;

    public GuiScreenAdvanced()
    {
        super();
        alpha = 0.05f;
        isOpening = true;
        isClosing = false;
        duration = ModConfig.guiAnimationDuration;
        animationTime = 0;
        closeFlag = false;
    }

    public GuiScreenAdvanced setDuration(float duration)
    {
        this.duration = duration;
        return this;
    }

    public void updateWidgets()
    {
        this.getAllWidgets().forEach(GuiWidget::updatePosAndSize);
        if (parentScreen != null)
            this.parentScreen.setWorldAndResolution(this.mc, this.width, this.height);
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
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.updateAnimation(partialTicks);
        this.updateAlpha();
        if (!this.isInAnimation())
        {
            this.animationTime = 0;
            if (this.closeFlag)
                this.alpha = 0.02F;
            else
                this.alpha = 1.0F;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.widgets.forEach(x -> x.draw(mouseX, mouseY, partialTicks));
//        super.drawScreen(mouseX, mouseY, partialTicks);   // draw vanilla GuiButton and GuiLabel
    }

    public void drawScreenAfter(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }

    public List<GuiWidget> getWidgets()
    {
        return this.widgets;
    }

    @Override
    public void updateScreen()
    {
        // update holders
        try
        {
            this.onUpdate();
        }
        catch (ConcurrentModificationException e)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            ModLog.debug("Unexpected widget modification: " + sw);
            return;
        }
        // switch screen
        this.switchGuiScreenToPrevious();
    }

    @Override
    public void onUpdate()
    {
        // update CursorCounter
        GuiTextFieldAdvanced fieldCurrent = this.getScreen().getCurrentTextField();
        if (fieldCurrent != null && fieldCurrent.isEnabled() && fieldCurrent.isVisible())
        {
//            fieldCurrent.updateCursorCounter();
        }
        IWidgetHolder.super.onUpdate();
    }

    @Override
    public void onGuiClosed()
    {
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
        if (this.isOpening)
        {
            this.animationTime += partialTicks / 20;
            if (this.animationTime > this.duration)
                this.isOpening = false;
        }
        else if (this.isClosing)
        {
            this.animationTime += partialTicks / 20;
            if (this.animationTime > this.duration)
                this.isClosing = false;
        }
    }

    protected void updateAlpha()
    {
        if (this.isOpening)
        {
            this.alpha = (float) MathHelper.clampedLerp(0.02F, 1.0F,
                    this.getAnimationProgress(CacGuiUtils.guiBezierAlpha));
        }
        else if (this.isClosing)
        {
            this.alpha = (float) MathHelper.clampedLerp(0.02F, 1.0F,
                    1 - this.getAnimationProgress(CacGuiUtils.guiBezierAlpha));
        }
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

    public boolean isInAnimation()
    {
        return this.isOpening || this.isClosing;
    }

    protected void displayPrevScreen()
    {
        this.closeFlag = true;
        this.isClosing = true;
    }

    protected void switchGuiScreenToPrevious()
    {
        if (!(this.isClosing && animationTime < duration / 10) && closeFlag)
        {
            if (parentScreen == null)
            {
                this.mc.displayGuiScreen(null);
            }
            else
            {
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button)
    {
        try
        {
            if (!this.isInAnimation() && !this.closeFlag)
            {
                this.getAllWidgets().forEach(w -> {
                    switch (button)
                    {
                    case 0:
                        w.onLeftClick(x, y);
                        break;
                    case 1:
                        w.onRightClick(x, y);
                        break;
                    case 2:
                        w.onMiddleClick(x, y);
                        break;
                    default:
                        w.onClickedOther(x, y, button);
                        break;
                    }
                });
            }
        }
        catch (ConcurrentModificationException e)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            ModLog.debug("Unexpected widget modification: " + sw);
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
        if (!this.isInAnimation() && !this.closeFlag)
        {
            this.getAllWidgets().forEach(w -> w.onKeyTyped(typedChar, keyCode));
        }
        if (keyCode == Keyboard.KEY_RETURN)
        {
            this.onPressingEnter();
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int button = Mouse.getEventButton();
        int scroll = Mouse.getEventDWheel();
        if (!this.isInAnimation() && !this.closeFlag)
        {
            if (scroll != 0)
            {
                this.getAllWidgets().forEach(w -> w.onScroll(x, y, scroll));
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (!this.isInAnimation() && !this.closeFlag)
        {
            this.getAllWidgets().forEach(w -> {
                switch (clickedMouseButton)
                {
                case 0:
                    w.onLeftClickAndDrag(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
                    break;
                case 1:
                case 2:
                default:
                    break;
                }
            });
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        if (!this.isInAnimation() && !this.closeFlag)
        {
            this.getAllWidgets().forEach(w -> {
                w.onMouseReleased(mouseX, mouseY, state);
            });
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        super.handleKeyboardInput();
    }

    protected void onPressingEsc()
    {
    }

    protected void onPressingEnter()
    {
        GuiTextFieldAdvanced field = this.getFocusedTextField();
        if (field != null)
        {
            field.checkValueAndSetText();
            field.setFocused(false);
            this.mc.getSoundHandler()
                    .playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
        }
        field = this.getNextTextField(field, true);
        if (field != null)
        {
            field.setFocused(true);
        }
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
            else
            {
                this.updateWidgets();
            }
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

    public GuiTextFieldAdvanced getFocusedTextField()
    {
        List<GuiTextFieldAdvanced> textFields = new ArrayList<>();
        this.getAllWidgets().stream().filter(w -> w instanceof GuiTextFieldAdvanced)
                .forEach(w -> textFields.add((GuiTextFieldAdvanced) w));
        if (textFields.isEmpty())
        {
            return null;
        }
        for (GuiTextFieldAdvanced field : textFields)
        {
            if (field.isFocused())
            {
                return field instanceof GuiTextFieldAdvanced ? field : null;
            }
        }
        return null;
    }

    public GuiTextFieldAdvanced getNextTextField(GuiTextFieldAdvanced fieldIn, boolean loop)
    {
        List<GuiTextFieldAdvanced> textFields = new ArrayList<>();
        this.getAllWidgets().stream().filter(w -> w instanceof GuiTextFieldAdvanced)
                .forEach(w -> textFields.add((GuiTextFieldAdvanced) w));
        if (textFields.isEmpty())
        {
            return null;
        }
        ListIterator<GuiTextFieldAdvanced> it = textFields.listIterator();
        while (it.hasNext())
        {
            GuiTextFieldAdvanced field = it.next();
            if (field == fieldIn)
            {
                if (it.hasNext())
                {
                    return it.next();
                }
                else if (loop)
                {
                    return textFields.get(0);
                }
                else return null;
            }
        }
        return null;
    }

    public GuiTextFieldAdvanced getPrevTextField(GuiTextFieldAdvanced fieldIn, boolean loop)
    {
        List<GuiTextFieldAdvanced> textFields = new ArrayList<>();
        this.getAllWidgets().stream().filter(w -> w instanceof GuiTextFieldAdvanced)
                .forEach(w -> textFields.add((GuiTextFieldAdvanced) w));
        if (textFields.isEmpty())
        {
            return null;
        }
        ListIterator<GuiTextFieldAdvanced> it = textFields.listIterator();
        while (it.hasPrevious())
        {
            GuiTextFieldAdvanced field = it.previous();
            if (field == fieldIn)
            {
                if (it.hasPrevious())
                {
                    return it.previous();
                }
                else if (loop)
                {
                    return textFields.get(textFields.size() - 1);
                }
                else return null;
            }
        }
        return null;
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

    public boolean isMouseInside()
    {
//        return CacGuiUtils.isMouseInside(0, 0, width, height);
        return true;
    }
}
