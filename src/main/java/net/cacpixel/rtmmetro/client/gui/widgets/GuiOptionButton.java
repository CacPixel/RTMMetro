package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiOption;
import net.cacpixel.rtmmetro.client.gui.GuiParam;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

public class GuiOptionButton<T> extends GuiButtonAdvanced
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
        if (this.isEnabled() && this.isVisible() && this.isMouseInside())
        {
            this.rollOptions();
        }
    }

    public GuiOption<T> getNextOption()
    {
        int currIndex = options.indexOf(this.selectedOption);
        currIndex = (currIndex >= this.options.size() - 1) ? 0 : currIndex + 1;
        return this.options.get(currIndex);
    }

    public GuiOption<T> getPrevOption()
    {
        int currIndex = options.indexOf(this.selectedOption);
        currIndex = (currIndex == 0) ? this.options.size() - 1 : currIndex - 1;
        return this.options.get(currIndex);
    }

    public List<GuiOption<T>> getAllOptions()
    {
        return this.options;
    }

    public GuiOptionButton<T> addOptions(List<GuiOption<T>> option)
    {
        this.options.addAll(option);
        return this;
    }

    public GuiOptionButton<T> addRawOptions(List<T> option)
    {
        return addOptions(option.stream().map(it -> new GuiOption<>(it.toString(), it)).collect(Collectors.toList()));
    }

    @SafeVarargs
    public final GuiOptionButton<T> addRawOptions(T... option)
    {
        return addRawOptions(Arrays.stream(option).collect(Collectors.toList()));
    }

    public GuiOption<T> getSelectedOption()
    {
        return this.selectedOption;
    }

    public T getSelectedRawOption()
    {
        return this.selectedOption.obj;
    }

    public GuiOptionButton<T> setSelectedOption(GuiOption<T> selectedOption)
    {
        this.selectedOption = selectedOption;
        this.displayString = this.prefix + I18n.format(selectedOption.getTranslationKey());
        return this;
    }

    public GuiOptionButton<T> setSelectedOption(T t)
    {
        options.stream().filter(it -> t.equals(it.obj)).findFirst().ifPresent(this::setSelectedOption);
        return this;
    }

    public void rollOptions()
    {
        this.setSelectedOption(this.getNextOption());
        this.setValueUpdated(true);
    }
}
