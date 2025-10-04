package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.GuiOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface IGuiWidgetWithOption<T>
{
    List<GuiOption<T>> getOptionList();

    GuiOption<T> getSelectedOption();

    <R extends IGuiWidgetWithOption<T>> R setSelectedOption(GuiOption<T> selectedOption);

    void setValueUpdated(boolean value);

    default GuiOption<T> getNextOption()
    {
        int currIndex = getOptionList().indexOf(getSelectedOption());
        currIndex = (currIndex >= this.getOptionList().size() - 1) ? 0 : currIndex + 1;
        return this.getOptionList().get(currIndex);
    }

    default GuiOption<T> getPrevOption()
    {
        int currIndex = getOptionList().indexOf(getSelectedOption());
        currIndex = (currIndex == 0) ? this.getOptionList().size() - 1 : currIndex - 1;
        return this.getOptionList().get(currIndex);
    }

    @SuppressWarnings("unchecked")
    default <R extends IGuiWidgetWithOption<T>> R addOptions(List<GuiOption<T>> option)
    {
        this.getOptionList().addAll(option);
        return (R) this;
    }

    default <R extends IGuiWidgetWithOption<T>> R addRawOptions(List<T> option)
    {
        return addOptions(option.stream().map(it -> new GuiOption<>(it.toString(), it)).collect(Collectors.toList()));
    }

    @SuppressWarnings("unchecked")
    default <R extends IGuiWidgetWithOption<T>> R addRawOptions(T... option)
    {
        return addRawOptions(Arrays.stream(option).collect(Collectors.toList()));
    }

    default T getSelectedRawOption()
    {
        return getSelectedOption().obj;
    }

    @SuppressWarnings("unchecked")
    default <R extends IGuiWidgetWithOption<T>> R setSelectedOption(T t)
    {
        getOptionList().stream().filter(it -> t.equals(it.obj)).findFirst().ifPresent(this::setSelectedOption);
        return (R) this;
    }

    default void rollOptions()
    {
        this.setSelectedOption(this.getNextOption());
        this.setValueUpdated(true);
    }

    default int getOptionIndex(GuiOption<T> option)
    {
        return getOptionList().indexOf(option);
    }

    default int getSelectedOptionIndex()
    {
        return getOptionIndex(getSelectedOption());
    }
}
