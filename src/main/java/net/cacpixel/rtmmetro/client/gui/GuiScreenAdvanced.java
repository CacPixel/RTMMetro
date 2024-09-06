package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public abstract class GuiScreenAdvanced extends GuiScreenCustom
{
    private static int NEXT_FIELD_ID;

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.textFields.clear();
        NEXT_FIELD_ID = 0;
    }

    @Override
    protected GuiTextFieldAdvanced setTextField(int xPos, int yPos, int w, int h, String text)
    {
        GuiTextFieldAdvanced field = new GuiTextFieldAdvanced(NEXT_FIELD_ID++, this.fontRenderer, xPos, yPos, w, h,
                this);
        field.setMaxStringLength(32767);
        field.setFocused(false);
        field.setText(text);
        this.textFields.add(field);
        return field;
    }
}
