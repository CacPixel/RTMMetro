package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiContainerCustom;
import jp.ngt.ngtlib.gui.GuiScreenCustom;
import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.math.BezierCurveAdvanced;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public abstract class GuiScreenAdvanced extends GuiScreen
{
    public GuiScreen parentScreen;
    public boolean hasValueUpdated;
    private static int NEXT_FIELD_ID;
    private static int NEXT_BUTTON_ID;
    protected List<GuiTextFieldAdvanced> textFields = new ArrayList();
    protected GuiTextField currentTextField;

    protected float alpha;
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
        this.textFields.clear();
        this.buttonList.clear();
        NEXT_FIELD_ID = 0;
        NEXT_BUTTON_ID = 0;
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
            float scale = (float) MathHelper.clampedLerp(0.9F, 1.0F, this.getAnimationProgress(CacGuiUtils.guiBezierTranslationIn));
            GlStateManager.translate((this.width * (1 - scale)) / 2.0F, (this.height * (1 - scale)) / 2.0F, 1.0F);
            GlStateManager.scale(scale, scale, 1.0F);
        }
        if (this.isClosing)
        {
            this.animationTime += partialTicks / 20;
            if (this.animationTime > this.duration)
                this.isClosing = false;
            float scale = (float) MathHelper.clampedLerp(0.9F, 1.0F, this.getAnimationProgress(CacGuiUtils.guiBezierTranslationOut));
            GlStateManager.translate((this.width * (1 - scale)) / 2.0F, (this.height * (1 - scale)) / 2.0F, 1.0F);
            GlStateManager.scale(scale, scale, 1.0F);
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
        for (GuiTextFieldAdvanced field : this.textFields)
        {
            field.drawTextBox(mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void drawScreenAfter(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }

    public List<GuiButton> getButtonList()
    {
        return this.buttonList;
    }

    public List<GuiTextFieldAdvanced> getTextFields()
    {
        return this.textFields;
    }

    public float getZLevel()
    {
        return this.zLevel;
    }

    @Override
    public void updateScreen()
    {
        if (this.currentTextField != null)
        {
            this.currentTextField.updateCursorCounter();
        }
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
        bufferbuilder.pos(0.0D, (double) this.height, 0.0D).tex(0.0D, (double) ((float) this.height / 32.0F + (float) tint))
                .color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double) this.width, (double) this.height, 0.0D)
                .tex((double) ((float) this.width / 32.0F), (double) ((float) this.height / 32.0F + (float) tint)).color(64, 64, 64, 255)
                .endVertex();
        bufferbuilder.pos((double) this.width, 0.0D, 0.0D).tex((double) ((float) this.width / 32.0F), (double) tint).color(64, 64, 64, 255)
                .endVertex();
        bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double) tint).color(64, 64, 64, 255).endVertex();
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

    protected float getAnimationProgress()
    {
        return this.getAnimationProgress(CacGuiUtils.guiBezierAlphaIn);
    }

    protected void updateAlpha()
    {
        if (this.isOpening)
        {
            this.alpha = (float) MathHelper.clampedLerp(0.02F, 1.0F, this.getAnimationProgress(CacGuiUtils.guiBezierAlphaIn));
        }
        else if (this.isClosing)
        {
            this.alpha = (float) MathHelper.clampedLerp(0.02F, 1.0F, this.getAnimationProgress(CacGuiUtils.guiBezierAlphaOut));
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

    protected int getAlphaInt(int a)
    {
        return this.getAlphaHighBits(a) << 24;
    }

    protected int getAlphaHighBits(int a)
    {
        return (int) (a * this.alpha);
    }

    protected float getAlphaFloat(float finalAlphaFloat)
    {
        return this.alpha * finalAlphaFloat;
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

    protected GuiTextFieldAdvanced setTextField(int xPos, int yPos, int w, int h, String text)
    {
        GuiTextFieldAdvanced field = new GuiTextFieldAdvanced(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos, w, h, this);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(text);
        this.textFields.add(field);
        return field;
    }

    protected GuiTextFieldAdvancedFloat setTextField(int xPos, int yPos, int w, int h, float value, float min, float max, boolean loop)
    {
        GuiTextFieldAdvancedFloat field = new GuiTextFieldAdvancedFloat(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos, w, h, this,
                value).setMinMax(min, max, loop);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        this.textFields.add(field);
        return field;
    }

    protected GuiTextFieldAdvancedInt setTextField(int xPos, int yPos, int w, int h, int value, int min, int max, boolean loop)
    {
        GuiTextFieldAdvancedInt field = new GuiTextFieldAdvancedInt(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos, w, h, this,
                value).setMinMax(min, max, loop);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(String.valueOf(value));
        this.textFields.add(field);
        return field;
    }

    protected <T extends GuiButton> T addButton(T buttonIn, Consumer<? super GuiButton> callback)
    {
        T button = super.addButton(buttonIn);
        return button;
    }

    protected GuiButtonAdvanced addButton(int x, int y, int w, int h, String text,
                                          Consumer<? super GuiButtonAdvanced> callback)
    {
        GuiButtonAdvanced button = new GuiButtonAdvanced(NEXT_BUTTON_ID++, x, y, w, h, text, this, callback);
        this.buttonList.add(button);
        return button;
    }

    protected GuiCheckBoxAdvanced addCheckBox(int x, int y, int w, int h, String text, boolean isChecked,
                                              Consumer<? super GuiCheckBoxAdvanced> callback)
    {
        GuiCheckBoxAdvanced button = new GuiCheckBoxAdvanced(NEXT_BUTTON_ID++, x, y, text, isChecked, this, callback);
        this.buttonList.add(button);
        return button;
    }

    protected GuiUnicodeGlyphButtonAdvanced addUnicodeGlyphButton(int x, int y, int w, int h, String text, String glyph, float glyphScale,
                                                                  Consumer<? super GuiUnicodeGlyphButtonAdvanced> callback)
    {
        GuiUnicodeGlyphButtonAdvanced button = new GuiUnicodeGlyphButtonAdvanced(NEXT_BUTTON_ID++, x, y, w, h, text, glyph, glyphScale,
                this, callback);
        this.buttonList.add(button);
        return button;
    }

    protected GuiUnicodeGlyphButtonAdvanced addUnicodeGlyphButton(int x, int y, int w, int h, String glyph, float glyphScale,
                                                                  Consumer<? super GuiUnicodeGlyphButtonAdvanced> callback)
    {
        return this.addUnicodeGlyphButton(x, y, w, h, "", glyph, glyphScale, callback);
    }

    protected <E extends Enum<E>> GuiOptionButton<E> addOptionButton(int x, int y, int w, int h, String prefix, E[] values, E initVal,
                                                                     Consumer<? super GuiOptionButton<E>> callback)
    {
        GuiOptionButton<E> button = new GuiOptionButton<>(NEXT_BUTTON_ID++, x, y, w, h, prefix, values, initVal, this, callback);
        this.buttonList.add(button);
        return button;
    }

    public void drawRightAlignedString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        this.drawString(fontRendererIn, text, x - fontRendererIn.getStringWidth(text), y, color);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException
    {
        super.mouseClicked(x, y, button);
        this.currentTextField = null;
        for (GuiTextFieldAdvanced textField : this.textFields)
        {
            textField.mouseClicked(x, y, button);
            if (textField.isFocused() && textField.isMouseInside() && !this.isInAnimation())
            {
                this.currentTextField = textField;
                this.onTextFieldClicked(textField);
            }
        }
    }

    protected void onTextFieldClicked(GuiTextField field) {}

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.currentTextField != null && !this.isInAnimation())
        {
            this.currentTextField.textboxKeyTyped(typedChar, keyCode);
        }
        else
        {
            super.keyTyped(typedChar, keyCode); // Close ALL Gui without animation while pressing ESC
        }
        if (Keyboard.getEventKey() == Keyboard.KEY_RETURN)
        {
            this.onPressingEnter();
        }
        else if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE)
        {
            this.onPressingEsc();
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        for (GuiTextFieldAdvanced field : this.textFields)
        {
            if (field instanceof GuiTextFieldAdvanced && field.isMouseInside() &&
                    field.isEnabled() && field.getVisible() && !this.isInAnimation())
            {
                field.handleMouseInput();
            }
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        super.handleKeyboardInput();
        for (GuiTextFieldAdvanced field : this.textFields)
        {
            if (field instanceof GuiTextFieldAdvanced && field.getVisible() && field.isFocused() &&
                    field.isEnabled() && !this.isInAnimation())
            {
                field.handleKeyboardInput();
            }
        }
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
            this.currentTextField = field;
        }
    }

    @SuppressWarnings("rawtypes,unchecked")
    @Override
    protected void actionPerformed(GuiButton button)
    {
        Consumer consumer = button instanceof GuiButtonAdvanced ? ((GuiButtonAdvanced) button).callback : null;
        if (consumer != null)
        {
            consumer.accept(button);
        }
        try
        {
            super.actionPerformed(button);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public GuiTextFieldAdvanced getFocusedTextField()
    {
        if (this.textFields == null || this.textFields.isEmpty())
        {
            return null;
        }
        for (GuiTextFieldAdvanced field : this.textFields)
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
        if (this.textFields == null || this.textFields.isEmpty())
        {
            return null;
        }
        ListIterator<GuiTextFieldAdvanced> it = this.textFields.listIterator();
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
                    return this.textFields.get(0);
                }
                else return null;
            }
        }
        return null;
    }

    public GuiTextFieldAdvanced getPrevTextField(GuiTextField fieldIn, boolean loop)
    {
        if (this.textFields == null || this.textFields.isEmpty())
        {
            return null;
        }
        ListIterator<GuiTextFieldAdvanced> it = this.textFields.listIterator();
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
                    return this.textFields.get(this.textFields.size() - 1);
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

    public int getNextFieldIdAndIncrease()
    {
        return NEXT_FIELD_ID++;
    }

    public int getNextButtonIdAndIncrease()
    {
        return NEXT_BUTTON_ID++;
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
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double) left, (double) bottom, 0.0D).endVertex();
        bufferbuilder.pos((double) right, (double) bottom, 0.0D).endVertex();
        bufferbuilder.pos((double) right, (double) top, 0.0D).endVertex();
        bufferbuilder.pos((double) left, (double) top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
//        GlStateManager.disableBlend();
    }
}
