package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiOption;
import net.cacpixel.rtmmetro.client.gui.GuiParam;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class GuiOptionButton<T> extends GuiButtonAdvanced implements IGuiWidgetWithOption<T>
{
    private final List<GuiOption<T>> options = new ArrayList<>();
    private GuiOption<T> selectedOption;
    public String prefix;

    public GuiOptionButton(IWidgetHolder holder, IntSupplier xSupplier, IntSupplier ySupplier,
                           IntSupplier widthSupplier, IntSupplier heightSupplier, String prefix)
    {
        super(holder, xSupplier, ySupplier, widthSupplier, heightSupplier);
        this.prefix = prefix;
    }

    public GuiOptionButton(IWidgetHolder holder, int x, int y,
                           int width, int height, String prefix)
    {
        super(holder, GuiParam.from(x), GuiParam.from(y), GuiParam.from(width), GuiParam.from(height));
        this.prefix = prefix;
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY)
    {
        super.onLeftClick(mouseX, mouseY);
        if (this.isEnabled() && this.isVisible() && this.getEventClick().canInteract())
        {
            this.rollOptions();
        }
    }

    @Override
    public GuiOption<T> getSelectedOption()
    {
        return this.selectedOption;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends IGuiWidgetWithOption<T>> R setSelectedOption(GuiOption<T> selectedOption)
    {
        this.selectedOption = selectedOption;
        this.displayString = this.prefix + I18n.format(selectedOption.getTranslationKey());
        return (R) this;
    }

    @Override
    public List<GuiOption<T>> getOptionList()
    {
        return options;
    }

    @Override
    public void setValueUpdated(boolean hasValueUpdated)
    {
        super.setValueUpdated(hasValueUpdated);
    }
}
