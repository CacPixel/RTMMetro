package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.util.ITranslatable;
import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class GuiOptionButton<E extends Enum<E>> extends GuiButtonAdvanced
{
    private final List<E> options = new ArrayList<>();
    private E selectedOption;

    public GuiOptionButton(int id, int xPos, int yPos, String displayString, E[] values,
                           GuiScreenAdvanced pScr, Consumer<? super GuiButton> callback)
    {
        this(id, xPos, yPos, 200, 20, displayString, values, pScr, callback);
    }

    public GuiOptionButton(int id, int xPos, int yPos, int width, int height, String displayString, E[] values,
                           GuiScreenAdvanced pScr, Consumer<? super GuiButton> callback)
    {
        super(id, xPos, yPos, width, height, displayString, pScr, callback);
        this.options.addAll(Arrays.asList(values));
        Arrays.stream(values).forEach(v -> this.options.set(v.ordinal(), v));
        this.selectedOption = values[0];
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
    }
}
