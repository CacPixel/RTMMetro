package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiTextFieldCustom;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class GuiTextFieldAdvanced extends GuiTextFieldCustom
{
    protected final GuiScreen pScr;
    public static final int DEFAULT_SCROLL_VALUE = 120;

    public GuiTextFieldAdvanced(int id, FontRenderer par1, int x, int y, int w, int h, GuiScreen pScr)
    {
        super(id, par1, x, y, w, h, pScr);
        this.pScr = pScr;
    }

    public void handleMouseInput()
    {

    }

    public void handleKeyboardInput()
    {
        this.setScrValueUpdated();
    }

    public boolean isValueValid()
    {
        return true;
    }

    // NoSuchFieldError guiResponder with optifine in dev env
    public boolean isMouseInside()
    {
        Minecraft mc = NGTUtilClient.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int mouseX = Mouse.getX() / sr.getScaleFactor();
        int mouseY = (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor();
        return this.x < mouseX && mouseX < (this.x + this.width)
                && this.y < mouseY && mouseY < (this.y + this.height);
    }

    @Override
    public boolean textboxKeyTyped(char word, int code)
    {
        boolean ret = super.textboxKeyTyped(word, code);
        this.setScrValueUpdated();
        return ret;
    }

    public void setScrValueUpdated()
    {
        if (this.pScr instanceof GuiScreenAdvanced)
        {
            ((GuiScreenAdvanced) this.pScr).hasValueUpdated = true;
        }
    }
}
