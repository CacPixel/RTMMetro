package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;
import net.cacpixel.rtmmetro.util.ITranslatable;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiOptionButton<E extends Enum<E>> extends GuiButtonAdvanced implements IGuiWidget
{
    private final List<E> options = new ArrayList<>();
    private E selectedOption;
    public String prefix;

    public GuiOptionButton(int id, int xPos, int yPos, String prefix, E[] values, E initVal,
                           GuiScreenAdvanced pScr, IActionListener callback)
    {
        this(id, xPos, yPos, 200, 20, prefix, values, initVal, pScr, callback);
    }

    public GuiOptionButton(int id, int xPos, int yPos, int width, int height, String prefix, E[] values, E initVal,
                           GuiScreenAdvanced pScr, IActionListener callback)
    {
        super(id, xPos, yPos, width, height, prefix, pScr, callback);
        this.prefix = prefix;
        this.options.addAll(Arrays.asList(values));
        Arrays.stream(values).forEach(v -> this.options.set(v.ordinal(), v));
        this.setSelectedOption(initVal);
    }

    public String getTranslationKey(int ordinal)
    {
        E e = options.get(ordinal);
        if (e instanceof ITranslatable)
        {
            return ((ITranslatable<?>) e).getTranslateKey();
        }
        else
        {
            return e.name();
        }
    }

    public E getNextOption()
    {
        int currIndex = this.selectedOption.ordinal();
        currIndex = (currIndex >= this.options.size() - 1) ? 0 : currIndex + 1;
        return this.options.get(currIndex);
    }

    public E getPrevOption()
    {
        int currIndex = this.selectedOption.ordinal();
        currIndex = (currIndex == 0) ? this.options.size() - 1 : currIndex - 1;
        return this.options.get(currIndex);
    }

    public List<E> getAllOptions()
    {
        return this.options;
    }

    public E getSelectedOption()
    {
        return this.selectedOption;
    }

    public void setSelectedOption(E selectedOption)
    {
        this.selectedOption = selectedOption;
        this.displayString = this.prefix + I18n.format(this.getTranslationKey(this.selectedOption.ordinal()));
    }

    public void rollOptions()
    {
        this.setSelectedOption(this.getNextOption());
        this.pScr.hasValueUpdated = true;
    }
}
