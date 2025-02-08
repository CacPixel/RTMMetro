package net.cacpixel.rtmmetro.client.gui.widgets;

public interface IGuiWidget
{
    default IActionListener getListener() {return null;}

    default void setListener(IActionListener listener) {}

    default void onClick(int mouseX, int mouseY, int mouseButton) {}

    default void onDrag(int mouseX, int mouseY, int mouseButton) {}

    default void onScroll(int mouseX, int mouseY, int scroll) {}

    default void onKeyTyped(char typedChar, int keyCode) {}

    boolean isMouseInside();

    void draw(int mouseX, int mouseY, float partialTicks);

    boolean isVisible();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    void setVisible(boolean visible);

}
