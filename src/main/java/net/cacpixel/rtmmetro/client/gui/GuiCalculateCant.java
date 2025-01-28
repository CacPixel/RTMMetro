package net.cacpixel.rtmmetro.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.DoubleConsumer;

@SideOnly(Side.CLIENT)
public class GuiCalculateCant extends GuiScreenAdvanced
{
    private final DoubleConsumer consumer;
    private GuiTextFieldAdvancedInt fieldSpeed;
    private GuiTextFieldAdvancedInt fieldRadius;
    private GuiTextFieldAdvancedInt fieldGauge;
    private GuiButton buttonOK;
    private GuiButton buttonCancel;
    private GuiCheckBoxAdvanced checkBoxFlip;
    private float addedHeight;
    private float cant;

    public GuiCalculateCant(GuiScreen parentScreen, DoubleConsumer consumer)
    {
        super();
        this.parentScreen = parentScreen;
        this.consumer = consumer;
    }

    @Override
    public void initGui()
    {
        parentScreen.initGui();
        super.initGui();
        int hw = this.width / 2;
        int hh = this.height / 2;
        int fieldWidth = 75;
        int fieldHeight = 16;
        //speed
        this.fieldSpeed = this.setTextField(hw, hh - 50, fieldWidth, fieldHeight, 80, 10, 1000, false);
        //radius
        this.fieldRadius = this.setTextField(hw, hh - 30, fieldWidth, fieldHeight, 500, 10, 10000, false);
        //gauge
        this.fieldGauge = this.setTextField(hw, hh - 10, fieldWidth, fieldHeight, 1435, 500, 3000, false);
        //reversed
        this.checkBoxFlip = this.addCheckBox(hw, hh + 10, fieldWidth, fieldHeight, "Flip", false, button -> {});
        //ok
        this.buttonOK = this.addButton(hw - 80 + 90, hh + 70, 160, 20, I18n.format("gui.done"), b -> {
            if (!(Float.isNaN(cant) || Float.isInfinite(cant)))
                this.consumer.accept(this.cant);
            this.displayPrevScreen();
        });
        //cancel
        this.buttonCancel = this.addButton(hw - 80 - 90, hh + 70, 160, 20, I18n.format("gui.cancel"), b -> {
            this.displayPrevScreen();
        });
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        int hw = this.width / 2;
        int hh = this.height / 2;
        parentScreen.drawScreen(par1, par2, par3);
        this.drawGradientRect(hw - 250, hh - 150, hw + 250, hh + 150, 0xC0101010, 0xD0101010);
        super.drawScreen(par1, par2, par3);
        addedHeight = getAddHeight(fieldSpeed.fieldValue, fieldRadius.fieldValue);
        cant = getCantValue(fieldGauge.fieldValue, addedHeight);
        cant = this.checkBoxFlip.isChecked() ? -cant : cant;
        String colorPrefix = addedHeight > 0.150F ? TextFormatting.RED.toString() : TextFormatting.GREEN.toString();
        this.drawCenteredString(this.fontRenderer, TextFormatting.BOLD + "Calculate Rail Cant", hw, hh - 80 + 4, 0xFFFFFF);
        this.drawRightAlignedString(this.fontRenderer, "Speed (km/h)", hw - 5, hh - 50 + 4, 0xFFFFFF);
        this.drawRightAlignedString(this.fontRenderer, "Curve Radius (m)", hw - 5, hh - 30 + 4, 0xFFFFFF);
        this.drawRightAlignedString(this.fontRenderer, "Rail Gauge (mm)", hw - 5, hh - 10 + 4, 0xFFFFFF);
        if (Float.isNaN(cant) || Float.isInfinite(cant))
        {
            this.buttonOK.enabled = false;
            this.drawCenteredString(this.fontRenderer,
                    TextFormatting.RED + "Cant Value is bad.", hw, hh + 30,
                    0xFFFFFF);
        }
        else
        {
            this.buttonOK.enabled = true;
            this.drawCenteredString(this.fontRenderer,
                    TextFormatting.RESET + "Cant Value: " + colorPrefix + TextFormatting.UNDERLINE + String.format("%.3f", cant), hw,
                    hh + 30, 0xFFFFFF);
        }
        this.drawCenteredString(this.fontRenderer,
                "Outer rail will add " + colorPrefix + TextFormatting.UNDERLINE + String.format("%.1f", addedHeight * 1000.0F) +
                        TextFormatting.RESET + "mm height.", hw, hh + 45, 0xFFFFFF);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    public static float getAddHeight(int maxSpeed, int curveRadius)
    {
        return ((11.8F * maxSpeed * maxSpeed) / curveRadius) / 1000.0F;
    }

    public static float getCantValue(int gauge, float height)
    {
        return (float) Math.toDegrees(Math.asin(height / (gauge / 1000.0F)));
    }

    public float getCant()
    {
        return cant;
    }
}
