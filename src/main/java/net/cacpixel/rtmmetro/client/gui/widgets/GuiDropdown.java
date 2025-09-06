package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

public class GuiDropdown<T> extends GuiWidgetContainer implements IGuiWidgetWithOption<T>
{
    private final GuiScroll scroll;
    private final GuiTextFieldAdvanced field;
    private final GuiButtonAdvanced buttonExpand;
    //    private GuiOption<T> selectedOption;
    private ButtonOption<T> selectedOptionButton;
    private boolean optionListExpanded = false;
    public final IntSupplier xSupplierOriginal;
    public final IntSupplier ySupplierOriginal;
    public final IntSupplier widthSupplierOriginal;
    public final IntSupplier heightSupplierOriginal;

    public GuiDropdown(IWidgetHolder holder, IntSupplier xSupplier, IntSupplier ySupplier,
                       IntSupplier widthSupplier, IntSupplier heightSupplier)
    {
        // 传入的四个supplier指的是未展开时的textField加右边button的x y w h
        // GuiDropdown实际的xywh应该根据下拉框扩展的大小实时变更的
        // 所以，field，buttonExpand，scroll的supplier不应该去跟随this（GuiDropdown）的supplier
        super(holder, xSupplier, ySupplier, widthSupplier, heightSupplier);
        xSupplierOriginal = xSupplier;
        ySupplierOriginal = ySupplier;
        widthSupplierOriginal = widthSupplier;
        heightSupplierOriginal = heightSupplier;

        // 初始化外层的控件
        field = add(new GuiTextFieldAdvanced(this,
                ZERO,
                ZERO,
                () -> widthSupplierOriginal.getAsInt() - heightSupplierOriginal.getAsInt(),
                heightSupplierOriginal));
        buttonExpand = add(new GuiButtonAdvanced(this,
                () -> field.widthSupplier.getAsInt(),
                ZERO,
                heightSupplierOriginal,
                heightSupplierOriginal))
                .setAlignX(Align.LEFT_OR_UP_ALIGNED)
                .setListener(w -> this.setOptionListExpanded(!isOptionListExpanded()));
        scroll = add(new GuiScroll(this,
                ZERO,
                heightSupplierOriginal,
                widthSupplierOriginal,
                ZERO));
        scroll.heightSupplier = this::calcDropdownHeight;
        scroll.scrollLeftRight = false;
        scroll.setLayout(new GuiLayoutFlex(scroll).setFlow(GuiLayoutFlex.FlexFlow.COLUMN));
        this.setLayer(1);

        setOptionListExpanded(false);
    }

    public GuiDropdown(IWidgetHolder holder, int x, int y, int width, int height)
    {
        this(holder, GuiParam.from(x), GuiParam.from(y), GuiParam.from(width), GuiParam.from(height));
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (!this.isVisible()) {return;}
        this.drawBefore(mouseX, mouseY, partialTicks);
        getScreen().getScissorManager().disableAll();
        super.draw(mouseX, mouseY, partialTicks);
        getScreen().getScissorManager().enableAll();
        this.drawAfter(mouseX, mouseY, partialTicks);
    }

    public void drawBefore(int mouseX, int mouseY, float partialTicks)
    {

    }

    public void drawAfter(int mouseX, int mouseY, float partialTicks)
    {

    }

    @Override
    public void onScreenResize()
    {
        super.onScreenResize();
        setOptionListExpanded(false);
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY)
    {
        super.onLeftClick(mouseX, mouseY);
        // 待height增长做完后取消注释
//        if (this.isEnabled() && this.isVisible() && this.isMouseInside())
//        {
            if (isOptionListExpanded())
            {
                getScreen().setMousePassThrough(false);
            }
//        }
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button)
    {
        super.onClick(mouseX, mouseY, button);
        if (!this.isMouseInside())
        {
            setOptionListExpanded(false);
        }
    }

    @Override
    public void onScroll(int mouseX, int mouseY, int scroll)
    {
        super.onScroll(mouseX, mouseY, scroll);
        if (!this.isMouseInside())
        {
            setOptionListExpanded(false);
        }
    }

    private int calcDropdownHeight()
    {
        int height = getButtonOptionList().size() * heightSupplierOriginal.getAsInt();
        height = Math.min(height, getScreen().height - (this.y + this.height));
        return height;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends IGuiWidgetWithOption<T>> R addOptions(List<GuiOption<T>> option)
    {
        for (GuiOption<T> opt : option)
        {
            this.scroll.add(new ButtonOption<>(scroll, ZERO, ZERO, widthSupplierOriginal, heightSupplierOriginal, opt, this))
                    .setDisplayString(I18n.format(opt.getTranslationKey()));
        }
        return (R) this;
    }

    @Override
    public void setValueUpdated(boolean value)
    {
        super.setValueUpdated(value);
    }

    @Override
    public List<GuiOption<T>> getOptionList()
    {
        return getButtonOptionList().stream().map(it -> it.option).collect(Collectors.toList());
    }

    @Override
    public GuiOption<T> getSelectedOption()
    {
        return selectedOptionButton.option;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends IGuiWidgetWithOption<T>> R setSelectedOption(GuiOption<T> selectedOption)
    {
        getButtonOptionList().forEach(it -> it.selected = false);
        getButtonOptionList().stream().filter(it -> it.option.equals(selectedOption)).findFirst().ifPresent(it -> {
            this.selectedOptionButton = it;
            it.selected = true;
        });
        return (R) this;
    }

    public ButtonOption<T> getSelectedButton()
    {
        return selectedOptionButton;
    }

    @SuppressWarnings("unchecked")
    public <R extends IGuiWidgetWithOption<T>> R setSelectedButton(ButtonOption<T> button)
    {
        getButtonOptionList().forEach(it -> it.selected = false);
        this.selectedOptionButton = button;
        this.field.setText(I18n.format(button.option.getTranslationKey()));
        button.selected = true;
        return (R) this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<ButtonOption<T>> getButtonOptionList()
    {
        return (List) scroll.widgets; // 可以确信widgets里面只有ButtonOption。
    }

    public boolean isTextFieldEditable()
    {
        return field.isEditable();
    }

    public void setTextFieldEditable(boolean editable)
    {
        field.setEditable(editable);
    }

    public boolean isOptionListExpanded()
    {
        return optionListExpanded;
    }

    public void setOptionListExpanded(boolean optionListExpanded)
    {
        this.optionListExpanded = optionListExpanded;
        buttonExpand.setIcon(new Image(optionListExpanded ? ResourceConstants.ICON_UP : ResourceConstants.ICON_DOWN));
        scroll.setEnabled(optionListExpanded);
        scroll.setVisible(optionListExpanded);
    }

    public static class ButtonOption<T> extends GuiButtonAdvanced
    {
        public GuiOption<T> option;
        public boolean selected = false;
        public GuiDropdown<T> dropdown;

        public ButtonOption(IWidgetHolder holder, IntSupplier xSupplier, IntSupplier ySupplier, IntSupplier widthSupplier,
                            IntSupplier heightSupplier, GuiOption<T> option, GuiDropdown<T> dropdown)
        {
            super(holder, xSupplier, ySupplier, widthSupplier, heightSupplier);
            this.option = option;
            this.dropdown = dropdown;
            this.setListener(this::buttonListener);
            this.setAlignX(Align.LEFT_OR_UP_ALIGNED);
        }

        private void buttonListener(GuiWidget w)
        {
            if (w != this)
            {
                return;
            }
            this.dropdown.setSelectedButton(this);
            this.dropdown.setOptionListExpanded(false);
        }

        public GuiScroll getHolderScroll()
        {
            return (GuiScroll) getHolder();
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial)
        {
            super.drawButton(mc, mouseX, mouseY, partial);
        }
    }
}
