package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiTextFieldAdvanced extends GuiTextField implements IGuiWidget
{
    protected final GuiScreenAdvanced pScr;
    private final List<String> tips = new ArrayList<>();

    public GuiTextFieldAdvanced(int id, FontRenderer par1, int x, int y, int w, int h, GuiScreenAdvanced pScr)
    {
        super(id, par1, x, y, w, h);
        this.pScr = pScr;
    }

    public boolean isValueValid()
    {
        return true;
    }

    @Override
    public boolean textboxKeyTyped(char word, int code)
    {
        return super.textboxKeyTyped(word, code);
    }

    public void drawTextBox(int mouseX, int mouseY)
    {
        this.drawTextBox();

        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if (hovered && !this.tips.isEmpty())
        {
            GuiScreenAdvanced.drawHoveringTextS(this.tips, mouseX, mouseY, this.pScr);
        }
    }

    public void checkValueAndSetText()
    {
        this.checkValue();
    }

    public void incValue(int scroll)
    {
        float pitch = GuiScreen.isAltKeyDown() ? 2.0F : GuiScreen.isShiftKeyDown() ? 1.0F : 1.5F;
        this.pScr.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_HAT, pitch));
    }

    public GuiTextFieldAdvanced addTips(String par1)
    {
        this.tips.add(par1);
        return this;
    }

    public void checkValue()
    {

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
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
//        GlStateManager.disableBlend();
    }

    public void drawTextBox()
    {
        if (this.getVisible())
        {
            if (this.getEnableBackgroundDrawing())
            {
                drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, 0xA0A0A0 | pScr.getAlphaInt(0xFF));
                drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x0 | pScr.getAlphaInt(0xFF));
            }

            int color = this.isEnabled ? this.enabledColor | pScr.getAlphaInt(0xFF) : this.disabledColor | pScr.getAlphaInt(0xFF);
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
                j1 = this.fontRenderer.drawStringWithShadow(s1, (float) l, (float) i1, color);
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
                j1 = this.fontRenderer.drawStringWithShadow(s.substring(j), (float) j1, (float) i1, color);
            }

            if (flag1)
            {
                if (flag2)
                {
                    drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT, 0xD0D0D0 | pScr.getAlphaInt(0xFF));
                }
                else
                {
                    this.fontRenderer.drawStringWithShadow("_", (float) k1, (float) i1, color);
                }
            }

            if (k != j)
            {
                int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
                this.drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT);
            }
        }
    }

    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isEnabled() && this.isVisible())
            this.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onDrag(int mouseX, int mouseY, int mouseButton)
    {
        // TODO: mouse drag to select on Text field
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        this.textboxKeyTyped(typedChar, keyCode);
    }

    public boolean isMouseInside()
    {
        return CacGuiUtils.isMouseInside(x, y, width, height);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.drawTextBox();
    }

    @Override
    public boolean isVisible()
    {
        return getVisible();
    }

    @Override
    public boolean isEnabled()
    {
        return this.isEnabled;
    }

    @Override
    public void setEnable(boolean enabled)
    {
        super.setEnabled(enabled);
    }
}
