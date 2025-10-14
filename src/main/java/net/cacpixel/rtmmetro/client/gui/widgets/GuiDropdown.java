package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

public class GuiDropdown<T> extends GuiWidgetContainer implements IGuiWidgetWithOption<T>
{
    private final DropdownScroll scroll;
    private final DropdownTextField field;
    private final DropdownButton buttonExpand;
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
        field = add(new DropdownTextField(this,
                ZERO,
                ZERO,
                () -> widthSupplierOriginal.getAsInt() - heightSupplierOriginal.getAsInt(),
                heightSupplierOriginal));
        buttonExpand = add(new DropdownButton(this,
                () -> field.widthSupplier.getAsInt(),
                ZERO,
                heightSupplierOriginal,
                heightSupplierOriginal))
                .setAlignX(Align.LEFT_OR_UP_ALIGNED)
                .setListener(w -> this.setOptionListExpanded(!isOptionListExpanded()));
        scroll = add(new DropdownScroll(this,
                ZERO,
                ZERO,
                ZERO,
                ZERO));
        scroll.xSupplier = this::calcScrollX;
        scroll.ySupplier = this::calcScrollY;
        scroll.widthSupplier = this::calcScrollWidth;
        scroll.heightSupplier = this::calcScrollHeight;
        scroll.scrollLeftRight = false;
        scroll.setLayout(new GuiLayoutFlex(scroll).setFlow(GuiLayoutFlex.FlexFlow.COLUMN));
        this.setLayer(1);
        this.heightSupplier = () -> {
            if (this.optionListExpanded)
            {
                return heightSupplier.getAsInt() + this.scroll.heightSupplier.getAsInt();
            }
            else
            {
                return heightSupplier.getAsInt();
            }
        };
        setOptionListExpanded(false);
        getEventClick().setEventPass(false);
    }

    public GuiDropdown(IWidgetHolder holder, int x, int y, int width, int height)
    {
        this(holder, GuiParam.from(x), GuiParam.from(y), GuiParam.from(width), GuiParam.from(height));
    }

    private int calcScrollX()
    {
        return 0;
    }

    private int calcScrollY()
    {
        return heightSupplierOriginal.getAsInt(); // todo up popup when can not fit
    }

    private int calcScrollWidth()
    {
        return widthSupplierOriginal.getAsInt(); // todo wide popup if text is longer
    }

    private int calcScrollHeight()
    {
        int height = getButtonOptionList().size() * heightSupplierOriginal.getAsInt();
//        height = Math.min(height, getScreen().height - (this.y + this.height)); // todo min size
        return height;
    }

    @Override
    public ScissorParam getMouseInteractJudgeScissorParam()
    {
        return new ScissorParam(getXOfScreen(), getYOfScreen(), getHolderWidth(), getHolderHeight()).setPushOrigin(true);
    }

    @Override
    public void onMakeLayoutStart()
    {
        super.onMakeLayoutStart();
        setOptionListExpanded(false);
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button)
    {
        super.onClick(mouseX, mouseY, button);
        if (!this.getEventClick().canInteract())
        {
            setOptionListExpanded(false);
        }
    }

    @Override
    public void onScroll(int mouseX, int mouseY, int scroll)
    {
        super.onScroll(mouseX, mouseY, scroll);
        if (!this.scroll.getEventScroll().canInteract())
        {
            setOptionListExpanded(false);
        }
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
        this.updateSize();
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
            getEventClick().setEventPass(true);
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

        @Override
        public void onLeftClick(int mouseX, int mouseY)
        {
            if (dropdown.isOptionListExpanded())
            {
                super.onLeftClick(mouseX, mouseY);
            }
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial)
        {
            super.drawButton(mc, mouseX, mouseY, partial);
        }
    }

    public static class DropdownScroll extends GuiScroll
    {
        public DropdownScroll(IWidgetHolder holder, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height)
        {
            super(holder, x, y, width, height);
            getEventClick().setEventPass(true);
            xButton.getEventClick().setEventPass(true);
            yButton.getEventClick().setEventPass(true);
        }

        @Override
        public void doScissorBefore()
        {
            ScreenScissorManager screenScissorManager = this.getScreen().getScreenScissorManager();
            ScissorParam param = new ScissorParam(getXOfScreen(), getYOfScreen(), getHolderWidth(), getHolderHeight()).setPushOrigin(true);
            screenScissorManager.push(param);
            screenScissorManager.apply();
        }

        @Override
        public ScissorParam getMouseInteractJudgeScissorParam()
        {
            return new ScissorParam(getXOfScreen(), getYOfScreen(), getHolderWidth(), getHolderHeight()).setPushOrigin(true);
        }
    }

    public static class DropdownButton extends GuiButtonAdvanced
    {

        public DropdownButton(IWidgetHolder holder, IntSupplier xSupplier, IntSupplier ySupplier, IntSupplier widthSupplier,
                              IntSupplier heightSupplier)
        {
            super(holder, xSupplier, ySupplier, widthSupplier, heightSupplier);
            getEventClick().setEventPass(true);
        }
    }

    public static class DropdownTextField extends GuiTextFieldAdvanced
    {

        public DropdownTextField(IWidgetHolder holder, IntSupplier xSupplier, IntSupplier ySupplier, IntSupplier widthSupplier,
                                 IntSupplier heightSupplier)
        {
            super(holder, xSupplier, ySupplier, widthSupplier, heightSupplier);
            getEventClick().setEventPass(false);
        }
    }
}
