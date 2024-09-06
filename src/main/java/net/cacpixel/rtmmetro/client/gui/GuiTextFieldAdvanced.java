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
    public GuiTextFieldAdvanced(int id, FontRenderer par1, int x, int y, int w, int h, GuiScreen pScr)
    {
        super(id, par1, x, y, w, h, pScr);
    }

    public void handleMouseInput()
    {
        int i = Mouse.getEventDWheel();
        if (this.isMouseInside())
        {
            if (i > 0)
            {
                float num = GUIHelper.getFieldValueFloat(this, 0.0f);
                num += 0.100000000f;
                this.setText(String.valueOf(num));
            }
            else if (i < 0)
            {
                float num = GUIHelper.getFieldValueFloat(this, 0.0f);
                num -= 0.100000000f;
                this.setText(String.valueOf(num));
            }
        }

    }

    // NoSuchFieldError guiResponder with optifine in dev env
    public boolean isMouseInside()
    {
        Minecraft mc = NGTUtilClient.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int mouseX = Mouse.getX() / sr.getScaleFactor();
        int mouseY = (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor();

//        ModLog.debug("mouseX = %d, mouseY = %d, x = %d, y = %d, width = %d, height = %d",
//                mouseX, mouseY, this.x, this.y, this.width, this.height);
        return this.x < mouseX && mouseX < (this.x + this.width)
                && this.y < mouseY && mouseY < (this.y + this.height);
    }
}
