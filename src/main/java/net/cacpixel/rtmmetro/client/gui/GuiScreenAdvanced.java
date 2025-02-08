package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiContainerCustom;
import jp.ngt.ngtlib.gui.GuiScreenCustom;
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.client.gui.widgets.*;
import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.cacpixel.rtmmetro.util.ModLog;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;

@SideOnly(Side.CLIENT)
public abstract class GuiScreenAdvanced extends GuiScreen implements IWidgetHolder
{
    public GuiScreen parentScreen;
    public boolean hasValueUpdated;
    private static int nextWidgetId;
    public List<IGuiWidget> widgets = new ArrayList<>();
    public List<IGuiWidget> widgetsOnAction = new ArrayList<>();
    private float alpha;
    public boolean isOpening;
    public boolean isClosing;
    protected float animationTime;
    protected float duration;
    protected boolean closeFlag;

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

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.widgets.clear();
        nextWidgetId = 0;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        this.width = scaledresolution.getScaledWidth();
        this.height = scaledresolution.getScaledHeight();
    }

    public void drawScreenBefore(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        if (this.isOpening)
        {
            this.animationTime += partialTicks / 20;
            if (this.animationTime > this.duration)
                this.isOpening = false;
            // 类win10动画
            float scale = (float) MathHelper.clampedLerp(0.9F, 1.0F, this.getAnimationProgress(CacGuiUtils.guiBezierTranslation));
            GlStateManager.translate((this.width * (1 - scale)) / 2.0F, (this.height * (1 - scale)) / 2.0F, 0.0F);
            GlStateManager.scale(scale, scale, 1.0F);
            // 下拉
//            float progress = (float) MathHelper.clampedLerp(0.9F, 1.0F, this.getAnimationProgress(CacGuiUtils.guiBezierTranslationIn));
//            GlStateManager.translate(0.0F, (progress - 1.0F) * this.height, 0.0F);
        }
        if (this.isClosing)
        {
            this.animationTime += partialTicks / 20;
            if (this.animationTime > this.duration)
                this.isClosing = false;
            // 类win10动画 用于小窗口
            float scale = (float) MathHelper.clampedLerp(0.9F, 1.0F, 1 - this.getAnimationProgress(CacGuiUtils.guiBezierTranslation));
            GlStateManager.translate((this.width * (1 - scale)) / 2.0F, (this.height * (1 - scale)) / 2.0F, 0.0F);
            GlStateManager.scale(scale, scale, 1.0F);
            // 下拉 用于全屏幕
//            float progress = (float) MathHelper.clampedLerp(0.9F, 1.0F, 1 - this.getAnimationProgress(CacGuiUtils
//            .guiBezierTranslationIn));
//            GlStateManager.translate(0.0F, (progress - 1.0F) * this.height, 0.0F);
        }
        if (!this.isInAnimation())
        {
            this.animationTime = 0;
        }
        this.updateAlpha();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.widgets.forEach(x -> x.draw(mouseX, mouseY, partialTicks));
//        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void drawScreenAfter(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }

    public List<IGuiWidget> getWidgets()
    {
        return this.widgets;
    }

    @Override
    public void updateScreen()
    {
        // perform action
        this.widgetsOnAction.forEach(w -> {
            IActionListener listener = w.getListener();
            if (listener != null)
            {
                listener.onAction();
            }
        });
        this.widgetsOnAction.clear();
        // update CursorCounter
        GuiTextFieldAdvanced fieldCurrent = this.getCurrentTextField();
        if (fieldCurrent != null && fieldCurrent.isEnabled() && fieldCurrent.isVisible())
        {
            fieldCurrent.updateCursorCounter();
        }
        // switch screen
        this.switchGuiScreenToPrevious();
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

    public void drawBackground(int tint, int left, int top, int right, int bottom)
    {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, this.height, 0.0D).tex(0.0D, (float) this.height / 32.0F + (float) tint)
                .color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(this.width, this.height, 0.0D)
                .tex((float) this.width / 32.0F, (float) this.height / 32.0F + (float) tint).color(64, 64, 64, 255)
                .endVertex();
        bufferbuilder.pos(this.width, 0.0D, 0.0D).tex((float) this.width / 32.0F, tint).color(64, 64, 64, 255)
                .endVertex();
        bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, tint).color(64, 64, 64, 255).endVertex();
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
        double point = curve.fromXGetY((int) curve.getLength(), (this.animationTime / this.duration) * CacGuiUtils.xMax);
        return MathHelper.clamp((float) point, 0.02f, 2.0f);
    }

    protected void updateAlpha()
    {
        if (this.isOpening)
        {
            this.alpha = (float) MathHelper.clampedLerp(0.02F, 1.0F, this.getAnimationProgress(CacGuiUtils.guiBezierAlpha));
        }
        else if (this.isClosing)
        {
            this.alpha = (float) MathHelper.clampedLerp(0.02F, 1.0F, 1 - this.getAnimationProgress(CacGuiUtils.guiBezierAlpha));
        }
        else if (!this.isInAnimation() && this.closeFlag)
        {
            this.alpha = 0.02F;
        }
        else
        {
            this.alpha = 1.0F;
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
        if (!this.isInAnimation() && closeFlag)
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
            if (button == 0)
            {
                for (IGuiWidget widget : widgets)
                {
                    if (!(widget instanceof GuiButtonAdvanced)) continue;
                    GuiButton guibutton = (GuiButtonAdvanced) widget;
                    if (guibutton.mousePressed(this.mc, x, y))
                    {
                        net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event =
                                new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(
                                        this, guibutton, this.buttonList);
                        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                            break;
                        guibutton = event.getButton();
                        this.selectedButton = guibutton;
                        guibutton.playPressSound(this.mc.getSoundHandler());
                        this.actionPerformed(guibutton);
                        if (this.equals(this.mc.currentScreen))
                            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(
                                    new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(),
                                            this.buttonList));
                    }
                }
            }
        }
        catch (ConcurrentModificationException e)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            ModLog.debug("Unexpected widget modification: " + sw);
            return;
        }
        if (!this.isInAnimation() && !this.closeFlag)
        {
            this.widgets.stream()/*.filter(w -> !(w instanceof GuiButton))*/.forEach(w -> w.onClick(x, y, button)); // click others
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
            this.widgets.forEach(w -> w.onKeyTyped(typedChar, keyCode));
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
        if (scroll != 0)
        {
            this.widgets.forEach(w -> w.onScroll(x, y, scroll));
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
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
        }
        field = this.getNextTextField(field, true);
        if (field != null)
        {
            field.setFocused(true);
        }
    }

    @SuppressWarnings("rawtypes,unchecked")
    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button instanceof IGuiWidget)
        {
            this.widgetsOnAction.add((IGuiWidget) button);
        }
    }

    public GuiTextFieldAdvanced getFocusedTextField()
    {
        List<GuiTextFieldAdvanced> textFields = new ArrayList<>();
        this.widgets.stream().filter(w -> w instanceof GuiTextFieldAdvanced).forEach(w -> textFields.add((GuiTextFieldAdvanced) w));
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
        this.widgets.stream().filter(w -> w instanceof GuiTextFieldAdvanced).forEach(w -> textFields.add((GuiTextFieldAdvanced) w));
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

    public GuiTextFieldAdvanced getPrevTextField(GuiTextField fieldIn, boolean loop)
    {
        List<GuiTextFieldAdvanced> textFields = new ArrayList<>();
        this.widgets.stream().filter(w -> w instanceof GuiTextFieldAdvanced).forEach(w -> textFields.add((GuiTextFieldAdvanced) w));
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

    public static int getNextWidgetId()
    {
        return nextWidgetId++;
    }

    public float getAlpha()
    {
        return alpha;
    }

    public GuiTextFieldAdvanced getCurrentTextField()
    {
        List<GuiTextFieldAdvanced> textFields = new ArrayList<>();
        this.widgets.stream().filter(w -> w instanceof GuiTextFieldAdvanced).forEach(w -> textFields.add((GuiTextFieldAdvanced) w));
        return textFields.stream().filter(GuiTextField::isFocused).findFirst().orElse(null);
    }

    @Override
    public GuiScreenAdvanced getScreen()
    {
        return this;
    }
}
