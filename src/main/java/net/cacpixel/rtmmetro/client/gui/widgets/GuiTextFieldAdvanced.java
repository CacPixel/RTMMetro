package net.cacpixel.rtmmetro.client.gui.widgets;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import jp.ngt.ngtlib.io.ScriptUtil;
import jp.ngt.ngtlib.math.NGTMath;
import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.cacpixel.rtmmetro.math.CacMath;
import net.cacpixel.rtmmetro.util.ModLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public class GuiTextFieldAdvanced extends GuiWidget
{
    // GuiTextField Fields BEGIN
    public final FontRenderer fontRenderer;
    protected String text = "";
    private int maxStringLength = 32;
    protected int cursorCounter;
    protected boolean enableBackgroundDrawing = true;
    private boolean canLoseFocus = true;
    protected boolean isFocused;
    protected int lineScrollOffset;
    protected int cursorPosition;
    protected int selectionEnd;
    protected int enabledColor = 14737632;
    protected int disabledColor = 7368816;
    protected Predicate<String> validator = Predicates.<String>alwaysTrue();
    // GuiTextField Fields END
    private final List<String> tips = new ArrayList<>();
    public boolean setTextIgnoreValidator = true;
    public String prefixTextFormatting = "";
    public boolean canDragEdit = false;

    public GuiTextFieldAdvanced(IWidgetHolder holder, int id, IntSupplier xSupplier, IntSupplier ySupplier,
                                IntSupplier widthSupplier, IntSupplier heightSupplier)
    {
        super(holder, id, xSupplier, ySupplier, widthSupplier, heightSupplier);
        this.fontRenderer = pScr.mc.fontRenderer;
    }

    public <T> T getFieldValue(T defaultVal)
    {
        return this.getFieldValue(defaultVal, true);
    }

    @SuppressWarnings("unchecked")
    public <T> T getFieldValue(T defaultVal, boolean doScript)
    {
        T ret;
        String text = getText();
        String result = getText();
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
            if (doScript)
            {
                ScriptEngine se = ScriptUtil.doScript(prefix + "x = " + text);
                result = ScriptUtil.getScriptField(se, "x").toString();
//            ModLog.debug("Executing script: " + "x = " + text + "; Result is: " + result);
            }
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

    public void onWidgetUpdate()
    {
        if (this.isDragging())
        {
            this.cursorCounter = 0;
        }
        else if (this.isEnabled() && this.isVisible() && this.isFocused())
        {
            ++this.cursorCounter;
        }
    }

    public <T extends GuiTextFieldAdvanced> T setText(String textIn)
    {
        if (this.validator.apply(textIn) || setTextIgnoreValidator)
        {
            if (textIn.length() > this.maxStringLength)
            {
                this.text = textIn.substring(0, this.maxStringLength);
            }
            else
            {
                this.text = textIn;
            }

            this.setCursorPositionEnd();
        }
        return (T) this;
    }

    public String getText()
    {
        return this.text;
    }

    public String getSelectedText()
    {
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(i, j);
    }

    public <T extends GuiTextFieldAdvanced> T setValidator(Predicate<String> theValidator)
    {
        this.validator = theValidator;
        return (T) this;
    }

    public void writeText(String textToWrite)
    {
        String s = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(textToWrite);
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int k = this.maxStringLength - this.text.length() - (i - j);

        if (!this.text.isEmpty())
        {
            s = s + this.text.substring(0, i);
        }

        int l;

        if (k < s1.length())
        {
            s = s + s1.substring(0, k);
            l = k;
        }
        else
        {
            s = s + s1;
            l = s1.length();
        }

        if (!this.text.isEmpty() && j < this.text.length())
        {
            s = s + this.text.substring(j);
        }

        if (this.validator.apply(s))
        {
            this.text = s;
            this.moveCursorBy(i - this.selectionEnd + l);
        }
    }

    public void deleteWords(int num)
    {
        if (!this.text.isEmpty())
        {
            if (this.selectionEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int num)
    {
        if (!this.text.isEmpty())
        {
            if (this.selectionEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                boolean flag = num < 0;
                int i = flag ? this.cursorPosition + num : this.cursorPosition;
                int j = flag ? this.cursorPosition : this.cursorPosition + num;
                String s = "";

                if (i >= 0)
                {
                    s = this.text.substring(0, i);
                }

                if (j < this.text.length())
                {
                    s = s + this.text.substring(j);
                }

                if (this.validator.apply(s))
                {
                    this.text = s;

                    if (flag)
                    {
                        this.moveCursorBy(num);
                    }
                }
            }
        }
    }

    public int getNthWordFromCursor(int numWords)
    {
        return this.getNthWordFromPos(numWords, this.getCursorPosition());
    }

    public int getNthWordFromPos(int n, int pos)
    {
        return this.getNthWordFromPosWS(n, pos, true);
    }

    public int getNthWordFromPosWS(int n, int pos, boolean skipWs)
    {
        int i = pos;
        boolean flag = n < 0;
        int j = Math.abs(n);

        for (int k = 0; k < j; ++k)
        {
            if (!flag)
            {
                int l = this.text.length();
                i = this.text.indexOf(32, i);

                if (i == -1)
                {
                    i = l;
                }
                else
                {
                    while (skipWs && i < l && this.text.charAt(i) == ' ')
                    {
                        ++i;
                    }
                }
            }
            else
            {
                while (skipWs && i > 0 && this.text.charAt(i - 1) == ' ')
                {
                    --i;
                }

                while (i > 0 && this.text.charAt(i - 1) != ' ')
                {
                    --i;
                }
            }
        }

        return i;
    }

    public void moveCursorBy(int num)
    {
        this.setCursorPosition(this.selectionEnd + num);
    }

    public void setCursorPosition(int pos)
    {
        this.cursorPosition = pos;
        int i = this.text.length();
        this.cursorPosition = MathHelper.clamp(this.cursorPosition, 0, i);
        this.setSelectionPos(this.cursorPosition);
    }

    public void setCursorPositionZero()
    {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd()
    {
        this.setCursorPosition(this.text.length());
    }

    public boolean textboxKeyTyped(char typedChar, int keyCode)
    {
        if (!this.isFocused)
        {
            return false;
        }
        else if (GuiScreen.isKeyComboCtrlA(keyCode))
        {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        }
        else if (GuiScreen.isKeyComboCtrlC(keyCode))
        {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        }
        else if (GuiScreen.isKeyComboCtrlV(keyCode))
        {
            if (this.isEnabled())
            {
                this.writeText(GuiScreen.getClipboardString());
            }

            return true;
        }
        else if (GuiScreen.isKeyComboCtrlX(keyCode))
        {
            GuiScreen.setClipboardString(this.getSelectedText());

            if (this.isEnabled())
            {
                this.writeText("");
            }

            return true;
        }
        else
        {
            switch (keyCode)
            {
            case Keyboard.KEY_BACK:

                if (GuiScreen.isCtrlKeyDown())
                {
                    if (this.isEnabled())
                    {
                        this.deleteWords(-1);
                    }
                }
                else if (this.isEnabled())
                {
                    this.deleteFromCursor(-1);
                }

                return true;
            case Keyboard.KEY_HOME:

                if (GuiScreen.isShiftKeyDown())
                {
                    this.setSelectionPos(0);
                }
                else
                {
                    this.setCursorPositionZero();
                }

                return true;
            case Keyboard.KEY_LEFT:

                if (GuiScreen.isShiftKeyDown())
                {
                    if (GuiScreen.isCtrlKeyDown())
                    {
                        this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                    }
                    else
                    {
                        this.setSelectionPos(this.getSelectionEnd() - 1);
                    }
                }
                else if (GuiScreen.isCtrlKeyDown())
                {
                    this.setCursorPosition(this.getNthWordFromCursor(-1));
                }
                else
                {
                    this.moveCursorBy(-1);
                }

                return true;
            case Keyboard.KEY_RIGHT:

                if (GuiScreen.isShiftKeyDown())
                {
                    if (GuiScreen.isCtrlKeyDown())
                    {
                        this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                    }
                    else
                    {
                        this.setSelectionPos(this.getSelectionEnd() + 1);
                    }
                }
                else if (GuiScreen.isCtrlKeyDown())
                {
                    this.setCursorPosition(this.getNthWordFromCursor(1));
                }
                else
                {
                    this.moveCursorBy(1);
                }

                return true;
            case Keyboard.KEY_END:

                if (GuiScreen.isShiftKeyDown())
                {
                    this.setSelectionPos(this.text.length());
                }
                else
                {
                    this.setCursorPositionEnd();
                }

                return true;
            case Keyboard.KEY_DELETE:

                if (GuiScreen.isCtrlKeyDown())
                {
                    if (this.isEnabled())
                    {
                        this.deleteWords(1);
                    }
                }
                else if (this.isEnabled())
                {
                    this.deleteFromCursor(1);
                }

                return true;
            default:

                if (ChatAllowedCharacters.isAllowedCharacter(typedChar))
                {
                    if (this.isEnabled())
                    {
                        this.writeText(Character.toString(typedChar));
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
    }

    public void drawTextBox(int mouseX, int mouseY)
    {
        if (this.isVisible())
        {
            if (this.getEnableBackgroundDrawing())
            {
                CacGuiUtils.drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1,
                        0xA0A0A0 | pScr.getAlphaInt(0xFF));
                CacGuiUtils.drawRect(this.x, this.y, this.x + this.width, this.y + this.height,
                        0x0 | pScr.getAlphaInt(0xFF));
            }

            int color = this.isEnabled() ? this.enabledColor | pScr.getAlphaInt(0xFF) :
                    this.disabledColor | pScr.getAlphaInt(0xFF);
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            int l = this.enableBackgroundDrawing ? this.x + 4 : this.x;
            int i1 = this.enableBackgroundDrawing ? this.y + (this.height - 8) / 2 : this.y;
            int j1 = l;

            if (k > s.length())
            {
                k = s.length();
            }

            if (!s.isEmpty())
            {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = this.fontRenderer.drawStringWithShadow(prefixTextFormatting + s1, (float) l, (float) i1, color);
            }

            boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int k1 = j1;

            if (!flag)
            {
                k1 = j > 0 ? l + this.width : l;
            }
            else if (flag2)
            {
                k1 = j1 - 1;
                --j1;
            }

            if (!s.isEmpty() && flag && j < s.length())
            {
                j1 = this.fontRenderer.drawStringWithShadow(prefixTextFormatting + s.substring(j), (float) j1,
                        (float) i1, color);
            }

            if (flag1)
            {
                if (flag2)
                {
                    CacGuiUtils.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT,
                            0xD0D0D0 | pScr.getAlphaInt(0xFF));
                }
                else
                {
                    this.fontRenderer.drawStringWithShadow(prefixTextFormatting + "_", (float) k1, (float) i1, color);
                }
            }

            if (k != j)
            {
                int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
                this.drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT);
            }
        }

        boolean hovered = isMouseInside();
        if (hovered && !this.tips.isEmpty())
        {
            GuiScreenAdvanced.drawHoveringTextS(this.tips, mouseX, mouseY, this.pScr);
        }
    }

    protected void drawSelectionBox(int startX, int startY, int endX, int endY)
    {
        if (startX < endX)
        {
            int i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY)
        {
            int j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > this.x + this.width)
        {
            endX = this.x + this.width;
        }

        if (startX > this.x + this.width)
        {
            startX = this.x + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.color(0.0F, 0.0F, 1F, 1F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double) startX, (double) endY, 0.0D).endVertex();
        bufferbuilder.pos((double) endX, (double) endY, 0.0D).endVertex();
        bufferbuilder.pos((double) endX, (double) startY, 0.0D).endVertex();
        bufferbuilder.pos((double) startX, (double) startY, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setMaxStringLength(int length)
    {
        this.maxStringLength = length;

        if (this.text.length() > length)
        {
            this.text = this.text.substring(0, length);
        }
    }

    public int getMaxStringLength()
    {
        return this.maxStringLength;
    }

    public int getCursorPosition()
    {
        return this.cursorPosition;
    }

    public boolean getEnableBackgroundDrawing()
    {
        return this.enableBackgroundDrawing;
    }

    public void setEnableBackgroundDrawing(boolean enableBackgroundDrawingIn)
    {
        this.enableBackgroundDrawing = enableBackgroundDrawingIn;
    }

    public void setTextColor(int color)
    {
        this.enabledColor = color;
    }

    public void setDisabledTextColour(int color)
    {
        this.disabledColor = color;
    }

    public void setFocused(boolean isFocusedIn)
    {
        if (isFocusedIn && !this.isFocused)
        {
            this.cursorCounter = 0;
        }

        this.isFocused = isFocusedIn;

        if (Minecraft.getMinecraft().currentScreen != null)
        {
            Minecraft.getMinecraft().currentScreen.setFocused(isFocusedIn);
        }
        if (!isFocusedIn)
        {
            cursorPosition = selectionEnd;
        }
    }

    public boolean isFocused()
    {
        return this.isFocused;
    }

    public int getSelectionEnd()
    {
        return this.selectionEnd;
    }

    public int getWidth()
    {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    public void setSelectionPos(int position)
    {
        int textLen = this.text.length();

        if (position > textLen)
        {
            position = textLen;
        }

        if (position < 0)
        {
            position = 0;
        }

        this.selectionEnd = position;

        if (this.fontRenderer != null)
        {
            if (this.lineScrollOffset > textLen)
            {
                this.lineScrollOffset = textLen;
            }

            int textBoxWidth = this.getWidth();
            String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), textBoxWidth);
            int k = s.length() + this.lineScrollOffset;

            if (position == this.lineScrollOffset)
            {
                if (GuiScreen.isShiftKeyDown() && (Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)))
                {
                    this.lineScrollOffset -= this.fontRenderer.trimStringToWidth(this.text, textBoxWidth, true).length() / 2;
                }
                else
                {
                    this.lineScrollOffset -= 1;
                }
            }

            if (position >= k)
            {
                this.lineScrollOffset += Math.max(1, position - k + 1);
            }
            else if (position <= this.lineScrollOffset)
            {
                this.lineScrollOffset -= this.lineScrollOffset - position;
            }
            if (s.isEmpty() || fontRenderer.getStringWidth(text) < textBoxWidth)
            {
                this.lineScrollOffset = 0;
            }
            this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, textLen - s.length());
        }
    }

    public void setCanLoseFocus(boolean canLoseFocusIn)
    {
        this.canLoseFocus = canLoseFocusIn;
    }

    public void incValue(int scroll)
    {
        float pitch = GuiScreen.isAltKeyDown() ? 2.0F : GuiScreen.isShiftKeyDown() ? 1.0F : 1.5F;
        Minecraft mc = pScr.mc;
        PositionedSoundRecord soundRecord = new PositionedSoundRecord(SoundEvents.BLOCK_NOTE_HAT.getSoundName(),
                SoundCategory.MASTER, 0.25f, pitch, false, 0, ISound.AttenuationType.NONE,
                (float) mc.player.posX, (float) mc.player.posY, (float) mc.player.posZ);
        this.pScr.mc.getSoundHandler().playSound(soundRecord);
    }

    public GuiTextFieldAdvanced addTips(String par1)
    {
        this.tips.add(par1);
        return this;
    }

    public void checkValue()
    {
    }

    public void checkValueAndSetText()
    {
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY)
    {
        super.onLeftClick(mouseX, mouseY);
        if (this.isEnabled() && this.isVisible() && this.canLoseFocus)
        {
            if (this.isMouseInside())
            {
                if (!this.canDragEdit)
                {
                    this.setFocused(true);
                    setCursorPositionRemake(mouseX, mouseY);
                }
            }
            else
            {
                this.setFocused(false);
            }
        }
    }

    @Override
    public void onLeftClickAndDrag(int mouseX, int mouseY, long timeSinceLastClick)
    {
        super.onLeftClickAndDrag(mouseX, mouseY, timeSinceLastClick);
        if (this.isEnabled() && this.isVisible() && isFocused())
        {
            setCursorPositionRemake(mouseX, mouseY);
        }
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.isEnabled() && this.isVisible() && canLoseFocus
                && this.isMouseInside() && this.isLastClickInside() && !isDragging() && canDragEdit)
        {
            this.setFocused(true);
            setCursorPositionRemake(mouseX, mouseY);
        }
        super.onMouseReleased(mouseX, mouseY, state);
    }

    public void setCursorPositionRemake(int mouseX, int mouseY)
    {
        int i = mouseX - (this.x + holder.shiftMouseX());
        int nextCharWidth;

        if (this.enableBackgroundDrawing)
        {
            i -= 4;
        }

        String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
        String clamped = this.fontRenderer.trimStringToWidth(s, i);
        String clamped2 = s.substring(clamped.length());
        char afterSelected = clamped2.isEmpty() ? ' ' : clamped2.charAt(0);
        nextCharWidth = fontRenderer.getCharWidth(afterSelected);
        int strlen = this.fontRenderer.trimStringToWidth(s, i).length();
        int strlen2 = this.fontRenderer.trimStringToWidth(s, i + nextCharWidth / 2).length();
        int pos = ((strlen2 - strlen > 1) ? strlen + 1 : strlen2) + this.lineScrollOffset;
        if (isDragging() || GuiScreen.isShiftKeyDown())
        {
            setSelectionPos(pos);
        }
        else
        {
            setCursorPosition(pos);
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        this.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.drawTextBox(mouseX, mouseY);
    }
}
