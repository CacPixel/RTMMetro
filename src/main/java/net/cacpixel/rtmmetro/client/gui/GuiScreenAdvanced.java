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
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
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
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public abstract class GuiScreenAdvanced extends GuiScreen implements IWidgetHolder
{
    public GuiScreen parentScreen;
    public boolean hasValueUpdated;
    private static int nextWidgetId;
    public List<IGuiWidget> widgets = new ArrayList<>();
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

    public GuiScreenAdvanced setDuration(float duration)
    {
        this.duration = duration;
        return this;
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

    public List<IGuiWidget> getWidgets()
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
        // perform action
        this.getButtonList().stream().filter(w -> w.clicked).collect(Collectors.toList()).forEach(w -> {
            this.getScreen().performActionAndSendEvent(w);
            w.clicked = false;
        });
        // update CursorCounter
        GuiTextFieldAdvanced fieldCurrent = this.getScreen().getCurrentTextField();
        if (fieldCurrent != null && fieldCurrent.isEnabled() && fieldCurrent.isVisible())
        {
            fieldCurrent.updateCursorCounter();
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
            this.alpha = (float) MathHelper.clampedLerp(0.02F, 1.0F, this.getAnimationProgress(CacGuiUtils.guiBezierAlpha));
        }
        else if (this.isClosing)
        {
            this.alpha = (float) MathHelper.clampedLerp(0.02F, 1.0F, 1 - this.getAnimationProgress(CacGuiUtils.guiBezierAlpha));
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
            if (!this.isInAnimation() && !this.closeFlag)
            {
                this.widgets.forEach(w -> w.onClick(x, y, button));
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

    public void actionPerformed(GuiButton button)
    {
        if (button instanceof GuiButtonAdvanced)
        {
            GuiButtonAdvanced b = ((GuiButtonAdvanced) button);
            IActionListener<? extends IGuiWidget> listener = b.getListener();
            if (listener != null)
            {
                listener.onAction(b);
            }
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

    @Override
    public GuiScreenAdvanced getScreen()
    {
        return this;
    }

    public void performActionAndSendEvent(GuiButton guibutton)
    {
        List<GuiButton> buttonList = new ArrayList<>(this.getButtonList());
        GuiScreenEvent.ActionPerformedEvent.Pre event = new GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, buttonList);
        if (MinecraftForge.EVENT_BUS.post(event))
            return;
        guibutton = event.getButton();
        this.selectedButton = guibutton;
        guibutton.playPressSound(this.mc.getSoundHandler());
        this.actionPerformed(guibutton);
        if (this.equals(this.mc.currentScreen))
            MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), buttonList));
    }
}
