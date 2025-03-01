package net.cacpixel.rtmmetro.client.gui.screens;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.cacpixel.rtmmetro.client.gui.GuiScreenWindowed;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiButtonAdvanced;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiCheckBoxAdvanced;
import net.cacpixel.rtmmetro.client.gui.widgets.GuiTextFieldAdvancedInt;
import net.cacpixel.rtmmetro.client.gui.widgets.WidgetFactory;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.DoubleConsumer;

@SideOnly(Side.CLIENT)
public class GuiCalculateCant extends GuiScreenWindowed
{
    public static int speed = 80;
    public static int radius = 600;
    public static int gauge = 1435;
    private final DoubleConsumer consumer;
    private GuiTextFieldAdvancedInt fieldSpeed;
    private GuiTextFieldAdvancedInt fieldRadius;
    private GuiTextFieldAdvancedInt fieldGauge;
    private GuiButtonAdvanced buttonOK;
    private GuiCheckBoxAdvanced checkBoxFlip;
    private float addedHeight;
    private float cant;

    public GuiCalculateCant(GuiScreenAdvanced parentScreen, DoubleConsumer consumer)
    {
        super();
        this.parentScreen = parentScreen;
        this.consumer = consumer;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.labelTitle.setText(TextFormatting.BOLD + "Calculate Rail Cant");
        int fieldWidth = 75;
        int fieldHeight = 16;
        //speed
        this.fieldSpeed = WidgetFactory.addTextField(this, () -> this.getHalfWidth(), () -> this.getHalfHeight() - 50,
                () -> fieldWidth,
                () -> fieldHeight, speed, 10, 1000, false);
        //radius
        this.fieldRadius = WidgetFactory.addTextField(this, () -> this.getHalfWidth(), () -> this.getHalfHeight() - 30,
                () -> fieldWidth,
                () -> fieldHeight, radius, 10, 10000, false);
        //gauge
        this.fieldGauge = WidgetFactory.addTextField(this, () -> this.getHalfWidth(), () -> this.getHalfHeight() - 10,
                () -> fieldWidth,
                () -> fieldHeight, gauge, 500, 3000, false);
        //reversed
        this.checkBoxFlip = WidgetFactory.addCheckBox(this, () -> this.getHalfWidth(), () -> this.getHalfHeight() + 10,
                "Flip", false);
        //ok
        this.buttonOK = WidgetFactory.addButton(this, () -> this.getHalfWidth() - 40,
                () -> this.getHalfHeight() + 70, () -> 80,
                () -> 20, "Set value").setListener((w) -> {
            if (!(Float.isNaN(cant) || Float.isInfinite(cant))) this.consumer.accept(this.cant);
            this.displayPrevScreen();
            speed = fieldSpeed.fieldValue;
            radius = fieldRadius.fieldValue;
            gauge = fieldGauge.fieldValue;
        });
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.drawScreenBefore(mouseX, mouseY, partialTicks);
        int hw = this.getHalfWidth();
        int hh = this.getHalfHeight();
        int fontColor = 0xE0E0E0 | this.getAlphaInt(0xFF);
        this.drawDefaultBackground();
        super.draw(mouseX, mouseY, partialTicks);
        addedHeight = getAddHeight(fieldSpeed.fieldValue, fieldRadius.fieldValue);
        cant = getCantValue(fieldGauge.fieldValue, addedHeight);
        cant = this.checkBoxFlip.isChecked() ? -cant : cant;
        String colorPrefix = addedHeight > 0.150F ? TextFormatting.RED.toString() : TextFormatting.GREEN.toString();
//        this.drawCenteredString(this.fontRenderer, TextFormatting.BOLD + "Calculate Rail Cant", hw, hh - 80 + 4,
//                fontColor);
        CacGuiUtils.drawRightAlignedString(this.fontRenderer, "Speed (km/h)", hw - 5, hh - 50 + 4, fontColor);
        CacGuiUtils.drawRightAlignedString(this.fontRenderer, "Curve Radius (m)", hw - 5, hh - 30 + 4, fontColor);
        CacGuiUtils.drawRightAlignedString(this.fontRenderer, "Rail Gauge (mm)", hw - 5, hh - 10 + 4, fontColor);
        if (Float.isNaN(cant) || Float.isInfinite(cant))
        {
            this.buttonOK.setEnabled(false);
            this.drawCenteredString(this.fontRenderer, TextFormatting.RED + "Cant Value is bad.", hw, hh + 30,
                    fontColor);
        }
        else
        {
            this.buttonOK.setEnabled(true);
            this.drawCenteredString(this.fontRenderer,
                    TextFormatting.RESET + "Cant Value: " + colorPrefix + TextFormatting.UNDERLINE +
                            String.format("%.3f", cant), hw,
                    hh + 30, fontColor);
        }
        this.drawCenteredString(this.fontRenderer,
                "Outer rail will raise " + colorPrefix + TextFormatting.UNDERLINE +
                        String.format("%.1f", addedHeight * 1000.0F) +
                        TextFormatting.RESET + "mm.", hw, hh + 45, fontColor);
        this.drawScreenAfter(mouseX, mouseY, partialTicks);
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
