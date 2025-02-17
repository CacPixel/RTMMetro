package net.cacpixel.rtmmetro.client.gui.widgets;

import net.cacpixel.rtmmetro.client.gui.CacGuiUtils;
import net.cacpixel.rtmmetro.client.gui.GuiScreenAdvanced;

import java.util.function.IntSupplier;

public abstract class GuiWidget
{
    public GuiScreenAdvanced pScr;
    public IWidgetHolder holder;
    public int x;
    public int y;
    public int width;
    public int height;
    public float zLevel; // From Gui
    public int id;
    private boolean enabled = true;
    private boolean visible = true;
    private IActionListener<? extends GuiButtonAdvanced> listener;
    public IntSupplier xSupplier = this::getX;
    public IntSupplier ySupplier = this::getY;
    public IntSupplier widthSupplier = this::getWidth;
    public IntSupplier heightSupplier = this::getHeight;
    public static final IntSupplier ZERO = () -> 0;

    public GuiWidget(IWidgetHolder holder, int id, int x, int y, int width, int height)
    {
        this.holder = holder;
        this.pScr = holder.getScreen();
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public GuiWidget(IWidgetHolder holder, int id, IntSupplier xSupplier, IntSupplier ySupplier,
                     IntSupplier widthSupplier, IntSupplier heightSupplier)
    {
        this.holder = holder;
        this.pScr = holder.getScreen();
        this.id = id;
        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
        this.widthSupplier = widthSupplier;
        this.heightSupplier = heightSupplier;
        this.updatePosAndSize(this.getXSupplier().getAsInt(),
                this.getYSupplier().getAsInt(),
                this.getWidthSupplier().getAsInt(),
                this.getHeightSupplier().getAsInt());
    }

    public GuiWidget(GuiScreenAdvanced pScr, IntSupplier xSupplier, IntSupplier ySupplier,
                     IntSupplier widthSupplier, IntSupplier heightSupplier)
    {
        this(pScr, pScr.getNextWidgetId(), xSupplier, ySupplier, widthSupplier, heightSupplier);
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiWidget> T setListener(IActionListener<T> listener)
    {
        this.listener = (IActionListener<? extends GuiButtonAdvanced>) listener;
        return (T) this;
    }

    public IActionListener<? extends GuiWidget> getListener()
    {
        return listener;
    }

    public void onLeftClick(int mouseX, int mouseY)
    {
    }

    public void onRightClick(int mouseX, int mouseY)
    {
    }

    public void onMiddleClick(int mouseX, int mouseY)
    {
    }

    public void LeftClickAndDrag(int mouseX, int mouseY, int mouseButton)
    {
    }

    public void onScroll(int mouseX, int mouseY, int scroll)
    {
    }

    public void onKeyTyped(char typedChar, int keyCode)
    {
    }

    public void onWidgetUpdate()
    {
    }

    public boolean isMouseInside()
    {
        int dx = holder.shiftMouseX();
        int dy = holder.shiftMouseY();
        return CacGuiUtils.isMouseInside(x + dx, y + dy, width, height);
    }

    public abstract void draw(int mouseX, int mouseY, float partialTicks);

    public int getX() {return x;}

    public int getY() {return y;}

    public int getWidth() {return width;}

    public int getHeight() {return height;}

    public GuiWidget updatePosAndSize(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        return this;
    }

    public GuiWidget updatePosAndSize()
    {
        this.updatePosAndSize(this.getXSupplier().getAsInt(),
                this.getYSupplier().getAsInt(),
                this.getWidthSupplier().getAsInt(),
                this.getHeightSupplier().getAsInt());
        return this;
    }

    public IntSupplier getXSupplier()
    {
        return this.xSupplier;
    }

    public IntSupplier getYSupplier()
    {
        return this.ySupplier;
    }

    public IntSupplier getWidthSupplier()
    {
        return this.widthSupplier;
    }

    public IntSupplier getHeightSupplier()
    {
        return this.heightSupplier;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiWidget> T setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return (T) this;
    }

    public boolean isVisible()
    {
        return visible;
    }

    @SuppressWarnings("unchecked")
    public <T extends GuiWidget> T setVisible(boolean visible)
    {
        this.visible = visible;
        return (T) this;
    }

    public boolean isPositionIndependent()
    {
        return x <= 0 && y <= 0 && width <= 0 && height <= 0;
    }
}
