package net.cacpixel.rtmmetro.client.gui.screens;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenWindowed;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiCheckBoxAdvanced;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiTextFieldAdvancedInt;
import net.cacpixel.rtmmetro.client.gui.widgets.WidgetFactory;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.DoubleConsumer;

@SideOnly(Side.CLIENT)
public class GuiCalculateCant extends GuiScreenWindowed
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
        this.fieldSpeed = WidgetFactory.addTextField(this, hw, hh - 50, fieldWidth, fieldHeight, 80, 10, 1000, false);
        //radius
        this.fieldRadius = WidgetFactory.addTextField(this, hw, hh - 30, fieldWidth, fieldHeight, 500, 10, 10000, false);
        //gauge
        this.fieldGauge = WidgetFactory.addTextField(this, hw, hh - 10, fieldWidth, fieldHeight, 1435, 500, 3000, false);
        //reversed
        this.checkBoxFlip = WidgetFactory.addCheckBox(this, hw, hh + 10, fieldWidth, fieldHeight, "Flip", false, null);
        //ok
        this.buttonOK = WidgetFactory.addButton(this, hw - 80 + 90, hh + 70, 160, 20, I18n.format("gui.done"), () -> {
            if (!(Float.isNaN(cant) || Float.isInfinite(cant)))
                this.consumer.accept(this.cant);
            this.displayPrevScreen();
        });
        //cancel
        this.buttonCancel = WidgetFactory.addButton(this, hw - 80 - 90, hh + 70, 160, 20, I18n.format("gui.cancel"), () -> {
            this.displayPrevScreen();
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (parentScreen != null)
        {
            parentScreen.drawScreen(mouseX, mouseY, partialTicks);
        }
        this.drawScreenBefore(mouseX, mouseY, partialTicks);
        int hw = this.width / 2;
        int hh = this.height / 2;
        int fontColor = 0xE0E0E0 | this.getAlphaInt(0xFF);
        this.drawDefaultBackground(hw - 250, hh - 150, hw + 250, hh + 150);
        super.drawScreen(mouseX, mouseY, partialTicks);
        addedHeight = getAddHeight(fieldSpeed.fieldValue, fieldRadius.fieldValue);
        cant = getCantValue(fieldGauge.fieldValue, addedHeight);
        cant = this.checkBoxFlip.isChecked() ? -cant : cant;
        String colorPrefix = addedHeight > 0.150F ? TextFormatting.RED.toString() : TextFormatting.GREEN.toString();
        this.drawCenteredString(this.fontRenderer, TextFormatting.BOLD + "Calculate Rail Cant", hw, hh - 80 + 4, fontColor);
        CacGuiUtils.drawRightAlignedString(this.fontRenderer, "Speed (km/h)", hw - 5, hh - 50 + 4, fontColor);
        CacGuiUtils.drawRightAlignedString(this.fontRenderer, "Curve Radius (m)", hw - 5, hh - 30 + 4, fontColor);
        CacGuiUtils.drawRightAlignedString(this.fontRenderer, "Rail Gauge (mm)", hw - 5, hh - 10 + 4, fontColor);
        if (Float.isNaN(cant) || Float.isInfinite(cant))
        {
            this.buttonOK.enabled = false;
            this.drawCenteredString(this.fontRenderer,
                    TextFormatting.RED + "Cant Value is bad.", hw, hh + 30,
                    fontColor);
        }
        else
        {
            this.buttonOK.enabled = true;
            this.drawCenteredString(this.fontRenderer,
                    TextFormatting.RESET + "Cant Value: " + colorPrefix + TextFormatting.UNDERLINE + String.format("%.3f", cant), hw,
                    hh + 30, fontColor);
        }
        this.drawCenteredString(this.fontRenderer,
                "Outer rail will raise " + colorPrefix + TextFormatting.UNDERLINE + String.format("%.1f", addedHeight * 1000.0F) +
                        TextFormatting.RESET + "mm.", hw, hh + 45, fontColor);
        this.drawScreenAfter(mouseX, mouseY, partialTicks);
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
