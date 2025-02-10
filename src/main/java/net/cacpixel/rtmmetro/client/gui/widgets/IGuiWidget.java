package net.cacpixel.rtmmetro.client.gui.widgets;

public interface IGuiWidget
{
    default IActionListener<? extends IGuiWidget> getListener() {return null;}

    default <T extends IGuiWidget> T setListener(IActionListener<T> listener) {return null;}

    default void onClick(int mouseX, int mouseY, int mouseButton) {}

    default void onDrag(int mouseX, int mouseY, int mouseButton) {}

    default void onScroll(int mouseX, int mouseY, int scroll) {}

    default void onKeyTyped(char typedChar, int keyCode) {}

    boolean isMouseInside();

    void draw(int mouseX, int mouseY, float partialTicks);

    boolean isVisible();

    boolean isEnabled();

    void setEnable(boolean enabled);

    void setVisible(boolean visible);

    int getX();

    int getY();

    int getWidth();

    int getHeight();
}
